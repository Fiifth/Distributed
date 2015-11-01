package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class NodeRemoveThread extends Thread
{
	NodeData nodedata1;
	String input;
	
	public NodeRemoveThread(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}

	public void run() 
	{
		boolean stay = true;
		System.out.println("Type quit to stop this node.");
		while(stay)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(input.equals("quit"))
			{
				sendMulticast("1"+"-"+nodedata1.getNodeName()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode());
				stay = false;
			}
		}
		
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
	
}
