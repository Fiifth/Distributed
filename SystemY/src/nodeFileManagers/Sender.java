package nodeFileManagers;

/* 1) Connectie met server via RMI zodat bestemming van files opgevraagd kunnen worden
 * 2) Connectie met node waar file naartoe gestuurd moet worden 
 * 		-via RMI zijn IP en ID in een lijst plaatsen op bestemming
 * 3) Wacht op bestemming tot deze klaar is om het bestand te ontvangen
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
			RMICommunicationInt recInt=null;
			try 
			{
				file1 = nodedata1.sendQueue.take();
				nodedata1.setSending(true);
			} catch (InterruptedException e1) {return;}

			if (file1.isOwner(nodedata1.getMyNodeID())&&file1.getDestinationFolder().equals("rep")&&file1.getDestinationID()==nodedata1.getMyNodeID())
			{
				copyFileLocally(nodedata1,file1);	
			}
			else if (file1.getDestinationFolder().equals("rep"))
			{
				boolean filePresent;
				file1.setSourceIP(nodedata1.getMyIP());
				file1.setSourceID(nodedata1.getMyNodeID());
				try 
				{
					recInt = (RMICommunicationInt) rmi.getRMIObject(file1.getDestinationID(), file1.getDestinationIP(), "RMICommunication");
					filePresent=recInt.receiveThisFile(file1);
				} catch (Exception e) {System.out.println("failed connection to RMI of the node"); filePresent=true;}
				
				if (!filePresent)
				{
					sendFile(file1);
					if (file1.getRemoveAfterSend()) 
					{
						Path source = Paths.get(file1.getFolderLocation()+"\\"+file1.getFileName());
						try {Files.delete(source);} catch (IOException e) {}
					}
				}
			}
			else if (file1.getDestinationFolder().equals("lok"))
			{
				file1.setFolderLocation(nodedata1.getMyLocalFolder());
				
				try 
				{
					recInt = (RMICommunicationInt) rmi.getRMIObject(file1.getDestinationID(), file1.getDestinationIP(), "RMICommunication");
					recInt.receiveThisFile(file1);
				} catch (Exception e) {System.out.println("failed connection to RMI of the node");}
				
				sendFile(file1);
			}
			else if (file1.getDestinationFolder().equals("part"))
			{
				GetPartOfFile partGetter=new GetPartOfFile();
				Path source = Paths.get(nodedata1.getMyLocalFolder()+"\\"+file1.getFileName());
				Path dest = Paths.get(nodedata1.getMyReplFolder()+"\\"+file1.getFileName());
				partGetter.getPart(file1.getPartSize(), file1.getPartID(),source ,dest);
				file1.setFolderLocation(nodedata1.getMyReplFolder());
				file1.setFileName(file1.getFileName()+"."+String.format("%03d", file1.getPartID()));
				try 
				{
					recInt = (RMICommunicationInt) rmi.getRMIObject(file1.getDestinationID(), file1.getDestinationIP(), "RMICommunication");
					recInt.receiveThisFile(file1);
				} catch (Exception e) {System.out.println("failed connection to RMI of the node");}
				
				sendFile(file1);
			}
			nodedata1.setSending(false);
		}
	}
	public void sendFile(FileData file1)
	{
		String filePath = file1.getFolderLocation()+"\\"+file1.getFileName();
        ServerSocket welcomeSocket = null;
        Socket connectionSocket = null;
        BufferedOutputStream outToClient = null;
        FileInputStream fis = null;
        BufferedInputStream bis=null;
        try 
        {
            welcomeSocket = new ServerSocket(file1.getSourceID()+32768);
            connectionSocket = welcomeSocket.accept();
            outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
            welcomeSocket.close();
        } catch (IOException ex) { 	System.out.println("Couldn't open socket1");}

        if (outToClient != null) 
        {
            File myFile = new File(filePath);
            byte[] mybytearray = new byte[(int) myFile.length()];

            try 
            {
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
            } catch (FileNotFoundException ex) {System.out.println("File wasn't found!");}

            try 
            {
                bis.read(mybytearray, 0, mybytearray.length);
                outToClient.write(mybytearray, 0, mybytearray.length);
                outToClient.flush();
                outToClient.close();
                connectionSocket.close();
                fis.close();
				bis.close();
            } catch (IOException ex) {System.out.println("Sending file failed!"); } 
        }
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
