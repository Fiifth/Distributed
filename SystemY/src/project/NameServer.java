package project;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry; 


public class NameServer extends UnicastRemoteObject implements NameServerInterface{
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		try{
			System.setProperty("java.security.policy","file:$git/Distributed/SystemY/bin/project/security.policy");
			System.setProperty("java.rmi.server.codebase","file:$git/Distributed/SystemY/bin/project/NameServer.class");
			
			LocateRegistry.createRegistry(1099);
			NameServerInterface nameint = new NameServer();
			Naming.rebind("//localhost/NameServer", nameint);
			//NameServer nameserverobj = new NameServer();
			//Naming.rebind("//localhost/NameServer", nameserverobj);
			
			System.out.println("NameServer is ready.");
		}catch(Exception e){
			System.out.println("NameServer error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected NameServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	//HashMap<Integer,InetAddress> nodeMap = new HashMap<Integer,InetAddress>();
	TreeMap<Integer,InetAddress> nodeMap = new TreeMap<Integer,InetAddress>();
	

	public void addNode(String nodeName, InetAddress nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
    	nodeMap.put(hashedNN,nodeIP);
    	

    	System.out.print("CURRENT MAP");
    	for (Entry<Integer, InetAddress> entry : nodeMap.entrySet()) 
    	{
    	     System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
    	}
    	System.out.print("***");
	}

	public void rmNode(String nodeName, InetAddress nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		nodeMap.remove(hashedNN);
	}

	public TreeMap<Integer, InetAddress> showList() throws RemoteException {
		// TODO Auto-generated method stub
		return (TreeMap<Integer, InetAddress>) nodeMap;
	}
	
	public InetAddress locateFile(String filename)throws RemoteException
	{
		int hashedFN = Math.abs(filename.hashCode()%32768);
		int destinationKey=nodeMap.lowerKey(hashedFN);
		if (destinationKey==0) destinationKey=nodeMap.lastKey();
		return nodeMap.get(destinationKey);
		
	}
}