package project;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;

public class NodeOrderThread extends Thread {
	DatagramPacket messageIn;
	NodeData nodedata1;
	int myNodeID;
	int myPrevNode;
	int myNextNode;
	public NodeOrderThread(DatagramPacket messageIn, NodeData nodedata1)
	{
		this.messageIn=messageIn;
		this.nodedata1=nodedata1;
		myNodeID=nodedata1.getMyNodeID();
		myPrevNode=nodedata1.getPrevNode();
		myNextNode=nodedata1.getNextNode();
		
	}

	public void run() {
		String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		//message = 0-nodeName or 1-nodename-prevnode-nextnode
		InetAddress addr=messageIn.getAddress();
		String nodeIP = addr.getHostAddress().toString();
		String[] msgs = msg.split("-");
		//node[0]=toleave, node[1] = his name, node[2] = hisprevID, node[3] = hisnextID
		int toLeave = Integer.parseInt(msgs[0]);
		//his nodeName to nodeID to compare with his next and his prev
		int newNodeID= Math.abs(msgs[1].hashCode()%32768);
		
		//removing node
		if(toLeave == 1)
		{
			//Strings van ID's naar int parsen
			int newPrevID=Integer.parseInt(msgs[2]);
			int newNextID=Integer.parseInt(msgs[3]);
			//if myprev == his id => myprev to his prev
			if(myPrevNode == newNodeID)
			{
				nodedata1.setPrevNode(newPrevID);
			}
			//if mynext == his id => mynext to his next
			else if(myNextNode == newNodeID)
			{
				nodedata1.setNextNode(newNextID);
			}
		}
		//adding new node
		else
		{
			if(myPrevNode == myNextNode && myNextNode == myNodeID)
			{
				sendToNode(myNodeID+"-"+myNodeID,nodeIP);
				nodedata1.setPrevNode(newNodeID);
				nodedata1.setNextNode(newNodeID);
				System.out.println("I am the previous/next of the new node (second)");
			}
			
			
			else if(myNodeID < newNodeID && newNodeID < myNextNode)
			{
				sendToNode(myNodeID+"-"+myNextNode,nodeIP);
				nodedata1.setNextNode(newNodeID);
				System.out.println("I am the previous of the new node (middle)");
			}
			else if( myNodeID > newNodeID && newNodeID > myPrevNode)
			{
				nodedata1.setPrevNode(newNodeID);	
				System.out.println("I am the next of the new node (middle)");
			}

			else if(myNodeID>myPrevNode && myNodeID>myNextNode)
			{
				//potential prev of new node
				if (newNodeID>myNodeID && newNodeID>myPrevNode && newNodeID>myNextNode)
				{
					sendToNode(myNodeID+"-"+myNextNode,nodeIP);
					nodedata1.setNextNode(newNodeID);
					System.out.println("I am the previous of the new node (end)");
				}
				else if (newNodeID<myNodeID && newNodeID<myPrevNode && newNodeID<myNextNode)
				{
					sendToNode(myNodeID+"-"+myNextNode,nodeIP);
					nodedata1.setNextNode(newNodeID);
					System.out.println("I am the previous of the new node (begin)");
				}
				
			}
			else if(myNodeID<myPrevNode && myNodeID<myNextNode)
			{
				//potential next of new node
				if (newNodeID>myNodeID && newNodeID>myPrevNode && newNodeID>myNextNode)
				{
					nodedata1.setPrevNode(newNodeID);	
					System.out.println("I am the next of the new node (end)");
				}
				else if (newNodeID<myNodeID && newNodeID<myPrevNode && newNodeID<myNextNode)
				{
					nodedata1.setPrevNode(newNodeID);	
					System.out.println("I am the next of the new node (begin)");
				}	
			}
		}
		
		System.out.println("My: "+nodedata1.getMyNodeID()+" Next: "+nodedata1.getNextNode()+" prev: "+nodedata1.getPrevNode());
		
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
