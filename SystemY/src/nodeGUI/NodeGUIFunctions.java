package nodeGUI;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import nodeFileManagers.FileData;
import nodeStart.NodeData;

public class NodeGUIFunctions 
{

	public void remove(String fileName, NodeData nodedata2) 
	{
		nodedata2.lockRequestList.put(Math.abs(fileName.hashCode()%32768), "rm");		
	}
	public void removeLocal(String fileName, NodeData nodedata2) 
	{
		int filehash=Math.abs(fileName.hashCode()%32768);
		int numberOfOwners = 1;
		if (nodedata2.replFiles.containsKey(filehash))
		{
			FileData file=nodedata2.replFiles.get(filehash);
			numberOfOwners=file.getNumberOfOwners();
		}
		else
		{
			TreeMap<Integer, TreeMap<Integer, FileData>> temp=new TreeMap<Integer, TreeMap<Integer, FileData>>();
			temp.putAll(nodedata2.allNetworkFiles);
			for (Map.Entry<Integer, TreeMap<Integer, FileData>> entry : temp.entrySet())
			{
				TreeMap<Integer, FileData> nodeRepFiles =entry.getValue();
				if (nodeRepFiles.containsKey(filehash))
				{
					FileData file=nodeRepFiles.get(filehash);
					numberOfOwners=file.getNumberOfOwners();
				}
			}
		}
		if (numberOfOwners==1)
		{
			System.out.println("You are last local owner. Use remove global function instead.");
		}
		else
		{
			FileData temp= nodedata2.localFiles.get(filehash);
			temp.setDestinationFolder("remove");
			nodedata2.sendQueue.add(temp);
		}		
	}

	public void open(String fileName, NodeData nodedata2) 
	{
		int valuehash = Math.abs(fileName.hashCode()%32768);
		Desktop desktop = Desktop.getDesktop();
		if(nodedata2.localFiles.containsKey(valuehash))
		{
			File file = new File(nodedata2.getMyLocalFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}
		else if(nodedata2.replFiles.containsKey(valuehash))
		{
			File file = new File(nodedata2.getMyReplFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}
		else if(isPresent(valuehash,nodedata2))
		{
			//file needs to be downloaded to local folder
			boolean failure=false;
			nodedata2.lockRequestList.put(Math.abs(fileName.hashCode()%32768), "dl");
			while(!nodedata2.localFiles.containsKey(valuehash)&&!failure)
			{
				try {Thread.sleep(1000);} catch (InterruptedException e1) {}	
				failure=nodedata2.allNetworkFiles.isEmpty()&&nodedata2.isAbortOpening();
			}
			nodedata2.setAbortOpening(false);
			if (!failure)
			{
				File file = new File(nodedata2.getMyLocalFolder() + "\\" + fileName);
				try {desktop.open(file);} catch (IOException e1) {}
			}
		}										
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
