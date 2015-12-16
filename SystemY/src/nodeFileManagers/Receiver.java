package nodeFileManagers;

import networkFunctions.TCP;
import nodeStart.NodeData;

public class Receiver extends Thread 
{
	TCP tcp=new TCP();
	NodeData nodedata1;
	
	public Receiver(NodeData nodedata1)
	{
		super();
		this.nodedata1=nodedata1;
	}
	
	public void run() 
	{
		while(!Thread.interrupted())
		{
			FileData file1=null;
			try 
			{
				file1=nodedata1.receiveQueue.take();
			} 
			catch (InterruptedException e) {return;}
			receiveFile(file1,nodedata1); 
			}
		}
	
	public void receiveFile(FileData file1, NodeData nodedata1) 
	{
		String fileOutput;
		if(file1.getDestinationFolder().equals("rep")) 
		{
			fileOutput = nodedata1.getMyReplFolder()+"\\"+file1.getFileName();
			int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
			if (!nodedata1.replFiles.containsKey(fileNameHash))
		       {
		       		file1.setFolderLocation(nodedata1.getMyReplFolder());
		       		file1.setRemoveAfterSend(false);
		       		nodedata1.replFiles.put(fileNameHash,file1);
		       		
		       } 
		}
		else if(file1.getDestinationFolder().equals("lok")) 
		fileOutput = nodedata1.getMyLocalFolder()+"\\"+file1.getFileName();
		else //TODO make partFileFolder
		fileOutput = nodedata1.getMyReplFolder()+"\\"+file1.getFileName()+"."+String.format("%03d", file1.getPartID());
		
        int serverPort = file1.getSourceID()+32768;
   
        tcp.receiveFile(file1.getSourceIP(), serverPort, fileOutput); 
        int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
        if(file1.getDestinationFolder().equals("part"));
        {
        	nodedata1.removeFromPartMap(fileNameHash, file1.getSourceID());
        }
    }
}
