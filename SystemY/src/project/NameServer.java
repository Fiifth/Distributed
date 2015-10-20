package project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry;


public class NameServer extends UnicastRemoteObject implements NameServerInterface{
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException{
		try{
			System.setProperty("java.security.policy","file:$git/Distributed/SystemY/bin/project/security.policy");
			System.setProperty("java.rmi.server.codebase","file:$git/Distributed/SystemY/bin/project/NameServer.class");
			LocateRegistry.createRegistry(1099);
			NameServerInterface nameint = new NameServer();
			Naming.rebind("//localhost/NameServer", nameint);
			
			System.out.println("NameServer is ready.");
			}
			catch(Exception e)
			{
			System.out.println("NameServer error: " + e.getMessage());
			e.printStackTrace();
			}
		MulticastSocket multicastSocket =null;
		
		InetAddress group = InetAddress.getByName("228.5.6.7");
		multicastSocket = new MulticastSocket(6789);
		multicastSocket.joinGroup(group);
		NameServer nameserver = new NameServer();
		byte[] buffer = new byte[10];
		for(int i=0; i< 2;i++)	//receive 3 messages
		 {					

			DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			
			multicastSocket.receive(messageIn);
			String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
			
			if (i==0)
			{
				System.out.println("Received:" + new String(msg));
			}
			else if (i==1)
			{
				InetAddress addr = InetAddress.getByName(msg);
				System.out.println("Received:" + addr.getLocalHost());
				String nodeIP = addr.getLocalHost().toString();
				nameserver.addNode(addr.getHostName(),nodeIP);
			}
			
			//TODO antwoorden aan opstartende node (hoeveel nodes in netwerk) 
			//TODO ontvangen antwoord server (ignore eerste 2 messages) (tcp?)
			//TODO fixen vorige node data verwerken
		}
		multicastSocket.close();
	}

	protected NameServer() throws RemoteException 
	{
		super();
	}

	TreeMap<Integer,String> nodeMap = new TreeMap<Integer,String>();
	

	public void addNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
    	nodeMap.put(hashedNN,nodeIP);
    	

    	System.out.print("CURRENT MAP");
    	for (Entry<Integer, String> entry : nodeMap.entrySet()) 
    	{
    	     System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
    	}
    	System.out.print("***");
	}

	public void rmNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		nodeMap.remove(hashedNN);
	}

	public TreeMap<Integer, String> showList() throws RemoteException {
		// TODO Auto-generated method stub
		return (TreeMap<Integer, String>) nodeMap;
	}
	
	public String locateFile(String filename)throws RemoteException
	{
		int hashedFN = Math.abs(filename.hashCode()%32768);
		int destinationKey=nodeMap.lowerKey(hashedFN);
		if (destinationKey==0) destinationKey=nodeMap.lastKey();
		return nodeMap.get(destinationKey);
		
	}
}