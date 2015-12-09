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
	NodeData failedNodeData;
	RMI rmi=new RMI();
	AgentMain agent = new AgentMain(false,null,null,failedNodeData);
	public Fail(NodeData nodeData1, NodeData failedNodeData) {
		this.nodeData1 = nodeData1;
		this.failedNodeData = failedNodeData;
	}
	
	public void failureDetected(){
		//getRMIObject nameserver
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodeData1.getNameServerIP()+":1099/NameServer");
			nameserver.thisNodeFails(failedNodeData.getMyNodeID());
		} catch (Exception e) {System.out.println("failed connection to RMI of the server and get ip");}
		
	}
}
