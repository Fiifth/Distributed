package nameServer;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry;

public class NameServer extends UnicastRemoteObject implements NameServerInterface
{
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
		
		
		nameServer.setUpRMI(nameServer);
		
		MulticastSocket multicastSocket =null;
		InetAddress group = InetAddress.getByName("228.5.6.7");
		multicastSocket = new MulticastSocket(6789);
		multicastSocket.joinGroup(group);
		
		while(true)
		{
			byte[] buffer = new byte[100];
			DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			multicastSocket.receive(messageIn);//blocks
			//message = 0-nodeName or 1-nodename-prevnode-nextnode
			//NameServerThread fixes add or remove
			NameServerThread c =new NameServerThread(messageIn,nameServer);
			c.start(); 
			
		}
		//multicastSocket.close();
	}

	public void setUpRMI(NameServer nameServer)
	{
		try{
			LocateRegistry.createRegistry(1099);
			NameServerInterface nameint = nameServer;
			Naming.rebind("//localhost/NameServer", nameint);
			
			System.out.println("NameServer is ready.");
			}
			catch(Exception e)
			{
			System.out.println("NameServer error: " + e.getMessage());
			e.printStackTrace();
			}
	}
	

	public void addNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
    	nodeMap.put(hashedNN,nodeIP);
    	
    	System.out.println("************");
    	System.out.println("CURRENT MAP:");
    	for (Entry<Integer, String> entry : nodeMap.entrySet()) 
    	{
    	     System.out.println("Key: " + entry.getKey() + ", NodeIP: " + entry.getValue());
    	}
    	System.out.println("************");
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
		System.out.println(hashedFN);

		if(nodeMap.lowerKey(hashedFN)==null)
		{
			destinationKey=0;
		}
		else
			destinationKey=nodeMap.lowerKey(hashedFN);
		System.out.println(destinationKey);
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