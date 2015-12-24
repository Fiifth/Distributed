package nodeAgent;


import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nodeFileManagers.FileData;

import java.util.TreeMap;

import nodeStart.NodeData;

public class AgentMain extends Thread implements Serializable
{
	private static final long serialVersionUID = 1L;
	public TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles;
	public TreeMap<Integer,FileData> downloadMap; //local owner,filedata
	public TreeMap<Integer,Integer> removeMap; //local owner,fileID
	public boolean networkFilesChanged;
	
	
	NodeData nodeData1;
	int failedNodeID;
	int startingNodeID;
	boolean typeOfAgent;
	public AgentMain(boolean typeOfAgent, TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles,TreeMap<Integer,FileData> downloadMap,TreeMap<Integer,Integer> removeMap, int failedNodeID, int failStarterID)
	{
		this.typeOfAgent = typeOfAgent;
		this.allAgentNetworkFiles = allAgentNetworkFiles;
		this.downloadMap=downloadMap;
		this.removeMap=removeMap;
		this.failedNodeID = failedNodeID;
		startingNodeID = failStarterID;
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
			//Iterate local locks and add actions for other nodes
			attemptToLock();
			//Check if current node has to take action (send/remove)
			checkAgentLockAction();
			//Update local node's file list
			updateLocalAllFiles();	
		}
		else
		{
			//check if local owners equals failing node id, if so remove owner from owners list
			checkReplicationFiles();
			checkReplicationOwnerOfLocalFiles();
		}
	}
	public void updateAgentNetworkFiles()
	{
	
		TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFilesTemp=new TreeMap<Integer, TreeMap<Integer,FileData>>();
		TreeMap<Integer, FileData> repFilesTemp=new TreeMap<Integer, FileData>();
		repFilesTemp.putAll(nodeData1.replFiles);
		allAgentNetworkFilesTemp.putAll(allAgentNetworkFiles);
		networkFilesChanged=false;
		if(allAgentNetworkFiles.containsKey(nodeData1.getMyNodeID()))
		{				
			TreeMap<Integer,FileData> tempMyFilesOnNode=new TreeMap<Integer,FileData>();
			tempMyFilesOnNode.putAll(allAgentNetworkFiles.get(nodeData1.getMyNodeID()));
			
			for (int key : repFilesTemp.keySet())
			{
				if(!(tempMyFilesOnNode.containsKey(key)))
				{
					tempMyFilesOnNode.put(key,repFilesTemp.get(key));
					networkFilesChanged=true;
				}
				else if (!tempMyFilesOnNode.get(key).getLocalOwners().equals(repFilesTemp.get(key).getLocalOwners()))
				{
					tempMyFilesOnNode.put(key,repFilesTemp.get(key));
					networkFilesChanged=true;
				}
			}
			TreeMap<Integer,FileData> tempMyFilesOnNode2=new TreeMap<Integer,FileData>();
			tempMyFilesOnNode2.putAll(tempMyFilesOnNode);
			for (int key : tempMyFilesOnNode2.keySet())
			{
				if(!(repFilesTemp.containsKey(key)))
				{
					tempMyFilesOnNode.remove(key);
					networkFilesChanged=true;
				}
			}
			
			if (networkFilesChanged==true)
			{
				allAgentNetworkFiles.remove(nodeData1.getMyNodeID());
				allAgentNetworkFiles.put(nodeData1.getMyNodeID(),tempMyFilesOnNode);
			}
		}
		else
			allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeData1.replFiles);
	}
	public void attemptToLock()
	{
		
		TreeMap<Integer, TreeMap<Integer, FileData>> tempAllAgentNetworkFiles=new TreeMap<Integer,TreeMap<Integer,FileData>>();
		tempAllAgentNetworkFiles.putAll(allAgentNetworkFiles);
		TreeMap<Integer,String> copyLockList=new TreeMap<Integer,String>();
		copyLockList.putAll(nodeData1.lockRequestList);
		
		for (int fileHash : copyLockList.keySet()) 
		{
			for (Map.Entry<Integer, TreeMap<Integer, FileData>> entry : tempAllAgentNetworkFiles.entrySet())
			{
				TreeMap<Integer, FileData> nodeRepFiles =entry.getValue();
				int nodeID=entry.getKey();
				if (nodeRepFiles.containsKey(fileHash)&&(!nodeRepFiles.get(fileHash).isLock()))
				{
					nodeData1.lockRequestList.remove(fileHash);
					allAgentNetworkFiles.get(nodeID).get(fileHash).setLock(true);
					if(copyLockList.get(fileHash).equals("dl"))
					{
						int partID;
						int numberOfParts;
						int partSize;
						boolean sendFromRepOwner;
						FileData tempFile=entry.getValue().get(fileHash);
						FileData wantedFile=new FileData();
						wantedFile.deepCopy(tempFile);
						
						if(tempFile.isOwner(wantedFile.getReplicateOwnerID()))
						{
							partID=0;
							sendFromRepOwner=false;
							numberOfParts=wantedFile.getNumberOfOwners();
							partSize =(int)(Math.ceil(wantedFile.getSize()/numberOfParts));
						}
						else
						{
							partID=1;
							sendFromRepOwner=true;
							numberOfParts=wantedFile.getNumberOfOwners()+1;
							partSize =(int)(Math.ceil(wantedFile.getSize()/numberOfParts));
						}
						
						
						List<Integer> owners = new ArrayList<Integer>(wantedFile.getLocalOwners().keySet());
						
						if ((partSize>1)&&(numberOfParts>1))
						{
							wantedFile.setPartSize(partSize);
							wantedFile.setNumberOfParts(numberOfParts);
							wantedFile.setDestinationFolder("part");
							wantedFile.setDestinationID(nodeData1.getMyNodeID());
							wantedFile.setDestinationIP(nodeData1.getMyIP());
							
							if(sendFromRepOwner)
							{
								wantedFile.setPartID(partID);
								wantedFile.setSourceID(wantedFile.getReplicateOwnerID());
								wantedFile.setSourceIP(wantedFile.getReplicateOwnerIP());
								FileData file1=new FileData();
								file1.deepCopy(wantedFile);
								downloadMap.put(tempFile.getReplicateOwnerID(), file1);
							}
							
							for(int owner:owners)
							{
								partID=partID+1;
								wantedFile.setPartID(partID);
								wantedFile.setSourceID(owner);
								wantedFile.setSourceIP(wantedFile.getLocalOwners().get(owner));
								FileData file1=new FileData();
								file1.deepCopy(wantedFile);
								downloadMap.put(owner, file1);
							}
						}
						else
						{
							wantedFile.setDestinationID(nodeData1.getMyNodeID());
							wantedFile.setDestinationIP(nodeData1.getMyIP());
							wantedFile.setDestinationFolder("lok");
							wantedFile.setSourceID(owners.get(0));
							wantedFile.setSourceIP(wantedFile.getLocalOwners().get(owners.get(0)));
							downloadMap.put(owners.get(0), wantedFile);
						}
					}
					else if (copyLockList.get(fileHash).equals("rm"))
					{
						FileData fileToRemove=entry.getValue().get(fileHash);
						List<Integer> owners = new ArrayList<Integer>(fileToRemove.getLocalOwners().keySet());
						for(int owner:owners)
						{
							removeMap.put(owner, fileHash);
						}
					}
				}
			}
		}
	}
	public void checkAgentLockAction()
	{
		TreeMap<Integer, FileData> tempDownloadMap=new TreeMap<Integer, FileData>();
		tempDownloadMap.putAll(downloadMap);
		for(Entry<Integer, FileData> entry : tempDownloadMap.entrySet()) 
		{
			if (entry.getKey()== nodeData1.getMyNodeID())
			{
				nodeData1.sendQueue.add(entry.getValue());
				downloadMap.remove(entry.getKey());
			}
		}
		
		TreeMap<Integer, Integer> tempRemoveMap=new TreeMap<Integer, Integer>();
		tempRemoveMap.putAll(removeMap);
		for(Entry<Integer, Integer> entry : tempRemoveMap.entrySet()) 
		{
			if (entry.getKey()== nodeData1.getMyNodeID())
			{
				if(nodeData1.localFiles.containsKey(entry.getValue()))
				{
					String fileName=nodeData1.localFiles.get(entry.getValue()).getFileName();
					Path source = Paths.get(nodeData1.getMyLocalFolder()+"\\"+fileName);
					try {Files.delete(source);} catch (IOException e) {}
					removeMap.remove(entry.getKey());
				}
			}
		}
		
	}
	public void updateLocalAllFiles()
	{
		boolean changed=false;
		if(!(nodeData1.allNetworkFiles.size()==(allAgentNetworkFiles.size())))
		changed=true;
		else
		{
			for(Entry<Integer, TreeMap<Integer, FileData>> entry : allAgentNetworkFiles.entrySet()) 
			{
				if (!(nodeData1.allNetworkFiles.get(entry.getKey()).size()==entry.getValue().size()))
					changed=true;
			}
		}
		
		if (changed)
		{
			nodeData1.allNetworkFiles = allAgentNetworkFiles;
			nodeData1.setChanged(true);
		}
	}
	public void checkReplicationFiles(){
		TreeMap<Integer,FileData> nodeReplFiles = nodeData1.replFiles;
		for(Map.Entry<Integer,FileData> entry : nodeReplFiles.entrySet()) 
		{
		  Integer fileHash = entry.getKey();
		  FileData tempFD = entry.getValue();
		  TreeMap<Integer,String> localFileOwners =new TreeMap<Integer,String>();
		  localFileOwners.putAll(tempFD.getLocalOwners());
		  List<Integer> owners = new ArrayList<Integer>(localFileOwners.keySet());
		  for(int temp : owners)
		  {
			  if(temp == failedNodeID)
			  {
				  if(tempFD.removeOwner(temp))//returns true if no owners remain
				  {//if no local owners remain remove file from replication lists
					  nodeData1.replFiles.remove(fileHash);
					  Path source = Paths.get(tempFD.getFolderLocation()+"\\"+tempFD.getFileName());
					  try {Files.delete(source);} catch (IOException e) {}
				  }
				  else
				  {//if other owners exist update file in list
					  nodeData1.replFiles.put(fileHash,tempFD);
				  }
			  }
		  }
		}
	}
	public void checkReplicationOwnerOfLocalFiles()
	{
		TreeMap<Integer,FileData> nodeLocalFiles = new TreeMap<Integer,FileData>();
		nodeLocalFiles.putAll(nodeData1.localFiles);
		
		for(Map.Entry<Integer,FileData> entry : nodeLocalFiles.entrySet())
		{
			Integer fileHash = entry.getKey();
			FileData tempFD = entry.getValue();
			if(tempFD.getReplicateOwnerID() == failedNodeID)
			{
				tempFD.refreshReplicateOwner(nodeData1);
				nodeData1.sendQueue.add(tempFD);
				nodeData1.localFiles.put(fileHash,tempFD);
			}
		}
	}
}

