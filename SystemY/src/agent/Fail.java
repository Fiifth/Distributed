package agent;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import fileManagers.FileData;
import nameServer.NameServerInterface;
import networkFunctions.RMI;
import nodeManager.RMICommunicationInt;
import nodeP.NodeData;

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
		failAgent.run();
		while(failAgent.isAlive()){}
		//send and run failureAgent on next node
		if(nodeData1.getNextNode() != failAgent.startingNodeID){
			RMICommunicationInt recInt=(RMICommunicationInt) rmi.getRMIObject(nodeData1.getNextNode(), nodeData1.getNextNodeIP(), "RMICommunication");
			try 
			{
				recInt.rmiFileAgentExecution(failAgent);
			} catch (RemoteException e) {e.printStackTrace();}
		}
	}
}
