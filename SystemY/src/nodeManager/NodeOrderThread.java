package nodeManager;

import java.net.DatagramPacket;
import java.net.InetAddress;
import neworkFunctions.TCP;
import nodeP.NodeData;

public class NodeOrderThread extends Thread {
	TCP tcp=new TCP();
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
		int toLeave = Integer.parseInt(msgs[0]);
		int newNodeID= Math.abs(msgs[1].hashCode()%32768);

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
			if(myNextNode == newNodeID)
			{
				nodedata1.setNextNode(newNextID);
			}
		}
		//adding new node
		else
		{
			if (myNodeID == newNodeID || newNodeID==myNextNode ||newNodeID==myPrevNode )
			{
				System.out.println("the new node already existed and won't be added");
			}
			else if(myPrevNode == myNextNode && myNextNode == myNodeID)
			{
				tcp.sendTextWithTCP(myNodeID+"-"+myNodeID, nodeIP, 6770);
				nodedata1.setPrevNode(newNodeID);
				nodedata1.setNextNode(newNodeID);
				System.out.println("I am the previous and next of the new node (second)");
			}
			
			
			else if(myNodeID < newNodeID && newNodeID < myNextNode)
			{
				tcp.sendTextWithTCP(myNodeID+"-"+myNextNode, nodeIP, 6770);
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
					tcp.sendTextWithTCP(myNodeID+"-"+myNextNode, nodeIP, 6770);
					nodedata1.setNextNode(newNodeID);
					System.out.println("I am the previous of the new node (end)");
				}
				else if (newNodeID<myNodeID && newNodeID<myPrevNode && newNodeID<myNextNode)
				{
					tcp.sendTextWithTCP(myNodeID+"-"+myNextNode, nodeIP, 6770);
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
}
