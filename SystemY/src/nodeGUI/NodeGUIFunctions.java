package nodeGUI;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

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
		//remove local file
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
					System.out.println("found= "+numberOfOwners);
				}
			}
		}
		if (numberOfOwners==1)
		{
			System.out.println("you are last lokal owner so I won't delete this");
		}
		else
		{
			Path source = Paths.get(nodedata2.getMyLocalFolder()+"\\"+fileName);
			try {Files.delete(source);} catch (IOException e1) {}
		}
	}

	public void open(String fileName, NodeData nodedata2) 
	{
		int valuehash = Math.abs(fileName.hashCode()%32768);
		Desktop desktop = Desktop.getDesktop();
		if(nodedata2.localFiles.containsKey(valuehash))
		{
			//file is in localfolder
			File file = new File(nodedata2.getMyLocalFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}
		else if(nodedata2.replFiles.containsKey(valuehash))
		{
			//file is in replfolder
			File file = new File(nodedata2.getMyReplFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}
		else
		{
			//file needs to be downloaded to local folder
			nodedata2.lockRequestList.put(Math.abs(fileName.hashCode()%32768), "dl");
			while(!nodedata2.localFiles.containsKey(valuehash))
			{
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {}												
			}
			File file = new File(nodedata2.getMyLocalFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}										
	}
}
