package project;
import java.net.*;
import java.io.*;

public class Node 
{	
	
	public static void main(String[] args)throws Exception
	{		
		final NodeData nodedata1=new NodeData();
		final Node node1=new Node();
		nodedata1.setNodeName("6");
		nodedata1.setMyNodeID(Math.abs((nodedata1.getNodeName()).hashCode()%32768));
		System.out.print("My name is: ");
		System.out.println(nodedata1.getNodeName());
		System.out.print("My id is: ");
		System.out.println(nodedata1.getMyNodeID());
		
		
		//hello i am new node, joining network
		node1.sendMulticast("0"+"-"+nodedata1.getNodeName());
		
		//removethread listens if node wants to leave
		NodeRemoveThread rm = new NodeRemoveThread(nodedata1);
		rm.start();

		int numberOfNodes=node1.getNameServerRespons(nodedata1);
		if (numberOfNodes>1)
		{
			System.out.println("Getting nodes...");
			String nodes=node1.getNextPrevNode();
			String[] node = nodes.split("-");
			nodedata1.setPrevNode(Integer.parseInt(node[0]));
			nodedata1.setNextNode(Integer.parseInt(node[1]));
			//System.out.println("I am node number " + numberOfNodes);
			System.out.println("My: "+nodedata1.getMyNodeID()+" Next: "+nodedata1.getNextNode()+" prev: "+nodedata1.getPrevNode());
		}
		else
		{
			System.out.println("I am the first node");
			 nodedata1.setPrevNode(nodedata1.getMyNodeID());
			 nodedata1.setNextNode(nodedata1.getMyNodeID());
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
			nodedata1.setToLeave(Integer.parseInt(msg[0]));
					
			if(nodedata1.getToLeave() == 1)
			{
				//if received nodename = own node name => remove node
				if(nodedata1.getNodeName() == msg[1])
				{
					stay = false;
					multicastSocket.close();
					
				}
			}
			else
			{
				System.out.println("New node connecting");
				//start thread
				NodeOrderThread c =new NodeOrderThread(messageIn,nodedata1);
				c.start();
			}
			
		}
	}
	
	public String getNextPrevNode() 
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

	public void sendMulticast(String name)
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
	
	public int getNameServerRespons(NodeData nodedata1)
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
			String ServerIPString=serverIP.getHostAddress();
			nodedata1.setNameServerIP(ServerIPString);
			
			System.out.println("ServerIP: " + nodedata1.getNameServerIP());
			
			connectionSocket.close();
		} 
		catch (IOException e) {e.printStackTrace();	}
		return nodes;
	}

}
