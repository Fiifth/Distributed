package fileManagers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import nodeP.NodeData;

public class FileReceiverT extends UnicastRemoteObject implements FileReceiverInt, Runnable 
{

	private static final long serialVersionUID = 1L;
	public static BlockingQueue<FileData> myQueue=new ArrayBlockingQueue<FileData>(500);
	NodeData nodedata1;
	
	public FileReceiverT(NodeData nodedata1) throws RemoteException
	{
		super();
		this.nodedata1=nodedata1;
	}
	
	public void run() 
	{
		setUpRMI(nodedata1);
		
		while(true)
		{
			String ipAndNameAndDir = null;
			FileData file1=null;
			try {
				file1=myQueue.take();//Wait until entry is found
			} catch (InterruptedException e) {System.out.println("interupted while waiting for queue entry");}
			receiveFile(file1);
			
			}
		}
	
	private void setUpRMI(NodeData nodedata1) 
	{
			try{
				LocateRegistry.createRegistry(nodedata1.getMyNodeID());
				FileReceiverInt RecInt = new FileReceiverT(nodedata1);
				Naming.rebind("//localhost:"+nodedata1.getMyNodeID()+"/FileReceiverT", RecInt);
				System.out.println("ReceiveQueueThreadRMI is ready.");
				}
				catch(Exception e){System.out.println("couldn't start RMI");}
	}

	public boolean addIP(FileData file1) throws RemoteException 
	{
		boolean queue=myQueue.offer(file1);
		return queue;
	}

	public void receiveFile(FileData file1)
	{
		
		FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket clientSocket = null;
        InputStream is = null;
		byte[] aByte = new byte[1];
        int bytesRead;
        String DirReplFiles="c:\\SystemYNodeFilesRep";
		//TODO change dir to personalised node map 
        int serverPort = 3248;
        String fileOutput = DirReplFiles+"\\"+file1.getFileName();
        System.out.println("receiveing file: "+fileOutput);
        //TODO before adding check if the file is present in the list already
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
        	System.out.println("Server found, recieving file");
           
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
