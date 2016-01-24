package nodeFileManagers;
import java.util.TreeMap;

import nameServer.NameServerInterface;
import nodeStart.NodeData;

public class FileOwnershipT extends Thread
{
	NodeData nodedata1;
	NameServerInterface nameserver;
	int mode;
	int newNode;
	String newNodeIP;
	int newPrevID;
	public FileOwnershipT(NodeData nodedata1,int mode,int newNode,String newNodeIP, int newPrevID)
	{
		this.nodedata1=nodedata1;
		this.mode=mode;
		this.newNode=newNode;
		this.newNodeIP=newNodeIP;
		this.newPrevID=newPrevID;
	}
	
	public void run()
	{
		try {Thread.sleep(500);} catch (InterruptedException e1) {	}
		if (mode==1) //wordt niet meer gebruikt
		{
		TreeMap<Integer,FileData> tempRepFiles=new TreeMap<Integer, FileData>();
		tempRepFiles.putAll(nodedata1.replFiles);

		for (Integer key : tempRepFiles.keySet())
		{
			if (tempRepFiles.get(key).refreshReplicateOwner(nodedata1))
			{
				nodedata1.replFiles.remove(key);
				tempRepFiles.get(key).setRemoveAfterSend(true);
				nodedata1.sendQueue.add(tempRepFiles.get(key));
			}
		}
		}
		else if(mode==2) //controleerd of de nieuwe node een rep eigenaar van een file moet zijn
		{
			TreeMap<Integer,FileData> tempRepFiles=new TreeMap<Integer, FileData>();
			tempRepFiles.putAll(nodedata1.replFiles);

			for (Integer key : tempRepFiles.keySet())
			{
				int next=nodedata1.getNextNode();
				int my=nodedata1.getMyNodeID();
				if(((next<my)&&(key>next)&&(my>key))||((next>my)&&((key>next)||(key<my))))
				{
					nodedata1.replFiles.remove(key);
					tempRepFiles.get(key).setReplicateOwnerID(nodedata1.getNextNode());
					tempRepFiles.get(key).setReplicateOwnerIP(nodedata1.getNextNodeIP());
					tempRepFiles.get(key).setDestinationID(nodedata1.getNextNode());
					tempRepFiles.get(key).setDestinationIP(nodedata1.getNextNodeIP());
					tempRepFiles.get(key).setDestinationFolder("rep");
					nodedata1.sendQueue.add(tempRepFiles.get(key));
				}
			}
		}
		else if (mode==3) //zend alles naar vorige eigenaar (bij afsluiten)
		{
			TreeMap<Integer,FileData> tempRepFiles=new TreeMap<Integer, FileData>();
			tempRepFiles.putAll(nodedata1.replFiles);

			for (Integer key : tempRepFiles.keySet())
			{
				nodedata1.replFiles.remove(key);
				tempRepFiles.get(key).setReplicateOwnerID(nodedata1.getPrevNode());
				tempRepFiles.get(key).setReplicateOwnerIP(nodedata1.getPrevNodeIP());
				tempRepFiles.get(key).setDestinationID(nodedata1.getPrevNode());
				tempRepFiles.get(key).setDestinationIP(nodedata1.getPrevNodeIP());
				tempRepFiles.get(key).setDestinationFolder("rep");
				nodedata1.sendQueue.add(tempRepFiles.get(key));
			}
		}
		else if (mode==4) //controlleerd of rep eigenaar van locale bestanden veranderen bij nieuwe node
		{
			TreeMap<Integer,FileData> tempRepFiles=new TreeMap<Integer, FileData>();
			tempRepFiles.putAll(nodedata1.localFiles);

			for (Integer key : tempRepFiles.keySet())
			{
				int next=newNode;
				String nextIP=newNodeIP;
				int my=tempRepFiles.get(key).getReplicateOwnerID();
				if(((next<my)&&(key>next)&&(my>key))||((next>my)&&((key>next)||(key<my))))
				{
					nodedata1.localFiles.get(key).setReplicateOwnerID(next);
					nodedata1.localFiles.get(key).setReplicateOwnerIP(nextIP);
					if (nodedata1.isDebug())System.out.println("local file rep owner adjusted: "+nodedata1.localFiles.get(key).getFileName());
				}
			}
		}
		else if (mode==5) //zelfde als mode 4 maar voor wanneer een node weg gaat
		{
			TreeMap<Integer,FileData> tempRepFiles=new TreeMap<Integer, FileData>();
			tempRepFiles.putAll(nodedata1.localFiles);

			for (Integer key : tempRepFiles.keySet())
			{
				int leavingNode=newNode;
				int newOnwerID=newPrevID;
				String newOwnerIP=newNodeIP;
				int my=tempRepFiles.get(key).getReplicateOwnerID();
				if (my==leavingNode)
				{
					nodedata1.localFiles.get(key).setReplicateOwnerID(newOnwerID);
					nodedata1.localFiles.get(key).setReplicateOwnerIP(newOwnerIP);
					if (nodedata1.isDebug())System.out.println("local file rep owner adjusted: "+nodedata1.localFiles.get(key).getFileName());
				}
			}
		}
	}
}
