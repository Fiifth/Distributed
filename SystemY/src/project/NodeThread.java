package project;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

public class NodeThread extends Thread {
	DatagramPacket messageIn;
	int myNextNode;
	int myPrevNode;
	int myNodeID;
	public NodeThread(DatagramPacket messageIn, int myNextNode,int myPrevNode,int myNodeID)
	{
		this.messageIn=messageIn;
		this.myNextNode=myNextNode;
		this.myPrevNode=myPrevNode;
		this.myNodeID=myNodeID;
		
	}
	
	public void run() {
		String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		InetAddress addr=messageIn.getAddress();
		String nodeIP = addr.getHostAddress().toString();

		int newNodeID=Math.abs(msg.hashCode()%32768);
		System.out.println(newNodeID);
		
		if(myNodeID < newNodeID && newNodeID < myNextNode)
		{
			System.out.println("The new node will be my new next node");
			Node.nextNode=newNodeID;
			sendToNode(myNodeID+"-"+myNextNode,nodeIP);
		}
		else if(myPrevNode == myNextNode && myNextNode == myNodeID)
		{
			sendToNode(myNodeID+"-"+myNodeID,nodeIP);
			Node.prevNode=newNodeID;
			Node.nextNode=newNodeID;
		}
		else if( myNodeID > newNodeID && newNodeID > myPrevNode)
		{
			Node.prevNode=newNodeID;	
			System.out.println("The new node will be my new previous node");
		}
		else if( myNodeID > myPrevNode && newNodeID > myPrevNode )
		{
			Node.prevNode=newNodeID;	
			System.out.println("The new node will be my new previous node");
		}
		else if(myNodeID < newNodeID && myNodeID < myNextNode)
		{
			System.out.println("The new node will be my new next node");
			Node.nextNode=newNodeID;
			sendToNode(myNodeID+"-"+myNextNode,nodeIP);
		}
		
	}
	public void sendToNode(String nodes, String nodeIP)
	{
		Socket clientSocket;
		try {
			Thread.sleep(1000);
			//mss ffkes sleepe zoda nieuwe node eerst tijd heeft gekrege om eerst aantal nodes op te vragen bij nameserver
			//voorlopig zonder sleep
			clientSocket = new Socket(nodeIP,6770);
			DataOutputStream outToNode = new DataOutputStream(clientSocket.getOutputStream());
			outToNode.writeBytes(nodes+ "\n");
			clientSocket.close();
		} catch (IOException | InterruptedException e) {e.printStackTrace();//} catch (InterruptedException e) {
			
			e.printStackTrace();
		}	
	}

}
