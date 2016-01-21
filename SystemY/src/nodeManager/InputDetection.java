package nodeManager;

import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import nodeFileManagers.FileData;
import nodeGUI.NodeGUIFunctions;
import nodeStart.NodeData;

public class InputDetection extends Thread 
{
	NodeData nodedata1;
	NodeGUIFunctions nodeFunctions=new NodeGUIFunctions();
	public InputDetection(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		Scanner reader = null;
		while(!Thread.interrupted())
		{
			reader = new Scanner(System.in);
			System.out.println("Commands: open-filename, remlok-filename, rem-filename, show, quit");
			String n = reader.next();
			if(n.contains("open-"))
			{
				String fileName=n.substring(5,n.length());
				nodeFunctions.open(fileName,nodedata1);
			}
			else if(n.contains("remlok-"))
			{
				String fileName=n.substring(7,n.length());
				nodeFunctions.removeLocal(fileName,nodedata1);
			}
			else if(n.contains("rem-"))
			{
				String fileName=n.substring(4,n.length());
				nodeFunctions.remove(fileName,nodedata1);
			}
			else if (n.contains("quit"))
			{
				nodedata1.setToQuit(true);
			}
			else if (n.contains("show"))
			{
				showFiles(nodedata1);
			}
		}
		reader.close();
	}
	private void showFiles(NodeData nodedata2) 
	{
		System.out.println("network files: ");
		TreeMap<Integer, TreeMap<Integer, FileData>>  tempAllNetworkFiles = new TreeMap<Integer, TreeMap<Integer, FileData>> ();
		tempAllNetworkFiles.putAll(nodedata2.allNetworkFiles);
         
		for (Entry<Integer, TreeMap<Integer, FileData>> entry : tempAllNetworkFiles.entrySet())
        {
			if (entry.getKey()==nodedata2.getMyNodeID())
				System.out.print("*");
			System.out.print(entry.getKey()+": ");
        	for (FileData temp : entry.getValue().values())
        	{
        		System.out.print(temp.getFileName()+"; ");
        	}
        	System.out.println("");
        }
		System.out.print("local files: ");
		
		TreeMap<Integer, FileData> local=new TreeMap<Integer, FileData>();
		local.putAll(nodedata2.localFiles);
		
		for (FileData value : local.values())
    	{
			System.out.print(value.getFileName()+"; ");
    	}
		System.out.println("");
	}

}
