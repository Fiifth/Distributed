package nodeFileManagers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import networkFunctions.RMI;
import networkFunctions.TCP;
import nodeManager.RMICommunicationInt;
import nodeStart.NodeData;
import splitAndMerge.GetPartOfFile;

public class Sender extends Thread
{
	TCP tcp=new TCP();
	RMI rmi=new RMI();
	NodeData nodedata1;
	
	public Sender(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		try {Thread.sleep(2000);} catch (InterruptedException e1) {System.out.println("interrupted sleep");return;}
		
		while(!Thread.interrupted())
		{
			FileData file1=null;
			try 
			{
				file1 = nodedata1.sendQueue.take();
				nodedata1.setSending(true);
			} catch (InterruptedException e1) {return;}
			
			File temp1 = new File(nodedata1.getMyLocalFolder()+"\\"+file1.getFileName());
			File temp2 = new File(nodedata1.getMyReplFolder()+"\\"+file1.getFileName());
				
			if (temp1.exists()||temp2.exists())
			{
				if (file1.getDestinationFolder().equals("remove"))
				{
					removeFile(file1);
				}
				else if (file1.isOwner(nodedata1.getMyNodeID())&&file1.getDestinationFolder().equals("rep")&&file1.getDestinationID()==nodedata1.getMyNodeID())
				{
					copyFileLocally(nodedata1,file1);
					//Indien de lokale eigenaar hetzelfde is als de replicatie eigenaar kan het bestand simpelweg
					//gekopieerd worden.
				}
				else if (file1.getDestinationFolder().equals("rep"))
				{
					file1.setSourceIP(nodedata1.getMyIP());
					file1.setSourceID(nodedata1.getMyNodeID());
					
					if (!tellNodeToReceive(file1))
					{
						tcp.sendFile(file1);
						if (file1.getRemoveAfterSend()) 
							removeFile(file1);
					}
				}
				else if (file1.getDestinationFolder().equals("lok"))
				{
					file1.setFolderLocation(nodedata1.getMyLocalFolder());
					tellNodeToReceive(file1);
					tcp.sendFile(file1);
				}
				else if (file1.getDestinationFolder().equals("part"))
				{
					createPartOfFile(file1);
					tellNodeToReceive(file1);
					tcp.sendFile(file1);
					removeFile(file1);
				}
			}
			else
			{
				makeSomeoneElseSend(file1);
				/*
				 * Aangezien remove opdrachten eveneens in de zendqueue staand is het mogelijk
				 * dat een bestand verwijderd wordt en daarna doorgestuurd moet worden. Daarom
				 * Controleren we eerst of het bestand nog wel aanwezig is.
				 * Remove opdrachten worden aan de zendqueue toegevoegd zodat wanneer een bestand
				 * verwijderd moet worden, deze niet op hetzelfde moment doorgestuurd kan worden.
				 */
			}
			nodedata1.setSending(false);
		}
	}
	
	public void createPartOfFile(FileData file1)
	{
		Path source;
		
		if(nodedata1.localFiles.containsKey(Math.abs(file1.getFileName().hashCode()%32768)))
		{
			source = Paths.get(nodedata1.getMyLocalFolder()+"\\"+file1.getFileName());
		}
		else
			source = Paths.get(nodedata1.getMyReplFolder()+"\\"+file1.getFileName());

		GetPartOfFile partGetter=new GetPartOfFile();
		//de functie getPart maakt het stukje van de file aan dat verstuurd moet worden.
		Path dest = Paths.get(nodedata1.getMyReplFolder()+"\\"+file1.getFileName());
		partGetter.getPart(file1.getPartSize(), file1.getPartID(),source ,dest);
		file1.setFolderLocation(nodedata1.getMyReplFolder());
		file1.setFileName(file1.getFileName()+"."+String.format("%03d", file1.getPartID()));
	}
	public void removeFile(FileData file1)
	{
		Path source = Paths.get(file1.getFolderLocation()+"\\"+file1.getFileName());
		try {Files.delete(source);} catch (IOException e) {}
	}
	public boolean tellNodeToReceive(FileData file1)
	{
		RMICommunicationInt recInt=null;
		boolean filePresent=true;
		try 
		{
			recInt = (RMICommunicationInt) rmi.getRMIObject(file1.getDestinationID(), file1.getDestinationIP(), "RMICommunication");
			filePresent=recInt.receiveThisFile(file1);
		} catch (Exception e) {System.out.println("failed connection to RMI of the node"); filePresent=true;}
		
		return filePresent;
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
	
	public void copyFileLocally(NodeData nodedata1,FileData file1)
	{
		int fileNameHash=Math.abs(file1.getFileName().hashCode()%32768);
		Path source = Paths.get(nodedata1.getMyLocalFolder()+"\\"+file1.getFileName());
		Path destination = Paths.get(nodedata1.getMyReplFolder()+"\\"+file1.getFileName());
		try 
		{
			Files.copy(source,destination,StandardCopyOption.REPLACE_EXISTING);
			file1.setFolderLocation(nodedata1.getMyReplFolder());
			if (!nodedata1.replFiles.containsKey(fileNameHash))
		       {
		       		nodedata1.replFiles.put(fileNameHash,file1);
		       }
			else
			{
				FileData temp=nodedata1.replFiles.get(fileNameHash);
				temp.addOwner(file1.getSourceID(),file1.getSourceIP());
				nodedata1.replFiles.put(fileNameHash, temp);
			}
		} catch (IOException e) {System.out.println("couldn't copy file");}
	}
}
