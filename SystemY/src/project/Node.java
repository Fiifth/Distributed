package project;
import java.net.*;
import java.io.*;

public class Node 
{	
	public String nodeNaam;
	public String prevNode;
	public String nextNode;
	
	public static void main(String[] args)throws Exception
	{
		//TODO bij ontvangen multicast nieuwe node: update next/previous node
		MulticastSocket multicastSocket =null;
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		
		try 
		{	
			String naam="Node1";
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(group);
			byte [] m1 = naam.getBytes();
			DatagramPacket messageOut1 = new DatagramPacket(m1, m1.length, group, 6789);
			
			multicastSocket.send(messageOut1);	
			
			multicastSocket.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(multicastSocket != null) multicastSocket.close();}
		
		//set up TCP socket to receive IPaddress from server and # nodes
		InetAddress serverIP;
		String amountOfNodes;
		welcomeSocket = new ServerSocket(6789);
		connectionSocket = welcomeSocket.accept();
		BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		amountOfNodes = inFromNameServer.readLine();
		System.out.println("amount of Nodes: " + amountOfNodes);
		serverIP=connectionSocket.getInetAddress();
		String serverIPstring=serverIP.getHostAddress();
		System.out.println("serverIP: " + serverIPstring);
		welcomeSocket.close();
		connectionSocket.close();
	}

}
