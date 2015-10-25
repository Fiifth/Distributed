package project;
import java.net.*;
import java.io.*;

public class Node 
{	
	public static String nodeName;
	public static String prevNode;
	public static String nextNode;
	public static String MyNodeID;
	public static String nameServerIP;
	
	public static void main(String[] args)throws Exception
	{
		nodeName="Node3";
		//TODO MyNodeID bepaalen door hash toe tepassen op deze naam door gwn da stukje code van nameserver hier ook te zette
		MyNodeID= String.valueOf(Math.abs(nodeName.hashCode()%32768));
		System.out.println("MyNodeID:" + MyNodeID);
		
		int numberOfNodes;
		
		sendMulticast(nodeName);
		numberOfNodes=getNameServerRespons();
		if (numberOfNodes>1)
		{
			//TODO we krijgen de volgende node in de cirkel van de vorige node in de cirkel. 
			//De ID van de node die dit geeft moet dus nog opgehaald worden op vervolgens op te slagen als previous node
			// ik dacht aan gewoon RMI te gebruiken door de IP te gebruiken om de ID op te zoeken in de map
			nextNode=getNextNode();
			prevNode="RMIlookupIguess";
		}
		else
		{
			 prevNode=MyNodeID;
			 nextNode=MyNodeID;
		}
		
		MulticastSocket multicastSocket =null;
		InetAddress group = InetAddress.getByName("228.5.6.7");
		multicastSocket = new MulticastSocket(6789);
		multicastSocket.joinGroup(group);
		
		while(true)
		{
		byte[] buffer = new byte[100];
		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
		multicastSocket.receive(messageIn);//blocks
		//start thread
		NodeThread c =new NodeThread(messageIn,nextNode, prevNode, MyNodeID);
		c.start();               
		}
		//TODO uiteindelijk moet het luistere naar de multicast om dan threads op te starten ook in een thread komen anders kunde nooit een node late stoppen
		//TODO er moet ook een thread gemaakt worden die luistert naar nodes die weg willen gaan 
		//1) Stuur de id van de volgende node door naar de vorige node
		//2) In de vorige node wordt de volgende node aangepast met deze info
		//3) Stuur de id van de vorige node op naar de volgende node
		//4) In de volgende node wordt de vorige node aangepast met deze info
		//5) Verwijder de node bij de nameserver
	}
	
	private static String getNextNode() 
	{
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		//InetAddress nodeInet;
		String nextNode = null;
		
		try {
			welcomeSocket = new ServerSocket(6770);
			connectionSocket = welcomeSocket.accept();
			welcomeSocket.close();
			BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			nextNode = inFromNameServer.readLine();
			
			//nodeInet=connectionSocket.getInetAddress();
			//NodeIP=serverIP.getHostAddress();
			//System.out.println("serverIP: " + nameServerIP);
			
			connectionSocket.close();
		} 
		catch (IOException e) {e.printStackTrace();	}
		return nextNode;
		
	}

	public static void sendMulticast(String name)
	{
		MulticastSocket multicastSocket =null;
		byte [] m1 = name.getBytes();
		try 
		{	
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(group);
			DatagramPacket messageOut1 = new DatagramPacket(m1, m1.length, group, 6789);
			multicastSocket.send(messageOut1);	
			multicastSocket.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(multicastSocket != null) multicastSocket.close();}
	}
	
	public static int getNameServerRespons()
	{
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		InetAddress serverIP;
		int nodes=0;
		
		try {
			welcomeSocket = new ServerSocket(6790);
			connectionSocket = welcomeSocket.accept();
			welcomeSocket.close();
			BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String amountOfNodes = inFromNameServer.readLine();
			nodes=Integer.parseInt(amountOfNodes);
			System.out.println("amount of Nodes: " + amountOfNodes);
			serverIP=connectionSocket.getInetAddress();
			nameServerIP=serverIP.getHostAddress();
			System.out.println("serverIP: " + nameServerIP);
			
			connectionSocket.close();
		} 
		catch (IOException e) {e.printStackTrace();	}
		return nodes;
	}

}
