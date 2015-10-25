package project;
import java.net.*;
import java.io.*;
//TODO bij ontvangen multicast nieuwe node: update next/previous node
public class Node 
{	
	public static String nodeName;
	public String prevNode;
	public String nextNode;
	public static String nameServerIP;
	
	public static void main(String[] args)throws Exception
	{
		nodeName="Node3";
		int numberOfNodes;
		
		sendMulticast(nodeName);
		numberOfNodes=getNameServerRespons();
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
