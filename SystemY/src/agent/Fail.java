package agent;

import java.util.ArrayList;
import fileManagers.FileData;
import nodeP.NodeData;

public class Fail{
	NodeData nodeData1;
	NodeData failedNodeData;
	public Fail(NodeData nodeData1, NodeData failedNodeData) {
		this.nodeData1 = nodeData1;
		this.failedNodeData = failedNodeData;
	}
	
	public void failureDetected(){
				
	}
}
