package nameServer;

import java.net.DatagramPacket;

import networkFunctions.Multicast;

public class NameServerNodeDetection extends Thread 
{
	StartNameServer nameServer;
	Multicast multi;
	public NameServerNodeDetection(StartNameServer nameServer,Multicast multi)
	{
		this.nameServer=nameServer;
		this.multi = multi;
	}
	public void run()
	{
		while(!Thread.interrupted())
		{
			DatagramPacket messageIn = multi.receiveMulticast();
			NameServerThread c =new NameServerThread(messageIn,nameServer);
			c.start(); 
		}
	}
}
