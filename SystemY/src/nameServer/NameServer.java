package nameServer;


import java.io.IOException;
import java.net.DatagramPacket;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry;

import neworkFunctions.Multicast;
import neworkFunctions.RMI;

public class NameServer extends UnicastRemoteObject implements NameServerInterface
{
	RMI rmi=new RMI();
	
	private TreeMap<Integer,String> nodeMap = new TreeMap<Integer,String>();
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) throws IOException
	{
		NameServer nameServer=new NameServer();
		nameServer.startNameServer(nameServer);
	}

	protected NameServer() throws RemoteException 
	{
		super();
	}
	
	public void startNameServer(NameServer nameServer) throws IOException
	{
		NameServerInterface nameint = nameServer;	
		rmi.bindObjectRMI(1099, "localhost", "NameServer", nameint);
		NameServerNodeDetection nameservernodedetection=new NameServerNodeDetection(nameServer);
		nameservernodedetection.start();
	}

	
	public boolean addNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		if (!nodeMap.containsKey(hashedNN))
		{
	    	nodeMap.put(hashedNN,nodeIP);
	    	
	    	System.out.println("************");
	    	System.out.println("CURRENT MAP:");
	    	for (Entry<Integer, String> entry : nodeMap.entrySet()) 
	    	{
	    	     System.out.println("Key: " + entry.getKey() + ", NodeIP: " + entry.getValue());
	    	}
	    	System.out.println("************");
	    	return true;
		}
		else
			System.out.println("This node name already exists");
		return false;
	}

	public void rmNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		nodeMap.remove(hashedNN);
		System.out.println("************");
    	System.out.println("CURRENT MAP:");
    	for (Entry<Integer, String> entry : nodeMap.entrySet()) 
    	{
    	     System.out.println("Key: " + entry.getKey() + ", NodeIP: " + entry.getValue());
    	}
    	System.out.println("************");
	}

	public TreeMap<Integer, String> showList() throws RemoteException {

		return (TreeMap<Integer, String>) nodeMap;
	}
	
	public String locateFile(String filename)throws RemoteException
	{
		int destinationKey=0;
		int hashedFN = Math.abs(filename.hashCode()%32768);
		if(nodeMap.lowerKey(hashedFN)==null)
		{
			destinationKey=0;
		}
		else
			destinationKey=nodeMap.lowerKey(hashedFN);
		if (destinationKey==0) destinationKey=nodeMap.lastKey();
		
		String toSend=nodeMap.get(destinationKey)+"-"+destinationKey;

		return (toSend);		
	}

	public TreeMap<Integer, String> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(TreeMap<Integer, String> nodeMap) {
		this.nodeMap = nodeMap;
	}
}