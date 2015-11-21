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
		nodeReplFiles = nodeData1.replFiles;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		//Update agent's network files list
		updateAgentNetworkFiles();
		//Check for lock requests before updating local node's file list
		checkLockRequestsAndGrantLock();
		//Update agent's network files list
		updateAgentNetworkFiles();
		//Update local node's file list
		updateLocalAllFiles();
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
		//when granting lock set downloaded to false
		//Node will set downloaded true when ready
		int listSize = nodeData1.lockRequestList.size();
		for(Map.Entry<Integer, ArrayList<FileData>> entry : allAgentNetworkFiles.entrySet()){//search node's file map for lock requests
			int tempNodeID = entry.getKey();
			ArrayList<FileData> tempFiles = entry.getValue();
			Iterator<FileData> itFile = tempFiles.iterator();
			while(itFile.hasNext()){
				FileData tempFD = itFile.next();
				for(int i=0;i<listSize;i++){
					FileData fileToAttemptLock = nodeData1.lockRequestList.get(i);
					if(tempFD.equals(fileToAttemptLock)){
						if(tempFD.getLock()){
							//file is locked, wait until downloading node releases
						}
						else{
							tempFD.setLock(true);
							nodeData1.removeLockRequest(fileToAttemptLock);
							tempFD.setDownloaded(false);
							tempFiles.remove(itFile); // replace original with lock activated file
							tempFiles.add(tempFD);
							allAgentNetworkFiles.remove(tempNodeID);
							allAgentNetworkFiles.put(tempNodeID, tempFiles);
						}
					}
				}
			}
		}
	}
	
	public void updateLocalAllFiles(){
		if(!nodeData1.allNetworkFiles.equals(allAgentNetworkFiles)){
			nodeData1.allNetworkFiles = allAgentNetworkFiles;
		}
		else{
			//nodes network files are already up to date
		}
	}
}
