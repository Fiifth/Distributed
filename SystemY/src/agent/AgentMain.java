package agent;


import java.io.Serializable;
import java.util.TreeMap;

import nodeP.NodeData;
import fileManagers.FileData;

public class AgentMain extends Thread implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles;
	public TreeMap<Integer,FileData> agentLockList;
	
	NodeData nodeData1;
	
	boolean typeOfAgent;
	public AgentMain(boolean typeOfAgent, TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles,TreeMap<Integer,FileData> agentLockList)
	{
		this.typeOfAgent = typeOfAgent;
		this.allAgentNetworkFiles = allAgentNetworkFiles;
		this.agentLockList=agentLockList;
	}
	public void setNodeData1(NodeData nodeData1) 
	{
		this.nodeData1 = nodeData1;
	}
	public void run() 
	{
		if(typeOfAgent)
		{
			//Update agent's network files list
			updateAgentNetworkFiles();
			//Check for lock requests before updating local node's file list
			attemptToLock();
			//Iterate local locks and add to send list
			checkAgentLocks();
			//Update local node's file list
			updateLocalAllFiles();	
		}
	}

	public void updateAgentNetworkFiles()
	{
		if(allAgentNetworkFiles.containsKey(nodeData1.getMyNodeID()))
		{
			if(!allAgentNetworkFiles.get(nodeData1.getMyNodeID()).equals(nodeData1.replFiles))
				allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeData1.replFiles);
		}
		else
			allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeData1.replFiles);
	}
	

	public void attemptToLock()
	{
		TreeMap<Integer,FileData> copyLockList=nodeData1.lockRequestList;
		
		for (int key : copyLockList.keySet()) 
		{
			if (!agentLockList.containsKey(key))
			{
				agentLockList.put(key, copyLockList.get(key));
				nodeData1.lockRequestList.remove(key);
			}
		}
	}
	
	public void checkAgentLocks()
	{
		TreeMap<Integer,FileData> lockedFiles=agentLockList;
		//TODO verzend als je een lokale eigenaar bent en zet iets op verzonden zodat de volgende dit ook niet verzend
		//check of ik een file gelockt heb-->heb ik het ontvangen-->verwijder uit locklist
		for (FileData value : lockedFiles.values())
		{
			if(value.getReplicateOwnerID()==nodeData1.getMyNodeID())
			{
				nodeData1.sendQueue.add(value);
				agentLockList.remove(Math.abs(value.getFileName().hashCode()%32768));
			}
		}
	}
	
	public void updateLocalAllFiles()
	{
		if(!nodeData1.allNetworkFiles.equals(allAgentNetworkFiles))
		{
			nodeData1.allNetworkFiles = allAgentNetworkFiles;
		}
	}
	
}
