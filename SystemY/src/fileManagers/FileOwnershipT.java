package fileManagers;
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
		ArrayList<FileData> removeFileList=new ArrayList<FileData>();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {e1.printStackTrace();
		}
	for (FileData file1 : nodedata1.replFiles) 
	{
		
		boolean newRepOwner =file1.refreshReplicateOwner(nodedata1,file1);
		if (newRepOwner)
		{
			System.out.println("Changing owner to:"+ file1.getReplicateOwnerID());
			nodedata1.toSendFileNameAndDirList.add(file1);
			removeFileList.add(file1);
		}
	}
		for (FileData file1:removeFileList)
		{
			nodedata1.replFiles.remove(file1);
			//TODO remove the file from folder after sending (remove file manager)
		}
	
	}
}
