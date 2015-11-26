package agent;


import java.util.ArrayList;
import java.util.TreeMap;

import nodeP.NodeData;
import fileManagers.FileData;

public class AgentMain extends Thread {
	
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
	@Override
	public void run() {
		if(typeOfAgent)
		{
			FileListAgent fla = new FileListAgent(nodeData1, allAgentNetworkFiles,agentLockList);
			fla.runFLA();
		}
	}
	
}
