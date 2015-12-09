package agent;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import nodeP.NodeData;
import fileManagers.FileData;

public class FailureAgentTemp extends Thread implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles;
	public TreeMap<Integer,FileData> agentLockList;
	public TreeMap<Integer,FileData> nodeReplFiles;
	
	public NodeData nodeData1;
	public NodeData failedNodeData;
	boolean typeOfAgent;
	public FailureAgentTemp(boolean typeOfAgent, TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles,TreeMap<Integer,FileData> agentLockList,NodeData failedNodeData)
	{
		this.typeOfAgent = typeOfAgent;
		this.allAgentNetworkFiles = allAgentNetworkFiles;
		this.agentLockList=agentLockList;
		this.failedNodeData = failedNodeData;
	}
	public void setNodeData1(NodeData nodeData1) 
	{
		this.nodeData1 = nodeData1;
	}
	public void run() 
	{
		if(typeOfAgent)
		{
			//
		}
		else
		{
			//check if local owners equals failing node id, if so remove owner from owners list
			checkReplicationFiles();
			checkReplicationOwnerOfLocalFiles();
		}
	}
	public void checkReplicationFiles(){
		TreeMap<Integer,FileData> nodeReplFiles = nodeData1.replFiles;
		for(Map.Entry<Integer,FileData> entry : nodeReplFiles.entrySet()) 
		{
			  Integer key = entry.getKey();
			  FileData tempFD = entry.getValue();
			  ArrayList<Integer> localFileOwners = tempFD.getLocalOwners();
			  for(int temp : localFileOwners)
			  {
				  if(temp == failedNodeData.getMyNodeID())
				  {
					  if(tempFD.removeOwner(temp))//returns true if no owners remain
					  {//if no local owners remain remove file from replication lists
						  nodeData1.replFiles.remove(key);
						  Path source = Paths.get(tempFD.getFolderLocation()+"\\"+tempFD.getFileName());
						  try {Files.delete(source);} catch (IOException e) {}
					  }
					  else
					  {//if other owners exist update file in list
						  nodeData1.replFiles.put(key,tempFD);
					  }
				  }
			  }
			  
		}
	}
	
	public void checkReplicationOwnerOfLocalFiles(){
		TreeMap<Integer,FileData> nodeLocalFiles = nodeData1.localFiles;
		for(Map.Entry<Integer,FileData> entry : nodeLocalFiles.entrySet())
		{
			Integer key = entry.getKey();
			FileData tempFD = entry.getValue();
			if(tempFD.getReplicateOwnerID() == failedNodeData.getMyNodeID())
			{
				tempFD.refreshReplicateOwner(nodeData1);
				nodeData1.sendQueue.add(tempFD);
				nodeData1.localFiles.put(key,tempFD);
			}
		}
	}
}