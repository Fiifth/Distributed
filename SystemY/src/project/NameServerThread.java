package project;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

public class NameServerThread extends Thread {
	DatagramPacket messageIn;
	NameServer nameServer;
	public NameServerThread(DatagramPacket messageIn, NameServer nameServer)
	{
		this.messageIn=messageIn;
		this.nameServer=nameServer;
		
	}
	
	public void run() {
		
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
			try 
			{
				nameServer.addNode(message[1], nodeIP);
				Integer numberOfNodes = nameServer.getNodeMap().size(); 
				System.out.println("Added NodeIP: " + nodeIP);
				String numOfNodesString = numberOfNodes.toString();				
				Socket clientSocket;
				try {
					clientSocket = new Socket(nodeIP,6790);
					DataOutputStream outToNode = new DataOutputStream(clientSocket.getOutputStream());
					outToNode.writeBytes(numOfNodesString + "\n");
					clientSocket.close();
				} catch (IOException e) {e.printStackTrace();}
			} catch (RemoteException e) {e.printStackTrace();}
		}
		
			
	}

}
