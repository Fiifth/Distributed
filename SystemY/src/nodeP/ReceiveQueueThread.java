package nodeP;

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

public class ReceiveQueueThread extends UnicastRemoteObject implements ReceiveQueueThreadInterface, Runnable 
{

	private static final long serialVersionUID = 1L;
	public static BlockingQueue<String> myQueue=new ArrayBlockingQueue<String>(500);
	NodeData nodedata1;
	
	public ReceiveQueueThread(NodeData nodedata1) throws RemoteException
	{
		super();
		this.nodedata1=nodedata1;
	}
	/*public static void main(String args[]) throws RemoteException
	{
		
		ReceiveQueueThread receiveQueueThread=new ReceiveQueueThread();
		receiveQueueThread.run();
	}*/
	
	public void run() 
	{
		setUpRMI(nodedata1);
		
		while(true)
		{
			String ipAndName = null;
			try {
				ipAndName=myQueue.take();
			} catch (InterruptedException e) {e.printStackTrace();}
			receiveFile(ipAndName);
			nodedata1.replFiles.add(ipAndName);
			}
		}
	
	private void setUpRMI(NodeData nodedata1) 
	{
			try{
				System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/Distributed/SystemY/bin/nodeP/ReceiveQueueThread.class");

				LocateRegistry.createRegistry(nodedata1.getMyNodeID());
				ReceiveQueueThreadInterface RecInt = new ReceiveQueueThread(nodedata1);
				System.out.println(nodedata1.getMyNodeID());
				Naming.rebind("//localhost:"+nodedata1.getMyNodeID()+"/ReceiveQueueThread", RecInt);
				
				System.out.println("ReceiveQueueThreadRMI is ready.");
				}
				catch(Exception e)
				{
				System.out.println("ReceiveQueueThreadRMI error: " + e.getMessage());
				e.printStackTrace();
				}
	}

	public boolean addIP(String ip) throws RemoteException 
	{
		boolean queue=myQueue.offer(ip);
		System.out.println("addedSomething");
		return queue;
	}

	public void receiveFile(String ipAndName)
	{
		String[] ipAndNameArray = ipAndName.split("-");
		byte[] aByte = new byte[1];
        int bytesRead;
        String serverIP = ipAndNameArray[0];
        int serverPort = 3248;
        String fileOutput = "C:\\"+ipAndNameArray[1];
        Socket clientSocket = null;
        InputStream is = null;
        
        try 
        {
        	System.out.println("looking for server");
        	//nieuwe socket om te communiceren met server
            clientSocket = new Socket(serverIP, serverPort);	
            is = clientSocket.getInputStream();
        } 
        catch (IOException ex) {System.out.println("I/O error");}
        
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
            catch (IOException ex) {System.out.println("I/O error2");}
        }
    }
}
