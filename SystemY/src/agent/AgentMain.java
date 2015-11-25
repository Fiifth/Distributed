package agent;


import java.util.ArrayList;
import java.util.TreeMap;

import nodeP.NodeData;
import fileManagers.FileData;

public class AgentMain extends Thread {
	
	public TreeMap<Integer, ArrayList<FileData>> allAgentNetworkFiles = new TreeMap<Integer,ArrayList<FileData>>();
	
	NodeData nodeData1;
	boolean typeOfAgent;
	public AgentMain(NodeData nodeData1, boolean typeOfAgent, TreeMap<Integer, ArrayList<FileData>> allAgentNetworkFiles)
	{
		this.nodeData1 = nodeData1;
		this.typeOfAgent = typeOfAgent;
		this.allAgentNetworkFiles = allAgentNetworkFiles;
	}
	@Override
	public void run() {
		if(typeOfAgent)
		{
			FileListAgent fla = new FileListAgent(nodeData1, allAgentNetworkFiles);
			fla.runFLA();
		}
	}
	
}
