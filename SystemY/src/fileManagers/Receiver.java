package fileManagers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import nodeP.NodeData;

public class Receiver extends Thread 
{

		NodeData nodedata1;
	
	public Receiver(NodeData nodedata1)
	{
		super();
		this.nodedata1=nodedata1;
	}
	
	public void run() 
	{
		while(true)
		{
			FileData file1=null;
			try {
				file1=nodedata1.receiveQueue.take();//Wait until entry is found
			} catch (InterruptedException e) {return;}
			
			receiveFile(file1,nodedata1.getMyReplFolder());
			}
		}
	
	public void receiveFile(FileData file1, String DirReplFiles)
	{
		
		FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket clientSocket = null;
        InputStream is = null;
		byte[] aByte = new byte[1];
        int bytesRead;
        
        int serverPort = file1.getSourceID()+32768;
        String fileOutput = DirReplFiles+"\\"+file1.getFileName();
        System.out.println("receiveing file: "+fileOutput);
        
        //check if filename already exists, if not add to dir
        boolean fnExists = false;
        for (FileData tempfile : nodedata1.replFiles) 
    	{
        	if(tempfile.getFileName().equals(file1.getFileName()))
        	{
        		fnExists = true;
       		}
    	}
        
        if(!fnExists){
        	file1.setFolderLocation(DirReplFiles);
        	file1.setRemoveAfterSend(false);
        	nodedata1.replFiles.add(file1);
        }
 

        	System.out.println("looking for server");
            try {
				clientSocket = new Socket(file1.getSourceIP(), serverPort);
				is = clientSocket.getInputStream();
			} catch (IOException e) {System.out.println("couldn't open socket");}	
           

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) 
        {
        	System.out.print("Server found: ");
           
            try 
            {
                fos = new FileOutputStream(fileOutput);
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(aByte, 0, aByte.length);
                do 
                {
                        baos.write(aByte);
                        bytesRead = is.read(aByte);
                } while (bytesRead != -1);

                bos.write(baos.toByteArray());
                bos.flush();
                bos.close();
                clientSocket.close();
                System.out.println("File received");
            } 
            catch (IOException ex) {System.out.println("File not found or error sending it");}
        }
    }


	
}
