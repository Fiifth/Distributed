package nodeAgent;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.TreeMap;

import nameServer.NameServerInterface;
import networkFunctions.RMI;
import nodeFileManagers.FileData;
import nodeManager.RMICommunicationInt;
import nodeStart.NodeData;

public class Fail{
	NodeData nodeData1;
	int failedNodeID;
	RMI rmi=new RMI();
	public Fail() {
				
	}
	
	public void failureDetected(NodeData nodeData1, int failedNodeID){
		this.nodeData1 = nodeData1;
		this.failedNodeID = failedNodeID;
		//getRMIObject nameserver
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodeData1.getNameServerIP()+":1099/NameServer");
			nameserver.thisNodeFails(failedNodeID);
		} catch (Exception e) {System.out.println("failed connection to RMI of the server and get ip");}
		AgentMain failAgent = new AgentMain(false,null,null,failedNodeID,nodeData1.getMyNodeID());
		failAgent.setNodeData1(nodeData1);
		failAgent.run();
		while(failAgent.isAlive()){}
		//send and run failureAgent on next node
		if((nodeData1.getNextNode() != failAgent.startingNodeID)&&nodeData1.getNextNode()!=failedNodeID)
		{
			RMICommunicationInt recInt=(RMICommunicationInt) rmi.getRMIObject(nodeData1.getNextNode(), nodeData1.getNextNodeIP(), "RMICommunication");
			try 
			{
				recInt.rmiFileAgentExecution(failAgent);
			} catch (RemoteException e) {e.printStackTrace();}
		}
		else if(nodeData1.getMyNodeID()!=nodeData1.getNextNode())
		{//restart file agent
			TreeMap<Integer, TreeMap<Integer,FileData>> initTree = new TreeMap<Integer, TreeMap<Integer,FileData>>();
			TreeMap<Integer,FileData> agentLockList=new TreeMap<Integer,FileData>();
			AgentMain fileAgent = new AgentMain(true, initTree,agentLockList, 0, 0);
			fileAgent.setNodeData1(nodeData1);
			fileAgent.run();
			
			while(fileAgent.isAlive()){}
			RMICommunicationInt recInt=(RMICommunicationInt) rmi.getRMIObject(nodeData1.getPrevNode(), nodeData1.getPrevNodeIP(), "RMICommunication");
			try 
			{
				recInt.rmiFileAgentExecution(fileAgent);
			} catch (RemoteException e) {e.printStackTrace();}
		}
	}
}
