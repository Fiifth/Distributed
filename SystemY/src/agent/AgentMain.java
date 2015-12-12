package agent;


import java.io.Serializable;
import java.util.Map;
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
		nodeData1.setChanged(networkFilesChanged);
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
				if (networkFilesChanged=true)
				{
					allAgentNetworkFiles.remove(nodeData1.getMyNodeID());
					allAgentNetworkFiles.put(nodeData1.getMyNodeID(),tempMyFilesOnNode);
					nodeData1.setChanged(networkFilesChanged);
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
						//not locked
						//lockFile()
						if(copyLockList.get(key).equals("dl"))
						{
							//make a download list
							//add to receive queue
							//local list that starts merging when empty
								//fileID,list of parts
								//remove parts when receiving
							//local owner,(nodedata)
							System.out.println(entry.getValue().get(key).getFileName());
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
		//TODO iterate download list
			//if key==my id
				//add to sendqueue
		//TODO iterate remove list
			//if key==my id
				//lookup fileID in local list -->remove
				//remove file
				
	}
	
	public void updateLocalAllFiles()
	{
		if(!nodeData1.allNetworkFiles.equals(allAgentNetworkFiles))
		{
			nodeData1.allNetworkFiles = allAgentNetworkFiles;
		}
	}
	
}
