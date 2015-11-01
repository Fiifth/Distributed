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
				nodedata1.setToLeave(1);
				nodedata1.sendMulticast("1"+"-"+nodedata1.getNodeName()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode());
				stay = false;
			}
		}	
	}
}
