package nodeP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import fileManagers.FileDetectionT;
import fileManagers.FileExchangeT;
import fileManagers.FileOwnershipT;
import fileManagers.FileReceiverT;
import nodeManager.NodeOrderThread;
import nodeManager.ShutdownT;

public class Node 
{	
	public static void main(String[] args) throws Exception
	{		
		Node node1=new Node();
		node1.startNieuweNode("5.txt");
	}
	public void startNieuweNode(String nodeNaam)throws Exception
	{
		final NodeData nodedata1=new NodeData();
		nodedata1.setNodeName(nodeNaam);
		nodedata1.setMyNodeID(Math.abs((nodedata1.getNodeName()).hashCode()%32768));
		System.out.print("My name is: ");
		System.out.println(nodedata1.getNodeName());
		System.out.print("My id is: ");
		System.out.println(nodedata1.getMyNodeID());


		//hello i am new node, joining network
		nodedata1.sendMulticast("0"+"-"+nodedata1.getNodeName());
		
		//removethread listens if node wants to leave
		ShutdownT rm = new ShutdownT(nodedata1);
		rm.start();

		int numberOfNodes=getNameServerRespons(nodedata1);
		if (numberOfNodes>1)
		{
			System.out.println("Getting nodes...");
			String nodes=getNextPrevNode();
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
		
		FileDetectionT CLFQ =new FileDetectionT(nodedata1);
		CLFQ.start();
		FileReceiverT RQT=new FileReceiverT(nodedata1) ;
		(new Thread(RQT)).start();
		FileExchangeT SRFT = new FileExchangeT(nodedata1);
		SRFT.start();
		
		
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
			//check if I to leave
			if(nodedata1.getToLeave() == 1)
			{
					stay = false;
					multicastSocket.close();
			}	
			else
			{
				System.out.println("Node communication detected");
				//start thread
				NodeOrderThread c =new NodeOrderThread(messageIn,nodedata1);
				c.start();
				FileOwnershipT COT =new FileOwnershipT(nodedata1);
				COT.start();
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
