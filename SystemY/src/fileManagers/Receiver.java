package fileManagers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import nodeP.NodeData;

public class Receiver implements Runnable 
{

		NodeData nodedata1;
	
	public Receiver(NodeData nodedata1)
	{
		super();
		this.nodedata1=nodedata1;
	}
	
	public void run() 
	{
		while(nodedata1.getToLeave()==0)
		{
			FileData file1=null;
			try {
				file1=nodedata1.receiveQueue.take();//Wait until entry is found
			} catch (InterruptedException e) {System.out.println("interupted while waiting for queue entry");}
			
			receiveFile(file1,nodedata1.getMyReplFolder());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
        
        int serverPort = file1.getReplicateOwnerID()+32768;
        String fileOutput = DirReplFiles+"\\"+file1.getFileName();
        System.out.println("receiveing file: "+fileOutput);
        //TODO before adding check if the file is present in the list already
        file1.setFolderLocation(DirReplFiles);
        nodedata1.replFiles.add(file1);
 
        try 
        {
        	System.out.println("looking for server");
            clientSocket = new Socket(file1.getLocalOwnerIP(), serverPort);	
            is = clientSocket.getInputStream();
        } 
        catch (IOException ex) {System.out.println("couldn't open socket");}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) 
        {
        	System.out.println("Server found, receiving file");
           
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
