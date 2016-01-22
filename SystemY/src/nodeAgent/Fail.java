package nodeAgent;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import nameServer.NameServerInterface;
import nodeManager.RMICommunicationInt;
import nodeStart.NodeData;

public class Fail
{
	
	public void failureDetected(NodeData nodeData1, int failedNodeID)
	{
		if(nodeData1.isDebug()) System.out.println("Failure detected: "+failedNodeID);
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodeData1.getNameServerIP()+":1099/NameServer");
			nameserver.thisNodeFails(failedNodeID);
		} catch (Exception e) {System.out.println("failed connection to RMI of the server and get ip");}
		try {Thread.sleep(2000);} catch (InterruptedException e1) {	}
		
		AgentMain failAgent = new AgentMain(false,null,null,null,failedNodeID,nodeData1.getMyNodeID());
		failAgent.setNodeData1(nodeData1);
		failAgent.run();
		
		while(failAgent.isAlive()){}

		if((nodeData1.getNextNode() != failAgent.startingNodeID)&& nodeData1.getNextNode()!=failedNodeID)
		{;
			RMICommunicationInt recInt;
			try {
				recInt = (RMICommunicationInt) Naming.lookup("//"+nodeData1.getNextNodeIP()+":"+nodeData1.getNextNode()+"/RMICommunication");
				recInt.rmiFailAgentExecution(failAgent);
			} catch (RemoteException | MalformedURLException | NotBoundException e) {}
			
		}
	}
}
