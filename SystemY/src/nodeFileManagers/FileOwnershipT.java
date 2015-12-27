package nodeFileManagers;
import java.util.TreeMap;

import nameServer.NameServerInterface;
import nodeStart.NodeData;

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
		try {Thread.sleep(500);} catch (InterruptedException e1) {	}
		TreeMap<Integer,FileData> tempRepFiles=new TreeMap<Integer, FileData>();
		tempRepFiles.putAll(nodedata1.replFiles);

		for (Integer key : tempRepFiles.keySet())
		{
			if (tempRepFiles.get(key).refreshReplicateOwner(nodedata1)) //return waarde geeft weer of rep eigenaar veranderd is
			{
				nodedata1.replFiles.remove(key);
				tempRepFiles.get(key).setRemoveAfterSend(true);
				nodedata1.sendQueue.add(tempRepFiles.get(key));
			}
		}
	}
}
