package fileManagers;
//TODO make this thread a manager that runs in the backgrounds an waits from an entry from 
//a blocking queue before checking all the replFiles
import java.rmi.Naming;
import java.util.ArrayList;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileOwnershipT extends Thread
{
	NodeData nodedata1;
	NameServerInterface nameserver;
	public FileOwnershipT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	public void run()
	{
		ArrayList<String> removeFileList=new ArrayList<String>();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {e1.printStackTrace();
		}
	for (String fileNameAndDir : nodedata1.replFiles) 
	{
		//replFiles cointains ipAndNameAndDirArray[1]+DirReplFiles
		String[] fileNameAndDirArray=fileNameAndDir.split("-");
		String ipAndID = null;
		try {
			nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			ipAndID = nameserver.locateFile(fileNameAndDirArray[0]);
		} catch (Exception e) {System.out.println("failed connect to RMI of the server and get ip");}
		
		String[] ipAndIDArray = ipAndID.split("-");

		if (Integer.parseInt(ipAndIDArray[1])!=nodedata1.getMyNodeID())
		{
			System.out.println("Changing owner to:"+ Integer.parseInt(ipAndIDArray[1]));
			nodedata1.toSendFileNameAndDirList.add(fileNameAndDir);
			removeFileList.add(fileNameAndDir);
		}
	}
		for (String temp:removeFileList)
		{
			nodedata1.replFiles.remove(temp);
			//TODO remove the file from folder after sending
		}
	
	}
}
