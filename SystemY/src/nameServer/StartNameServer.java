package nameServer;


import java.io.IOException;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry;

import networkFunctions.Multicast;
import networkFunctions.RMI;

public class StartNameServer extends UnicastRemoteObject implements NameServerInterface
{
	RMI rmi=new RMI();
	
	private TreeMap<Integer,String> nodeMap = new TreeMap<Integer,String>();
	private static final long serialVersionUID = 1L;
	Multicast multi=new Multicast("228.5.6.7", 6789);

	public StartNameServer() throws RemoteException 
	{
		super();
	}
	
	public void startNameServer() throws IOException
	{
		multi.joinMulticastGroup();
		StartNameServer nameServer=this;
		NameServerInterface nameint = nameServer;	
		rmi.bindObjectRMI(1099, "localhost", "NameServer", nameint);
		NameServerNodeDetection nameservernodedetection=new NameServerNodeDetection(nameServer,multi);
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
	
	public String[] locateFile(String filename)throws RemoteException
	{
		//TODO change String to String[]
		String[] toSend=new String[2];
		int destinationKey=0;
		int hashedFN = Math.abs(filename.hashCode()%32768);
		if(nodeMap.lowerKey(hashedFN)==null)
		{
			destinationKey=0;
		}
		else
			destinationKey=nodeMap.lowerKey(hashedFN);
		if (destinationKey==0) destinationKey=nodeMap.lastKey();
		
		toSend[0]=nodeMap.get(destinationKey);
		toSend[1]=Integer.toString(destinationKey);

		return toSend;		
	}

	public TreeMap<Integer, String> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(TreeMap<Integer, String> nodeMap) {
		this.nodeMap = nodeMap;
	}
	
	public void thisNodeFails(int failingNodeID){
		int prevNodeID;
		String prevNodeIP;
		int nextNodeID;
		String nextNodeIP;
		if(nodeMap.lowerKey(failingNodeID) == null){
			prevNodeID = nodeMap.lastKey();
			prevNodeIP = nodeMap.get(prevNodeID);
		}
		else{
			prevNodeID = nodeMap.lowerKey(failingNodeID);
			prevNodeIP = nodeMap.get(prevNodeID);
		}
		if(nodeMap.higherKey(failingNodeID)==null){
			nextNodeID = nodeMap.firstKey();
			nextNodeIP = nodeMap.get(nextNodeID);
		}
		else{
			nextNodeID = nodeMap.higherKey(failingNodeID);
			nextNodeIP = nodeMap.get(nextNodeID);
		}
		String text="1"+"-"+failingNodeID+"-"+prevNodeID+"-"+nextNodeID+"-"+prevNodeIP+"-"+nextNodeIP;
		multi.sendMulticast(text);
		
	}
}