package project;
import java.net.*;
import java.io.*;

public class Node 
{	
	public static String nodeName;
	public static int prevNode;
	public static int nextNode;
	public static int MyNodeID;
	public static int toLeave;
	public static String nameServerIP;
	
	public static void main(String[] args)throws Exception
	{		
		nodeName="3";
		MyNodeID=Math.abs(nodeName.hashCode()%32768);
		System.out.print("My name is: ");
		System.out.println(nodeName);
		System.out.print("My id is: ");
		System.out.println(MyNodeID);
		System.out.println("Type quit to stop this node.");
		
		//hello i am new node, joining network
		sendMulticast("0"+"-"+nodeName);
		
		//removethread listens if node wants to leave
		NodeRemoveThread rm = new NodeRemoveThread(nodeName, prevNode, nextNode);
		rm.start();
		
		int numberOfNodes=getNameServerRespons();
		if (numberOfNodes>1)
		{
			System.out.println("Getting nodes...");
			String nodes=getNextPrevNode();
			String[] node = nodes.split("-");
			nextNode=Integer.parseInt(node[0]);
			prevNode=Integer.parseInt(node[1]);
			System.out.println("I am node number " + numberOfNodes);
			System.out.print("My next node: ");
			System.out.println(nextNode);
			System.out.print("My prev node: ");
			System.out.println(prevNode);
		}
		else
		{
			System.out.println("I am the first node");
			 prevNode=MyNodeID;
			 nextNode=MyNodeID;
		}
		
		MulticastSocket multicastSocket =null;
		InetAddress group = InetAddress.getByName("228.5.6.7");
		multicastSocket = new MulticastSocket(6789);
		multicastSocket.joinGroup(group);
		
		boolean stay = true;
		while(stay == true)
		{
			byte[] buffer = new byte[100];
			DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			multicastSocket.receive(messageIn);//blocks
			//check if node wants to join or leave
			String msgs = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
			String[] msg = msgs.split("-");
			toLeave=Integer.parseInt(msg[0]);
					
			if(toLeave == 1)
			{
				//if received nodename = own node name => remove node
				if(Node.nodeName == msg[1])
				{
					stay = false;
					multicastSocket.close();
					
				}
			}
			else
			{
				System.out.println("New node connecting");
				//start thread
				NodeOrderThread c =new NodeOrderThread(messageIn,nextNode, prevNode, MyNodeID);
				c.start();
			}
			
		}
	}
	
	private static String getNextPrevNode() 
	{
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		String nextPrevNode = null;
		
		try {
			welcomeSocket = new ServerSocket(6770);
			connectionSocket = welcomeSocket.accept();
			welcomeSocket.close();
			BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			nextPrevNode = inFromNameServer.readLine();			
			connectionSocket.close();
		} 
		catch (IOException e) {e.printStackTrace();	}
		return nextPrevNode;
		
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
			System.out.println("Amount of Nodes: " + amountOfNodes);
			serverIP=connectionSocket.getInetAddress();
			nameServerIP=serverIP.getHostAddress();
			System.out.println("ServerIP: " + nameServerIP);
			
			connectionSocket.close();
		} 
		catch (IOException e) {e.printStackTrace();	}
		return nodes;
	}

}
