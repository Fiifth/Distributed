package project;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

public class NameServerThread extends Thread {
	DatagramPacket messageIn;
	public NameServerThread(DatagramPacket messageIn)
	{
		this.messageIn=messageIn;
		
	}
	
	public void run() {
		String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		InetAddress addr=messageIn.getAddress();
		String nodeIP = addr.getHostAddress().toString();
		
		try 
		{
			NameServer nameserver = new NameServer();
			nameserver.addNode(msg,nodeIP);
		}
		
		catch (RemoteException e) {e.printStackTrace();}
		Integer numberOfNodes = NameServer.nodeMap.size(); 
		System.out.println("Added NodeIP:" + nodeIP);
		String numOfNodesString = numberOfNodes.toString();
		
		Socket clientSocket;
		try {
			clientSocket = new Socket(nodeIP,6790);
			DataOutputStream outToNode = new DataOutputStream(clientSocket.getOutputStream());
			outToNode.writeBytes(numOfNodesString + "\n");
			clientSocket.close();
		} catch (IOException e) {e.printStackTrace();}	
	}

}
