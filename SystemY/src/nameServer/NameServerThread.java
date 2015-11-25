package nameServer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.rmi.RemoteException;

import networkFunctions.TCP;

public class NameServerThread extends Thread {
	DatagramPacket messageIn;
	TCP tcp=new TCP();
	StartNameServer nameServer;
	public NameServerThread(DatagramPacket messageIn, StartNameServer nameServer)
	{
		this.messageIn=messageIn;
		this.nameServer=nameServer;
	}
	
	public void run() 
	{
		String msgs = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		String[] message = msgs.split("-");
		int toLeave=Integer.parseInt(message[0]);
		InetAddress addr=messageIn.getAddress();
		String nodeIP = addr.getHostAddress().toString();
		if(toLeave == 1)//rmnode
		{
			try {	
				nameServer.rmNode(message[1],nodeIP);
				System.out.println("Removed NodeIP: " + nodeIP);
				Integer numberOfNodes = nameServer.getNodeMap().size();
				System.out.println("There are "+ numberOfNodes + " nodes left in the map.");
			} catch (RemoteException e) {e.printStackTrace();}				
		}
		else//addnode
		{
			String numOfNodesString=null;
			boolean isNewNode=false;
			try 
			{
				isNewNode = nameServer.addNode(message[1], nodeIP);
			} catch (RemoteException e) {}
			
			if (isNewNode)
			{
				Integer numberOfNodes = nameServer.getNodeMap().size(); 
				System.out.println("Added NodeIP: " + nodeIP);
				numOfNodesString = numberOfNodes.toString();	
			}
			else
			{
				numOfNodesString="0";
			}
			tcp.sendTextWithTCP(numOfNodesString, nodeIP, 6790);
		}
	}
}

