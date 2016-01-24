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
	public final TreeMap<Integer,Integer> absentCounter=new TreeMap<Integer,Integer>(); //local owner,fileID
	public boolean networkFilesChanged;
	public int startingNodeID;
	
	NodeData nodeData1;
	int failedNodeID;
	boolean typeOfAgent;
	public AgentMain(boolean typeOfAgent, TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles,TreeMap<Integer,FileData> downloadMap,TreeMap<Integer,Integer> removeMap, int failedNodeID, int failStarterID)
	{
		this.typeOfAgent = typeOfAgent;
		this.allAgentNetworkFiles = allAgentNetworkFiles;
		this.downloadMap=downloadMap;
		this.removeMap=removeMap;
		this.failedNodeID = failedNodeID;
		this.startingNodeID = failStarterID;
	}
	public void setNodeData1(NodeData nodeData1) 
	{
		this.nodeData1 = nodeData1;
	}
	public void run() 
	{
		if(typeOfAgent)
		{
			while(nodeData1.isFApresent()){}
			nodeData1.setFApresent(true);
			//Update agent's network files list
			updateAgentNetworkFiles();
			//Iterate local locks and add actions for other nodes
			attemptToLock();
			//Check if current node has to take action (send/remove)
			checkAgentLockAction();
			//Update local node's file list
			updateLocalAllFiles();
			checkIfFilesAreReplicated();
			nodeData1.setFApresent(false);			
		}
		else
		{
			nodeData1.setFApresent(true);
			nodeData1.allNetworkFiles.clear();
			nodeData1.setChanged(true);
			//check if local owners equals failing node id, if so remove owner from owners list
			checkReplicationFiles();
			checkReplicationOwnerOfLocalFiles();
			nodeData1.setFApresent(false);	
		}
	}
	
	public void updateAgentNetworkFiles()
	{
		//Deze functie update de Treemap als de node een verandering heeft bij zijn replicatie files
		//Hierbij worden de volledig lijst vervangen indien er verandering zijn gevonden.
		//Wanneer de eigenaar lijst van de file gewijzigd, wanneer er een file meer of minder in de lijst staat. 
		//wanneer iemand een file download/verwijderd zal de lock automatisch verdwijnen aangezien een
		//eigenaar van de file toegevoegd/verwijderd wordt.
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
		//Bij deze functie wordt de lokale lock lijst afgegaan. Wanneer de file die men wenst te locken
		//unlocked gevonden wordt zal verdere actie ondernomen worden.
		//De actie kan downloaden of verwijderen zijn. De actie die ondernomen moet worden zal toegevoegd
		//worden aan een lijst die de agent bijhoud. Deze lijst bevat de ID van de node die een actie moet
		//ondernemen en wat hij juist moet doen.
		
		//opm: bij doorsturen van parts zal de replicatie eigenaar enkel een stuk doorsturen indien
		//hij geen gewone eigenaar is (anders zou hij 2 parts moeten doorsturen)
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
						if (nodeData1.partMap.containsKey(fileHash))
							nodeData1.partMap.remove(fileHash);
						if(tempFile.isOwner(wantedFile.getReplicateOwnerID()))
						{
							partID=0;
							sendFromRepOwner=false;
							numberOfParts=wantedFile.getNumberOfOwners();
							partSize =(int)(Math.floor((wantedFile.getSize()+2)/numberOfParts));
						}
						else
						{
							partID=1;
							sendFromRepOwner=true;
							numberOfParts=wantedFile.getNumberOfOwners()+1;
							partSize =(int)(Math.ceil((wantedFile.getSize()+2)/numberOfParts));
						}
						
						
						List<Integer> owners = new ArrayList<Integer>(wantedFile.getLocalOwners().keySet());
						
						if ((partSize>1)&&(numberOfParts>1)) //kan het bestand wel in stukken doorgestuurd worden?
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
						else //zend het bestand in zijn geheel
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
		//controlleer of huidige node een actie moet ondernemen (doorsturen/verwijderen van een bestand)
		
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
					FileData temp= nodeData1.localFiles.get(entry.getValue());
					temp.setDestinationFolder("remove");
					nodeData1.sendQueue.add(temp);
					removeMap.remove(entry.getKey());
				}
			}
		}
		
	}
	public void updateLocalAllFiles()
	{
		//Deze functie zorgt ervoor dat de GUI weet wanneer de netwerk bestanden lijst aangepast is
		//We updaten hier eveneens de lokale lijst met alle bestanden
		boolean changed=false;
		if(!(nodeData1.allNetworkFiles.size()==(allAgentNetworkFiles.size())))
		changed=true;
		else
		{
			for(Entry<Integer, TreeMap<Integer, FileData>> entry : allAgentNetworkFiles.entrySet()) 
			{
				if (nodeData1.allNetworkFiles.containsKey(entry.getKey()))
				{
				if (!(nodeData1.allNetworkFiles.get(entry.getKey()).size()==entry.getValue().size()))
					changed=true;
				}
				else
				changed=true;
			}
		}
		nodeData1.allNetworkFiles = allAgentNetworkFiles;
		if (changed)
		{
			nodeData1.setChanged(true);
		}
	}
	public void checkReplicationFiles(){
		
		TreeMap<Integer,FileData> nodeReplFiles = new TreeMap<Integer,FileData>();
		nodeReplFiles.putAll(nodeData1.replFiles);
		if(!nodeReplFiles.isEmpty())
		{
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
						FileData fd = nodeData1.replFiles.get(fileHash);
						if(fd.removeOwner(temp))//returns true if no owners remain
						{//if no local owners remain remove file from replication lists
							nodeData1.replFiles.remove(fileHash);
							Path source = Paths.get(fd.getFolderLocation()+"\\"+tempFD.getFileName());
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
	}
	public void checkReplicationOwnerOfLocalFiles()
	{
		TreeMap<Integer,FileData> nodeLocalFiles = new TreeMap<Integer,FileData>();
		nodeLocalFiles.putAll(nodeData1.localFiles);
		if(!nodeLocalFiles.isEmpty())
		{
			for(Map.Entry<Integer,FileData> entry : nodeLocalFiles.entrySet())
			{
				Integer fileHash = entry.getKey();
				FileData fd = nodeData1.localFiles.get(fileHash);
				if(fd.getReplicateOwnerID() == failedNodeID)
				{
					fd.refreshReplicateOwner(nodeData1);
					nodeData1.sendQueue.add(fd);
					nodeData1.localFiles.put(fileHash,fd);
				}
			}
		}
	}
	private void checkIfFilesAreReplicated() 
	{
		TreeMap<Integer,FileData> nodeLocalFiles = new TreeMap<Integer,FileData>();
		nodeLocalFiles.putAll(nodeData1.localFiles);
		if(!nodeLocalFiles.isEmpty())
		{
			for(Map.Entry<Integer,FileData> entry : nodeLocalFiles.entrySet())
			{
				Integer fileHash = entry.getKey();
				FileData fd = nodeData1.localFiles.get(fileHash);
				if((!isPresent(fileHash,nodeData1))&&(!nodeData1.isSending()))
				{
					if(!absentCounter.containsKey((fileHash*100000)+nodeData1.getMyNodeID()))
						absentCounter.put((fileHash*100000)+nodeData1.getMyNodeID(), 1);
					else
					{
						absentCounter.put((fileHash*100000)+nodeData1.getMyNodeID(), absentCounter.get((fileHash*100000)+nodeData1.getMyNodeID())+1);
						if(absentCounter.get((fileHash*100000)+nodeData1.getMyNodeID())>5)
						{
							if(nodeData1.isDebug())System.out.println(fd.getFileName()+"was not found on network");
							nodeData1.sendQueue.add(fd);
							absentCounter.remove((fileHash*100000)+nodeData1.getMyNodeID());
						}
						else if(absentCounter.get((fileHash*100000)+nodeData1.getMyNodeID())>2)
						{
							fd.refreshReplicateOwner(nodeData1);
						}
					}
				}
			}
		}
		if (absentCounter.size()>100) absentCounter.clear();
	}
	public boolean isPresent(int fileHash,NodeData nodedata2)
	{
		boolean present=false;
		TreeMap<Integer, TreeMap<Integer, FileData>>  tempAllNetworkFiles = new TreeMap<Integer, TreeMap<Integer, FileData>> ();
		tempAllNetworkFiles.putAll(nodedata2.allNetworkFiles);
         
		for (Entry<Integer, TreeMap<Integer, FileData>> entry : tempAllNetworkFiles.entrySet())
        {
        	for (int temp : entry.getValue().keySet())
        	{
        		if(temp==fileHash)
        			present=true;
        	}
        }
		return present;
	}
}

