package nodeAgent;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
		AgentMain failAgent = new AgentMain(false,null,null,null,failedNodeID,nodeData1.getMyNodeID());
		failAgent.setNodeData1(nodeData1);
		failAgent.run();
		while(failAgent.isAlive()){}
		//send and run failureAgent to/on next node
		if((nodeData1.getNextNode() != failAgent.startingNodeID)&& nodeData1.getNextNode()!=failedNodeID)
		{
			System.out.println("failagent started");
			System.out.println("assumedNextNodeID:" + nodeData1.getNextNode());
			RMICommunicationInt recInt;
			try{
				recInt = (RMICommunicationInt) Naming.lookup("//"+nodeData1.getNextNodeIP()+":"+nodeData1.getNextNode()+"/RMICommunication");
				recInt.rmiFailAgentExecution(failAgent);
			} catch (MalformedURLException | RemoteException | NotBoundException e){}
		}
	}
}
