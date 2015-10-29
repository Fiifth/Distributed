package project;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

public class ReceiveQueueThread extends UnicastRemoteObject implements ReceiveQueueThreadInterface, Runnable 
{

	private static final long serialVersionUID = 1L;
	Queue<String> myQueue = new LinkedList<String>();
	
	public ReceiveQueueThread() throws RemoteException
	{
		super();
	}
	
	public void run() 
	{
		setUpRMI();
		
		while(true)
		{
			if (myQueue.peek()!=null)
			{
				//String ip=myQueue.poll();
				//TODO receive file from IP by setting up TCP connection
			}
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
