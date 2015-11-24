package agent;

import java.util.ArrayList;
import fileManagers.FileData;
import nodeP.NodeData;

public class FailureAgent{
	NodeData nodeData1;
	NodeData failedNodeData;
	public FailureAgent(NodeData nodeData1, NodeData failedNodeData) {
		this.nodeData1 = nodeData1;
		this.failedNodeData = failedNodeData;
	}
	
	public void run(){
		ArrayList<FileData> localFilesList = nodeData1.localFiles;
		
		
	}
}
