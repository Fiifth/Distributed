package nodeManager;

import java.net.DatagramPacket;

import networkFunctions.Multicast;
import nodeStart.NodeData;

public class NodeDetection extends Thread 
{
	NodeData nodedata1;
	Multicast multi;
	
	public NodeDetection(NodeData nodedata1,Multicast multi)
	{
		this.nodedata1=nodedata1;
		this.multi=multi;
	}
	
	public void run()
	{
		multi.joinMulticastGroup();
		while(nodedata1.getToLeave() == 0)
		{
			DatagramPacket messageIn = multi.receiveMulticast();			
			if(nodedata1.getToLeave() == 0)
			{
				NodeOrderThread c =new NodeOrderThread(messageIn,nodedata1);
				c.start();
			}
		}
	}

}
