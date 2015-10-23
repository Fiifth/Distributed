package project;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.Map.Entry;


public class NameServer extends UnicastRemoteObject implements NameServerInterface{
	private static final long serialVersionUID = 1L;
	
	//@SuppressWarnings("static-access")
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
				System.out.println("NodeIP:" + addr.getLocalHost());
				String nodeIP = addr.getLocalHost().toString();
				nameserver.addNode(addr.getHostName(),nodeIP);
				
			}
			//TODO fixen vorige node data verwerken 
		}
		multicastSocket.close();
		//Send serverIP to newly joined Node
		//String serverIP = InetAddress.getLocalHost().getHostAddress();
		Integer numberOfNodes = nameserver.nodeMap.size(); //includes the new node
		String numOfNodesString = numberOfNodes.toString();
		Socket clientSocket = new Socket("localhost",6789);
		DataOutputStream outToNode = new DataOutputStream(clientSocket.getOutputStream());
		//outToNode.writeBytes(serverIP + "\n");
		outToNode.writeBytes(numOfNodesString + "\n");
		clientSocket.close();
	}

	protected NameServer() throws RemoteException 
	{
		super();
	}

	TreeMap<Integer,String> nodeMap = new TreeMap<Integer,String>();
	

	public void addNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
    	nodeMap.put(hashedNN,nodeIP);
    	

    	System.out.println("CURRENT MAP");
    	for (Entry<Integer, String> entry : nodeMap.entrySet()) 
    	{
    	     System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
    	}
    	System.out.println("***");
	}

	public void rmNode(String nodeName, String nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		nodeMap.remove(hashedNN);
	}

	public TreeMap<Integer, String> showList() throws RemoteException {

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