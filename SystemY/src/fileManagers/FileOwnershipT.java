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
		try {Thread.sleep(2000);} catch (InterruptedException e1) {	}
		//ArrayList<FileData> tempreplFiles = new ArrayList<FileData> (nodedata1.replFiles.size());
		//Collections.copy(tempreplFiles, nodedata1.replFiles);
		@SuppressWarnings("unchecked")
		ArrayList<FileData> tempreplFiles = (ArrayList<FileData>) nodedata1.replFiles.clone();
		for (FileData file1 : tempreplFiles) 
		{
			if (file1.refreshReplicateOwner(nodedata1,file1))
			{
					file1.setRemoveAfterSend(true);
					nodedata1.sendQueue.add(file1);
					nodedata1.replFiles.remove(file1);
			}
		}
	}
}
