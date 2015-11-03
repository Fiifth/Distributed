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
	public static BlockingQueue<String> myQueue=new ArrayBlockingQueue<String>(500);
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
			try {
				ipAndNameAndDir=myQueue.take();
				System.out.println("new entry in queue found(ip-name-dir):"+ipAndNameAndDir );
			} catch (InterruptedException e) {System.out.println("interupted while waiting for queue entry");}
			receiveFile(ipAndNameAndDir);
			nodedata1.replFiles.add(ipAndNameAndDir);
			}
		}
	
	private void setUpRMI(NodeData nodedata1) 
	{
			try{
				LocateRegistry.createRegistry(nodedata1.getMyNodeID());
				FileReceiverInt RecInt = new FileReceiverT(nodedata1);
				Naming.rebind("//localhost:"+nodedata1.getMyNodeID()+"/ReceiveQueueThread", RecInt);
				System.out.println("ReceiveQueueThreadRMI is ready.");
				}
				catch(Exception e){System.out.println("couldn't start RMI");}
	}

	public boolean addIP(String ip) throws RemoteException 
	{
		boolean queue=myQueue.offer(ip);
		return queue;
	}

	public void receiveFile(String ipAndNameAndDir)
	{
		String[] ipAndNameAndDirArray = ipAndNameAndDir.split("-");
		byte[] aByte = new byte[1];
        int bytesRead;
        String serverIP = ipAndNameAndDirArray[0];
        int serverPort = 3248;
        String fileOutput = "c:\\SystemYNodeFilesRep\\"+ipAndNameAndDirArray[1];
        System.out.println("receiveing file: "+fileOutput);
        Socket clientSocket = null;
        InputStream is = null;
        
        try 
        {
        	System.out.println("looking for server");
        	//nieuwe socket om te communiceren met server
            clientSocket = new Socket(serverIP, serverPort);	
            is = clientSocket.getInputStream();
        } 
        catch (IOException ex) {System.out.println("couldn't open socket");}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) 
        {
        	System.out.println("Server found, recieving file");
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
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
