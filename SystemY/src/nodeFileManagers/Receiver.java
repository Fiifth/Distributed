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
				nodedata1.plusReceive();
			} 
			catch (InterruptedException e) {return;}
			final FileData file=file1;
			new Thread() 
			{
	            public void run() 
	            {
	            	receiveFile(file,nodedata1); 
	            	nodedata1.minReceive();
	            }
			 }.start();
		}
	}
	
	public void receiveFile(FileData file1, NodeData nodedata1) 
	{
		String fileOutput;
		int oldNextNode=nodedata1.getNextNode();
		if(file1.getDestinationFolder().equals("rep")) 
		{
			fileOutput = nodedata1.getMyReplFolder()+"\\"+file1.getFileName();
		}
		else if(file1.getDestinationFolder().equals("lok")) 
		{
			fileOutput = nodedata1.getMyLocalFolder()+"\\"+file1.getFileName();
		}
		else 
			fileOutput = nodedata1.getMyReplFolder()+"\\"+file1.getFileName();
		
        int serverPort = file1.getSourceID()+32768;
        
        if (tcp.receiveFile(file1.getSourceIP(), serverPort, fileOutput))
        {
	        if (file1.getDestinationFolder().equals("part") )
	        {	        
		        if(file1.getFileName().length()>=5) //extra controle
		        {
		        	//bestandnaam.*** (***is part nummer)
		        	String fileName= file1.getFileName().substring(0, file1.getFileName().length() - 4);
			        int fileNameHash=Math.abs(fileName.hashCode()%32768);
			     	//we zorgen ervoor dat verschillende threads de addAPart functie niet tegelijk oproepen
			        //door gebruik te maken van een Semaphore.
			        nodedata1.acquire();
			        //de functie addAPart zorgt voor een lijst met parts. Wanneer deze compleet is worden 
			        //de parts samengevoegd.
					nodedata1.addAPart(fileNameHash, fileOutput,file1.getNumberOfParts(), fileName);
					nodedata1.release();
		        }
	        }
	        else if(file1.getDestinationFolder().equals("rep")) 
			{
	        	int currentNextNode=nodedata1.getNextNode();
	        	int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
				if (!nodedata1.replFiles.containsKey(fileNameHash))
			    {
			       	file1.setFolderLocation(nodedata1.getMyReplFolder());
			       	nodedata1.replFiles.put(fileNameHash,file1);
			       	if (currentNextNode!=oldNextNode)
			       	{
			       		FileOwnershipT COT =new FileOwnershipT(nodedata1);
			    		COT.start();
			       	}
			    } 
			}
        }
    }
}
