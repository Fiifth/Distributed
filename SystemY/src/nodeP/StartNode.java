package nodeP;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;

import agent.AgentMain;
import agent.FileListAgent;
import fileManagers.*;
import neworkFunctions.*;
import nodeManager.*;

public class StartNode 
{	
	TCP tcp=new TCP();
	Multicast multi=new Multicast("228.5.6.7", 6789);
	String nodeName;
	public NodeData nodedata1;
	
	public StartNode(String nodeName)
	{
		this.nodeName=nodeName;
	}
	
	public void startNewNode()
	{
		nodedata1=new NodeData();
		
		nodedata1.setNodeName(nodeName);
		System.out.println("My name is: "+nodedata1.getNodeName());
		System.out.println("My id is: "+nodedata1.getMyNodeID());

		multi.joinMulticastGroup();
		multi.sendMulticast("0"+"-"+nodedata1.getNodeName());
		multi.LeaveMulticast();
		
		if (!setSurroundingNodes(nodedata1)) return;
			
		try 
		{
			RMICommunication rmiCom=new RMICommunication(nodedata1);
			rmiCom.setUpRMI();
		} catch (RemoteException e1) {e1.printStackTrace();}
		ArrayList<Object> threadList = new ArrayList<Object>();
		
		FileDetectionT filedetector =new FileDetectionT(nodedata1);
		filedetector.start();
		threadList.add(filedetector);
		Remover remover =new Remover(nodedata1);
		remover.start();
		threadList.add(remover);
		Receiver receiver = new Receiver(nodedata1);
		receiver.start();
		threadList.add(receiver);
		Sender sender = new Sender(nodedata1);
		sender.start();
		threadList.add(sender);
		NodeDetection nodedetection =new NodeDetection(nodedata1,multi);
		nodedetection.start();
		threadList.add(nodedetection);
		ShutdownT shutdown = new ShutdownT(nodedata1,threadList,multi);
		shutdown.start();
	}
	
	private boolean setSurroundingNodes(NodeData nodedata1) 
	{
		int numberOfNodes=getNameServerRespons(nodedata1);
		if (numberOfNodes>1)
		{
			String nodes = tcp.receiveTextWithTCP(6770, 5000)[0];
			String[] node = nodes.split("-");
			nodedata1.setPrevNode(Integer.parseInt(node[0]));
			nodedata1.setNextNode(Integer.parseInt(node[1]));
			System.out.println("My: "+nodedata1.getMyNodeID()+" Next: "+nodedata1.getNextNode()+" prev: "+nodedata1.getPrevNode());
			if(numberOfNodes==2)
			{
				TreeMap<Integer,ArrayList<FileData>> initTree = new TreeMap<Integer,ArrayList<FileData>>();
				AgentMain fileAgent = new AgentMain(nodedata1, true, initTree);
				fileAgent.run();
			}
			return true;
			
		}
		else if(numberOfNodes==1)
		{
			System.out.println("I am the first node");
			 nodedata1.setPrevNode(nodedata1.getMyNodeID());
			 nodedata1.setNextNode(nodedata1.getMyNodeID());
			 return true;
		}
		else if(numberOfNodes==0)
		{
			System.out.println("this node name already exists, please try again with a different name");
			return false;
		}
		else
		{
			System.out.println("no nameserver was found");
			return false;
		}
		
	}
	
	public int getNameServerRespons(NodeData nodedata1) 
	{
		int nodes;
		String[] received=tcp.receiveTextWithTCP(6790, 5000);
		nodedata1.setNameServerIP(received[1]);
		if (received[0] != null)
		{
		nodes=Integer.parseInt(received[0]);
		}
		else nodes=-1;
		return nodes;
	}
}