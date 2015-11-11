package nodeP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.rmi.RemoteException;
import fileManagers.FileDetectionT;
import fileManagers.Sender;
import fileManagers.FileOwnershipT;
import fileManagers.Receiver;
import fileManagers.Remover;
import nodeManager.NodeOrderThread;
import nodeManager.ShutdownT;

public class Node 
{	
	public static void main(String[] args) throws Exception
	{		
		Node node1=new Node();
		node1.startNieuweNode("5");
	}
	public void startNieuweNode(String nodeNaam)
	{
		final NodeData nodedata1=new NodeData();
		nodedata1.setNodeName(nodeNaam);
		System.out.println("My name is: "+nodedata1.getNodeName());
		System.out.println("My id is: "+nodedata1.getMyNodeID());

		nodedata1.sendMulticast("0"+"-"+nodedata1.getNodeName());	//TODO change to TCP.sendMulticast


		int numberOfNodes=getNameServerRespons(nodedata1);
		if (numberOfNodes>1)
		{
			String nodes=getNextPrevNode();
			String[] node = nodes.split("-");
			nodedata1.setPrevNode(Integer.parseInt(node[0]));
			nodedata1.setNextNode(Integer.parseInt(node[1]));
			System.out.println("My: "+nodedata1.getMyNodeID()+" Next: "+nodedata1.getNextNode()+" prev: "+nodedata1.getPrevNode());
		}
		else if(numberOfNodes==1)
		{
			System.out.println("I am the first node");
			 nodedata1.setPrevNode(nodedata1.getMyNodeID());
			 nodedata1.setNextNode(nodedata1.getMyNodeID());
		}
		else if(numberOfNodes==0)
		{
			System.out.println("this node name already exists, please try again with a different name");
			return;
		}
		else
		{
			System.out.println("no nameserver was found");
			return;
		}	
		try {
			RMICommunication rmi=new RMICommunication(nodedata1);
			rmi.setUpRMI();
			
		} catch (RemoteException e1) {e1.printStackTrace();}
		
			
			FileDetectionT CLFQ =new FileDetectionT(nodedata1);
			CLFQ.start();
			Remover rem =new Remover(nodedata1);
			rem.start();
			 Receiver RQT = new Receiver(nodedata1);
			RQT.start();
			Sender SRFT = new Sender(nodedata1);
			SRFT.start();
			ShutdownT rm = new ShutdownT(nodedata1,CLFQ,rem,RQT,SRFT);
			rm.start();
		
		MulticastSocket multicastSocket =null;
		
		try {	//TODO change to mulicast.joinmulitcast
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(group);
		} catch (IOException e) {System.out.println("failed to join multicast group");}
		
		boolean stay = true;
		while(stay == true)
		{
			byte[] buffer = new byte[100]; //TODO change to mulicast.receivemulticast
			DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
			try {
				multicastSocket.receive(messageIn); //blocks
			} catch (IOException e) {System.out.println("failed to receive multicast message");}
			System.out.println("Node communication detected");
			String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
			//message = 0-nodeName or 1-nodename-prevnode-nextnode
;
			String[] msgs = msg.split("-");
			if(nodedata1.getToLeave() == 1)
			{
					stay = false;
					multicastSocket.close();
			}	
			else
			{
				NodeOrderThread c =new NodeOrderThread(messageIn,nodedata1);
				c.start();

				FileOwnershipT COT =new FileOwnershipT(nodedata1);
				COT.start();

			}
		}

		System.out.println("stopped");
	}

	public String getNextPrevNode() //TODO change to TCP.receiveText
		{
			ServerSocket welcomeSocket = null;
			Socket connectionSocket = null;
			String nextPrevNode = null;
			
			try {
				welcomeSocket = new ServerSocket(6770);
				connectionSocket = welcomeSocket.accept();
				welcomeSocket.close();
				BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				nextPrevNode = inFromNameServer.readLine();			
				connectionSocket.close();
			} 
			catch (IOException e) {e.printStackTrace();	}
			return nextPrevNode;		
		}
		
	public int getNameServerRespons(NodeData nodedata1) //TODO change to TCP.receiveText
		{
			ServerSocket welcomeSocket = null;
			Socket connectionSocket = null;
			InetAddress serverIP;
			int nodes=-1;
			
			try {
				welcomeSocket = new ServerSocket(6790);
				welcomeSocket.setSoTimeout(5000);
				connectionSocket = welcomeSocket.accept();
				welcomeSocket.close();
				BufferedReader inFromNameServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				String amountOfNodes = inFromNameServer.readLine();
				nodes=Integer.parseInt(amountOfNodes);
				serverIP=connectionSocket.getInetAddress();
				String ServerIPString=serverIP.getHostAddress();
				nodedata1.setNameServerIP(ServerIPString);
				connectionSocket.close();
			} 
			catch (IOException e) {}
			return nodes;
		}
}
