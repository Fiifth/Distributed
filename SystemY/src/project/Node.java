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
	public static String castMsg;
	public static String nameServerIP;
	
	public static void main(String[] args)throws Exception
	{		
		nodeName="Node18";
		MyNodeID=Math.abs(nodeName.hashCode()%32768);
		System.out.print("My name is: ");
		System.out.println(nodeName);
		System.out.print("My id is: ");
		System.out.println(MyNodeID);
		
		//TODO castMsg fixen adhv user input voor leaving en dan nodename bijvoegen
		//if node is joining => castmsg = 0-nodeName
		//if node is leaving => castmsg = 1-nodeName
		
		
		sendMulticast(castMsg);
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
		
		boolean boo = true;
		while(boo == true)
		{
			byte[] buffer = new byte[100];
			DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			multicastSocket.receive(messageIn);//blocks
			//check if node wants to join or leave
			String msgs = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
			String[] node = msgs.split("-");
			toLeave=Integer.parseInt(node[0]);
			InetAddress receivedaddr=messageIn.getAddress();
			InetAddress ownaddr=InetAddress.getLocalHost();
			//TODO if multicast is from own node and asks to remove node => out of loop
			//received multicast is from myself
			if(receivedaddr == ownaddr)
			{
				//i want to leave
				if(toLeave == 1)
				{
					boo = false;
				}
			}
			//received multicast is from new node
			else
			{
				System.out.println("New node connecting");
				//start thread
				NodeThread c =new NodeThread(messageIn,nextNode, prevNode, MyNodeID);
				c.start();
			}
		}
		//TODO uiteindelijk moet het luistere naar de multicast om dan threads op te starten ook in een thread komen anders kunde nooit een node late stoppen
		//TODO er moet ook een thread gemaakt worden die luistert naar nodes die weg willen gaan 
		//1) Stuur de id van de volgende node door naar de vorige node
		//2) In de vorige node wordt de volgende node aangepast met deze info
		//3) Stuur de id van de vorige node op naar de volgende node
		//4) In de volgende node wordt de vorige node aangepast met deze info
		//5) Verwijder de node bij de nameserver
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
