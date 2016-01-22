package nodeFileManagers;

import java.util.ArrayList;
import java.util.List;

import networkFunctions.RMI;
import networkFunctions.TCP;
import nodeManager.RMICommunicationInt;
import nodeStart.NodeData;

public class Receiver extends Thread 
{
	TCP tcp=new TCP();
	RMI rmi=new RMI();
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
        	if(nodedata1.isDebug()) System.out.println("received "+file1.getDestinationFolder()+" file: "+file1.getFileName());
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
        else if (file1.getDestinationFolder().equals("rep")&&!file1.getLocalOwners().containsKey(file1.getSourceID()))
        {
        	makeSomeoneElseSend(file1);
        	//indien deze node een nieuwe replicatie eigenaar van een file moest worden
        	//maar er iets misliep met het zenden
        }
    }
	public boolean makeSomeoneElseSend(FileData file1)
	{
		List<Integer> owners = new ArrayList<Integer>(file1.getLocalOwners().keySet());
		boolean send=false;
		for (Integer owner:owners)
		{
			if(!send)
			{
				int id=owner;
				String ip=file1.getLocalOwners().get(id);
				send = tellNodeToSend(file1, id, ip);
			}
		}
		return send;
	}
	public boolean tellNodeToSend(FileData file1, int id, String ip)
	{
		RMICommunicationInt recInt=null;
		boolean present=false;
		try 
		{
			recInt = (RMICommunicationInt) rmi.getRMIObject(id, ip, "RMICommunication");
			present=recInt.sendThisFile(file1);
		} catch (Exception e) {return false;}
		return present;
	}
}
