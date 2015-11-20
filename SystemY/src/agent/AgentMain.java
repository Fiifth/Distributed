package agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;

import nodeP.NodeData;

import fileManagers.FileData;
public class AgentMain implements Runnable , Serializable {
	
	public TreeMap<Integer, ArrayList<FileData>> allAgentNetworkFiles = new TreeMap<Integer,ArrayList<FileData>>();
	NodeData nodeData1;
	
	public AgentMain(NodeData nodeData1){
		this.nodeData1 = nodeData1;
	}
	@Override
	public void run() {
		ArrayList<FileData> nodeReplFiles = nodeData1.replFiles;
		
		//Update agent's network files list
		if(allAgentNetworkFiles.containsKey(nodeData1.getMyNodeID())){ //if agent already has a version of node's replFiles
			ArrayList<FileData>tempReplList = allAgentNetworkFiles.get(nodeData1.getMyNodeID()); //place the agents version of replFiles in tempList
			if(tempReplList.equals(nodeData1.replFiles)){ //if agent's version equals node's version
				//list is already up to date
			}
			else{	//agent's version is outdated! -> remove old and add new version
				allAgentNetworkFiles.remove(nodeData1.getMyNodeID());
				allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeReplFiles);
			}
		}
		else{ // agent has no version of node's replFiles
			allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeReplFiles);
		}
		//Check for lock requests before updating local node's file list
		for(Map.Entry<Integer, ArrayList<FileData>> entry : nodeData1.allNetworkFiles.entrySet()){
			int tempNodeID = entry.getKey();
			ArrayList<FileData> tempFiles = entry.getValue();
			Iterator<FileData> itFile = tempFiles.iterator();
			while(itFile.hasNext()){
				FileData tempFD = itFile.next();
				boolean lockRequested = tempFD.getLockRequest();
				if(lockRequested){ // if LockRequested check if lock can be granted to this node in agentAllFiles
					boolean isLocked = tempFD.getLock();
					if(isLocked){
						//file is locked, so keep request active
					}
					else{
						tempFD.setLock(true); //activate lock when granted to this node
						tempFD.setLockRequest(false); //deactivate lock request when granted
					}
					
				}
			}
		}
						
		//Update local node's file list
		if(!nodeData1.allNetworkFiles.equals(allAgentNetworkFiles)){
			nodeData1.allNetworkFiles = allAgentNetworkFiles;
		}
	}
	
}
