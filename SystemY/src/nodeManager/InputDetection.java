package nodeManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import nodeFileManagers.FileData;
import nodeStart.NodeData;

public class InputDetection extends Thread 
{
	NodeData nodedata1;
	public InputDetection(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		while(!Thread.interrupted())
		{
			Scanner reader = new Scanner(System.in);
			System.out.println("Commands: open-filename, remlok-filename, rem-filename, quit");
			String n = reader.next();
			if(n.contains("open-"))
			{
				String fileName=n.substring(5,n.length());
				open(fileName);
			}
			else if(n.contains("remlok-"))
			{
				String fileName=n.substring(7,n.length());
				removeLocal(fileName);
			}
			else if(n.contains("rem-"))
			{
				String fileName=n.substring(4,n.length());
				remove(fileName);
			}
			else if (n.contains("quit"))
			{
				nodedata1.setToQuit(true);
			}
		}
	}
	private void remove(String fileName) 
	{
		nodedata1.lockRequestList.put(Math.abs(fileName.hashCode()%32768), "rm");
	}
	private void removeLocal(String fileName) 
	{
		//remove local file
		int filehash=Math.abs(fileName.hashCode()%32768);
		int numberOfOwners = 1;
		if (nodedata1.replFiles.containsKey(filehash))
		{
			FileData file=nodedata1.replFiles.get(filehash);
			numberOfOwners=file.getNumberOfOwners();
		}
		else
		{
			TreeMap<Integer, TreeMap<Integer, FileData>> temp=new TreeMap<Integer, TreeMap<Integer, FileData>>();
			temp.putAll(nodedata1.allNetworkFiles);
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
			Path source = Paths.get(nodedata1.getMyLocalFolder()+"\\"+fileName);
			try {Files.delete(source);} catch (IOException e1) {}
		}
	}

	private void open(String fileName) 
	{
		int valuehash = Math.abs(fileName.hashCode()%32768);
		Desktop desktop = Desktop.getDesktop();
		if(nodedata1.localFiles.containsKey(valuehash))
		{
			//file is in localfolder
			File file = new File(nodedata1.getMyLocalFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}
		else if(nodedata1.replFiles.containsKey(valuehash))
		{
			//file is in replfolder
			File file = new File(nodedata1.getMyReplFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}
		else
		{
			//file needs to be downloaded to local folder
			nodedata1.lockRequestList.put(Math.abs(fileName.hashCode()%32768), "dl");
			while(!nodedata1.localFiles.containsKey(valuehash))
			{
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {}												
			}
			File file = new File(nodedata1.getMyLocalFolder() + "\\" + fileName);
			try {
				desktop.open(file);
			} catch (IOException e1) {}
		}										
	}

}
