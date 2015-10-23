package project;
import java.net.*;
import java.io.*;

public class Node 
{
	
	public static void main(String[] args)throws Exception
	{
		//TODO bij ontvangen multicast nieuwe node: update next/previous node
		MulticastSocket multicastSocket =null;
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		BufferedOutputStream outToClient = null;
		try 
		{	
			InetAddress mijnIP0=InetAddress.getLocalHost();
			String naam="Node1";
			String mijnIP= mijnIP0.getHostAddress();
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(group);
			byte [] m1 = naam.getBytes();
 			byte [] m2 = mijnIP.getBytes(); 			
			DatagramPacket messageOut1 = new DatagramPacket(m1, m1.length, group, 6789);
			DatagramPacket messageOut2 = new DatagramPacket(m2, m2.length, group, 6789);
			
			multicastSocket.send(messageOut1);	
			multicastSocket.send(messageOut2);
			
			byte[] buffer = new byte[1000];
			
 			for(int i=0; i< 2;i++)	//receive 3 messages
			 {		
				// get messages from others in group
 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				multicastSocket.receive(messageIn);
 				String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
 				System.out.println("Received:" + msg);
  		}
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
		//serverIPstring = inFromNameServer.readLine();
		amountOfNodes = inFromNameServer.readLine();
		System.out.println("amount of Nodes: " + amountOfNodes);
		serverIP=connectionSocket.getInetAddress();
		String serverIPstring=serverIP.getHostAddress();
		System.out.println("serverIP: " + serverIPstring);
		welcomeSocket.close();
		connectionSocket.close();
	}

}
