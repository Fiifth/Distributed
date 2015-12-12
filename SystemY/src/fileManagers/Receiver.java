package fileManagers;

import networkFunctions.TCP;
import nodeP.NodeData;

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
		String DestinationFolder;
		if(file1.getDestinationFolder().equals("rep")) 
		{
			
			DestinationFolder=nodedata1.getMyReplFolder();
			int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
			if (!nodedata1.replFiles.containsKey(fileNameHash))
		       {
		       		file1.setFolderLocation(DestinationFolder);
		       		file1.setRemoveAfterSend(false);
		       		nodedata1.replFiles.put(fileNameHash,file1);
		       		
		       } 
		}
		else if(file1.getDestinationFolder().equals("lok")) 
			DestinationFolder=nodedata1.getMyLocalFolder();
		else
			DestinationFolder=nodedata1.getMyReplFolder(); //TODO destination part
		
        int serverPort = file1.getSourceID()+32768;
        String fileOutput = DestinationFolder+"\\"+file1.getFileName(); 
        tcp.receiveFile(file1.getSourceIP(), serverPort, fileOutput); 
    }
}
