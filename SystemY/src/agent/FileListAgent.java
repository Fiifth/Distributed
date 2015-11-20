package agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import fileManagers.FileData;
import nodeP.NodeData;

public class FileListAgent extends AgentMain{
	public TreeMap<Integer, ArrayList<FileData>> allAgentNetworkFiles = new TreeMap<Integer,ArrayList<FileData>>();
	ArrayList<FileData> nodeReplFiles;
	public FileListAgent(NodeData nodeData1) {
		super(nodeData1);
		nodeReplFiles = new ArrayList<FileData>();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		nodeReplFiles = nodeData1.replFiles;
		//Check for lock requests before updating local node's file list
		checkLockRequestsAndGrantLock();
		//Update agent's network files list
		updateAgentNetworkFiles();
		//Update local node's file list
		if(!nodeData1.allNetworkFiles.equals(allAgentNetworkFiles)){
			nodeData1.allNetworkFiles = allAgentNetworkFiles;
		}
		else{
			//nodes network files are already up to date
		}
	}
	public void updateAgentNetworkFiles(){
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
	}
	
	public void checkLockRequestsAndGrantLock(){
		for(Map.Entry<Integer, ArrayList<FileData>> entry : nodeData1.allNetworkFiles.entrySet()){//search node's file map for lock requests
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
						tempFiles.remove(itFile); // replace original with lock activated file
						tempFiles.add(tempFD);
						nodeData1.allNetworkFiles.remove(tempNodeID);
						nodeData1.allNetworkFiles.put(tempNodeID, tempFiles);
						itFile = tempFiles.iterator();//reset iterator after removing itfile from list
					}
				}
			}
		}
	}
}
