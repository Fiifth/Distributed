package nodeManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

import networkFunctions.RMI;
import nodeAgent.AgentMain;
import nodeAgent.Fail;
import nodeFileManagers.FileData;
import nodeStart.NodeData;

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
		int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
		if (file1.getDestinationFolder().equals("rep")&&nodedata1.replFiles.containsKey(fileNameHash))
		{
			FileData temp=nodedata1.replFiles.get(fileNameHash);
			temp.addOwner(file1.getSourceID(),file1.getSourceIP());
			nodedata1.replFiles.put(fileNameHash, temp);
			return true;
		}
		else
		{
		nodedata1.receiveQueue.offer(file1);
		return false; 
		}
	}
	
	public void removeThisOwner(FileData file1) throws RemoteException 
	{
		int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
		if (nodedata1.replFiles.containsKey(fileNameHash))
		{
			FileData removedFile=nodedata1.replFiles.get(fileNameHash);
			if (removedFile.removeOwner(file1.getSourceID()))
			{
				nodedata1.replFiles.remove(fileNameHash);
				Path source = Paths.get(removedFile.getFolderLocation()+"\\"+removedFile.getFileName());
				try {Files.delete(source);} catch (IOException e) {}
			}
		}
	}
	
	public boolean addOwner(FileData file1) throws RemoteException {
		return false;
	}
	public void rmiFileAgentExecution(AgentMain fileAgent) throws RemoteException
	{
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}	
		if (nodedata1.getPrevNode()!=nodedata1.getMyNodeID())
		{
			fileAgent.setNodeData1(nodedata1);
			fileAgent.run();
			while(fileAgent.isAlive()){}
			new Thread() {
	            public void run() 
	            {
	            	RMICommunicationInt recInt;
					try {
						recInt = (RMICommunicationInt) Naming.lookup("//"+nodedata1.getPrevNodeIP()+":"+nodedata1.getPrevNode()+"/RMICommunication");
						recInt.rmiFileAgentExecution(fileAgent);
					} catch (MalformedURLException | RemoteException | NotBoundException e) {
						Fail fail = new Fail();
						int failedNodeID = nodedata1.getPrevNode();
						fail.failureDetected(nodedata1, failedNodeID);
						//e.printStackTrace();
					}
	            }
	        }.start();
		}
		else
			nodedata1.allNetworkFiles.clear();
	}
	public void rmiFailAgentExecution(AgentMain failAgent) throws RemoteException
	{
		System.out.println("running failagent");
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}	
		if(nodedata1.getNextNode() != nodedata1.getMyNodeID())
		{
			if(nodedata1.getNextNode() != failAgent.startingNodeID)
			{
				failAgent.setNodeData1(nodedata1);
				failAgent.run();
				while(failAgent.isAlive()){}
				new Thread() {
					public void run()
					{
						RMICommunicationInt recInt;
						try{
							recInt = (RMICommunicationInt) Naming.lookup("//"+nodedata1.getNextNodeIP()+":"+nodedata1.getNextNode()+"/RMICommunication");
							recInt.rmiFailAgentExecution(failAgent);
						} catch (MalformedURLException | RemoteException | NotBoundException e){}
					}
				}.start();
			}
			else //restart fileAgent after restoring from failure
			{
				System.out.println("restarting fileAgent");
				TreeMap<Integer, TreeMap<Integer, FileData>> NewAgentNetworkFiles = new TreeMap<Integer, TreeMap<Integer,FileData>>();
				TreeMap<Integer, FileData> newDownloadMap = new TreeMap<Integer, FileData>();
				TreeMap<Integer, Integer> newRemoveMap = new TreeMap<Integer, Integer>();
				
				AgentMain fileAgent = new AgentMain(true, NewAgentNetworkFiles, newDownloadMap, newRemoveMap, 0, 0);
				rmiFileAgentExecution(fileAgent);
			}
		}
	}
}
