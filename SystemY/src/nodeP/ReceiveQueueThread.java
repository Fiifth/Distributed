package nodeP;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ReceiveQueueThread extends UnicastRemoteObject implements ReceiveQueueThreadInterface, Runnable 
{

	private static final long serialVersionUID = 1L;
	public volatile BlockingQueue<String> myQueue=new ArrayBlockingQueue<String>(500);
	
	public ReceiveQueueThread() throws RemoteException
	{
		super();
	}
	
	public void run() 
	{
		setUpRMI();
		
		while(true)
		{
			try {
				String ip=myQueue.take();
			} catch (InterruptedException e) {e.printStackTrace();}
				
				//TODO receive file from IP by setting up TCP connection
			}
		}
	
	private void setUpRMI() 
	{
			try{
				System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/Distributed/SystemY/bin/project/NameServer.class");
				
				LocateRegistry.createRegistry(2000);
				ReceiveQueueThreadInterface RecInt = (ReceiveQueueThreadInterface) new ReceiveQueueThread();
				Naming.rebind("//localhost/ReceiveQueueThread", RecInt);
				
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
		return queue;
	}

	
}
