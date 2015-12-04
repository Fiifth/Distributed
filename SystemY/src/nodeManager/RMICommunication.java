package nodeManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import agent.AgentMain;
import fileManagers.FileData;
import networkFunctions.RMI;
import nodeP.NodeData;

public class RMICommunication extends UnicastRemoteObject implements RMICommunicationInt  {
	private static final long serialVersionUID = 1L;
	NodeData nodedata1;
	RMI rmi=new RMI();
	public RMICommunication(NodeData nodedata1) throws RemoteException {
		super();
		this.nodedata1=nodedata1;
	}
	public void setUpRMI()
	{
		RMICommunicationInt rmiInt = this;
		nodedata1.setBind(rmi.bindObjectRMI(nodedata1.getMyNodeID(), "localhost", "RMICommunication", rmiInt));
	}

	public boolean receiveThisFile(FileData file1) throws RemoteException 
	{
		//IF destinationFolderReplication
		//TODO filedata already present? -->return false and add owner
		boolean queue=nodedata1.receiveQueue.offer(file1);
		return queue;
	}
	
	public boolean removeOwner(FileData file1) throws RemoteException {
		int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
		FileData removedFile=nodedata1.replFiles.get(fileNameHash);
		
      //TODO taken enkel uitvoeren in filedata als er geen owners meer zijn
        nodedata1.replFiles.remove(fileNameHash);
        nodedata1.removeQueue.add(removedFile);
        
		return false;
	}
	
	public boolean addOwner(FileData file1) throws RemoteException {
		return false;
	}
	public void rmiAgentExecution(AgentMain fileAgent) throws RemoteException
	{
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}	
		if (nodedata1.getPrevNode()!=nodedata1.getMyNodeID())
		{
			fileAgent.setNodeData1(nodedata1);
			fileAgent.run();
			while(fileAgent.isAlive()){}
			new Thread() {
	            public void run() {
	            	RMICommunicationInt recInt=  (RMICommunicationInt) rmi.getRMIObject(nodedata1.getPrevNode(), nodedata1.getPrevNodeIP(), "RMICommunication");
	    			try {
	    				((RMICommunicationInt) recInt).rmiAgentExecution(fileAgent);
	    			} catch (RemoteException e) {}
	            }
	        }.start();
			
		}
	}
}
