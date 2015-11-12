package nameServer;

import java.net.DatagramPacket;
import neworkFunctions.Multicast;

public class NameServerNodeDetection extends Thread 
{
	NameServer nameServer;
	public NameServerNodeDetection(NameServer nameServer)
	{
		this.nameServer=nameServer;
	}
	public void run()
	{
		Multicast multi=new Multicast("228.5.6.7", 6789);
		multi.joinMulticastGroup();
		while(!Thread.interrupted())
		{
			DatagramPacket messageIn = multi.receiveMulticast();
			NameServerThread c =new NameServerThread(messageIn,nameServer);
			c.start(); 
		}
	}
}
