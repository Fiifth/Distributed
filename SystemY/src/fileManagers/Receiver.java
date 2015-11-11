package fileManagers;

import neworkFunctions.TCP;
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
		while(true)
		{
			FileData file1=null;
			try {
				file1=nodedata1.receiveQueue.take();//Wait until entry is found
			} catch (InterruptedException e) {return;}
			
			receiveFile(file1,nodedata1.getMyReplFolder());
			}
		}
	
	public void receiveFile(FileData file1, String DirReplFiles) 
	{
        
        int serverPort = file1.getSourceID()+32768;
        String fileOutput = DirReplFiles+"\\"+file1.getFileName();
        System.out.println("receiveing file: "+fileOutput);
        
        //check if filename already exists, if not add to replFiles
        boolean fnExists = false;
        for (FileData tempfile : nodedata1.replFiles) 
    	{
        	if(tempfile.getFileName().equals(file1.getFileName()))
        	{
        		fnExists = true;
       		}
    	}
        if(!fnExists){
        	file1.setFolderLocation(DirReplFiles);
        	file1.setRemoveAfterSend(false);
        	nodedata1.replFiles.add(file1);
        }
        	System.out.println("looking for server");
        	tcp.receiveFile(file1.getSourceIP(), serverPort, fileOutput);
           
    }


	
}
