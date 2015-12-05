package fileManagers;
import java.util.Iterator;
import java.util.Map;

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
		
		Iterator<Map.Entry<Integer, FileData>> iterator = nodedata1.replFiles.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<Integer, FileData> entry = iterator.next();
			if (entry.getValue().refreshReplicateOwner(nodedata1))
			{
				entry.getValue().setRemoveAfterSend(true);
				nodedata1.sendQueue.add(entry.getValue());
				iterator.remove();
			}
		}
	}
}
