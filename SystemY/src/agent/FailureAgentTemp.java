package agent;

import java.io.Serializable;
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
	
	NodeData nodeData1;
	NodeData failedNodeData;
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
			checkReplicationFiles();
		}
	}
	public void checkReplicationFiles(){
		nodeReplFiles = nodeData1.replFiles;
		for(Map.Entry<Integer,FileData> entry : nodeReplFiles.entrySet()) {
			  Integer key = entry.getKey();
			  FileData value = entry.getValue();

			  System.out.println(key + " => " + value);
			}
	}
}