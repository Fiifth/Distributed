package agent;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

import nodeP.NodeData;
import fileManagers.FileData;

public class AgentMain extends Thread implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TreeMap<Integer, ArrayList<FileData>> allAgentNetworkFiles;
	public ArrayList<FileData> agentLockList;
	
	NodeData nodeData1;
	
	boolean typeOfAgent;
	public AgentMain(boolean typeOfAgent, TreeMap<Integer, ArrayList<FileData>> allAgentNetworkFiles,ArrayList<FileData> agentLockList)
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

	public void updateAgentNetworkFiles(){
		if(allAgentNetworkFiles.containsKey(nodeData1.getMyNodeID()))
		{ //if agent already has a version of node's replFiles
			ArrayList<FileData>tempReplList = allAgentNetworkFiles.get(nodeData1.getMyNodeID()); //place the agents version of replFiles in tempList
			if(tempReplList.equals(nodeData1.replFiles)){ //if agent's version equals node's version
				//list is already up to date
			}
			else{	//agent's version is outdated! -> remove old and add new version
				allAgentNetworkFiles.remove(nodeData1.getMyNodeID());
				allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeData1.replFiles);
			}
		}
		else{ // agent has no version of node's replFiles
			allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeData1.replFiles);
		}
	}
	

	public void attemptToLock()
	{
		boolean found=false;
		ArrayList<FileData> templockRequestList=new ArrayList<FileData>();
		templockRequestList=nodeData1.lockRequestList;
		for (FileData fileToAttemptLock:templockRequestList)
		{
			for(FileData lockedFiles:agentLockList)
			{
				if (lockedFiles.getFileName().equals(fileToAttemptLock.getFileName()))
					found=true;
			}
			if (!found)
			{
				fileToAttemptLock.refreshReplicateOwner(nodeData1, fileToAttemptLock);
				fileToAttemptLock.setDestinationID(nodeData1.getMyNodeID());
				fileToAttemptLock.setDestinationIP(nodeData1.getMyIP());
				fileToAttemptLock.setDestinationFolderReplication(false);
				nodeData1.lockRequestList.remove(fileToAttemptLock);
			}
		}
	}
	
	public void checkAgentLocks(){
		ArrayList<FileData> lockedFiles = agentLockList;
		for(FileData lockedFile : lockedFiles)
		{
			if (lockedFile.getReplicateOwnerID()==nodeData1.getMyNodeID())
			{
				nodeData1.sendQueue.add(lockedFile);
				agentLockList.remove(lockedFile);
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
