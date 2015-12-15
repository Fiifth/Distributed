package agent;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import nodeP.NodeData;
import fileManagers.FileData;

public class AgentMain extends Thread implements Serializable
{
	private static final long serialVersionUID = 1L;
	public TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles;
	public TreeMap<Integer,FileData> downloadMap; //local owner,filedata
	public TreeMap<Integer,Integer> removeMap; //local owner,fileID
	public boolean networkFilesChanged;
	
	
	NodeData nodeData1;
	NodeData failedNodeData;
	boolean typeOfAgent;
	public AgentMain(boolean typeOfAgent, TreeMap<Integer, TreeMap<Integer,FileData>> allAgentNetworkFiles,TreeMap<Integer,FileData> downloadMap, NodeData failedNodeData)
	{
		this.typeOfAgent = typeOfAgent;
		this.allAgentNetworkFiles = allAgentNetworkFiles;
		this.downloadMap=downloadMap;
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
			//Update agent's network files list
			updateAgentNetworkFiles();
			//Check for lock requests before updating local node's file list
			attemptToLock();
			//Iterate local locks and add to send list
			checkAgentLockAction();
			//Update local node's file list
			updateLocalAllFiles();	
		}
		else
		{
			
		}
	}

	public void updateAgentNetworkFiles()
	{
	
		TreeMap<Integer, TreeMap<Integer,FileData>> temp=new TreeMap<Integer, TreeMap<Integer,FileData>>();
		temp.putAll(allAgentNetworkFiles);
		networkFilesChanged=false;
		if(allAgentNetworkFiles.containsKey(nodeData1.getMyNodeID()))
		{
			if(!(allAgentNetworkFiles.get(nodeData1.getMyNodeID()) ==nodeData1.replFiles))
			{
				
				TreeMap<Integer,FileData> tempMyFilesOnNode=new TreeMap<Integer,FileData>();
				tempMyFilesOnNode.putAll(allAgentNetworkFiles.get(nodeData1.getMyNodeID()));
				for (int key : nodeData1.replFiles.keySet())
				{
					if(!(tempMyFilesOnNode.containsKey(key)))
					{
						tempMyFilesOnNode.put(key,nodeData1.replFiles.get(key));
						networkFilesChanged=true;
					}
					else if (!tempMyFilesOnNode.get(key).getLocalOwners().equals(nodeData1.replFiles.get(key).getLocalOwners()))
					{
						tempMyFilesOnNode.put(key,nodeData1.replFiles.get(key));
						//TODO check if lock
						networkFilesChanged=true;
					}
				}
				TreeMap<Integer,FileData> tempMyFilesOnNode2=new TreeMap<Integer,FileData>();
				tempMyFilesOnNode2.putAll(tempMyFilesOnNode);
				for (int key : tempMyFilesOnNode2.keySet())
				{
					if(!(nodeData1.replFiles.containsKey(key)))
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
		}
		else
			allAgentNetworkFiles.put(nodeData1.getMyNodeID(), nodeData1.replFiles);
	}
	

	public void attemptToLock()
	{
		
		TreeMap<Integer, TreeMap<Integer, FileData>> tempAllAgentNetworkFiles=new TreeMap<Integer,TreeMap<Integer,FileData>>();
		tempAllAgentNetworkFiles.putAll(allAgentNetworkFiles);
		TreeMap<Integer,String> copyLockList=nodeData1.lockRequestList;
		
		for (int key : copyLockList.keySet()) 
		{
			for (Map.Entry<Integer, TreeMap<Integer, FileData>> entry : tempAllAgentNetworkFiles.entrySet())
			{
				if (entry.getValue().containsKey(key))
				{
					//file found
					if (!entry.getValue().get(key).isLock())
					{
						//TODO remove from downloadlist
						allAgentNetworkFiles.get(entry.getKey()).get(key).setLock(true);
						if(copyLockList.get(key).equals("dl"))
						{
							//TODO check for length (if size<length localOwners.size don't use parts)
							//TODO check if sizelocalowners <1 don't use parts
							int partID=1;
							ArrayList<Integer> parts=new ArrayList<Integer>();
							for(int owner:entry.getValue().get(key).getLocalOwners())
							{
								parts.add(partID);
								//TODO file1.setPartSize
								entry.getValue().get(key).setPartID(partID);
								entry.getValue().get(key).setDestinationFolder("part");
								downloadMap.put(owner, entry.getValue().get(key));
								//nodeData1.receiveQueue.add(entry.getValue().get(key));
								partID++;
								System.out.println(entry.getValue().get(key).getFileName()+"-"+owner);
							}
							nodeData1.partMap.put(key, parts);
							//TODO generate merge list (still has to be added to nodedata
							
						}
						else if (copyLockList.get(key).equals("rm"))
						{
							//make remove list
							//(lokal owner,fileID)
						}
					}
				}
			}
		}
	}
	
	public void checkAgentLockAction()
	{
		/*for(Entry<Integer, FileData> entry : downloadMap.entrySet()) 
		{
			if (entry.getKey()== nodeData1.getMyNodeID())
			{
				nodeData1.sendQueue.add(entry.getValue());
			}
		}*/
		//TODO iterate remove list
			//if key==my id
				//lookup fileID in local list -->remove
				//remove file	
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
	
}
