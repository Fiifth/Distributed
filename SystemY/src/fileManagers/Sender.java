package fileManagers;

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
import neworkFunctions.RMI;
import neworkFunctions.TCP;
import nodeP.NodeData;
import nodeP.RMICommunicationInt;

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
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {System.out.println("interrupted sleep");}
		while(true)
			{
			
			FileData file1=null;
			 RMICommunicationInt recInt=null;
			try {
				file1 = nodedata1.sendQueue.take();
			} catch (InterruptedException e1) {return;}

			
			if (file1.getLocalOwnerID()!=file1.getReplicateOwnerID())
			{
				try {
					recInt = (RMICommunicationInt) rmi.getRMIObject(file1.getReplicateOwnerID(), file1.getReplicateOwnerIP(), "RMICommunication");
					file1.setSourceIP(nodedata1.getMyIP());
					file1.setSourceID(nodedata1.getMyNodeID());
					recInt.receiveThisFile(file1);
				} catch (Exception e) {System.out.println("failed connection to RMI of the node");}
				
				sendFile(file1);
				if (file1.getRemoveAfterSend())
						{
					nodedata1.removeQueue.add(file1);
					
						}
			}
			else
			{
				System.out.print("the file exist locally: ");
				copyFileLocally(nodedata1,file1);
			}
		}

	}
	public void sendFile(FileData file1) //TODO change to TCP.sendFile
	{
		String filePath = file1.getFolderLocation()+"\\"+file1.getFileName();
		System.out.print("Sending following file: "+ filePath+": ");
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            BufferedOutputStream outToClient = null;
            FileInputStream fis = null;
            //tcp.sendFile(filePath, file1.getSourceID()+3276);
            try {
                welcomeSocket = new ServerSocket(file1.getSourceID()+32768);
                connectionSocket = welcomeSocket.accept();
                outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
                welcomeSocket.close();
            } catch (IOException ex) { 	System.out.println("Couldn't open socket1");}

            if (outToClient != null) 
            {
            	
                File myFile = new File(filePath);
                byte[] mybytearray = new byte[(int) myFile.length()];

                try {
                    fis = new FileInputStream(myFile);
                } catch (FileNotFoundException ex) {System.out.println("File wasn't found!");}
                BufferedInputStream bis = new BufferedInputStream(fis);

                try {
                    bis.read(mybytearray, 0, mybytearray.length);
                    outToClient.write(mybytearray, 0, mybytearray.length);
                    outToClient.flush();
                    outToClient.close();
                    connectionSocket.close();
                    fis.close();
					bis.close();
                } catch (IOException ex) {System.out.println("Sending file failed!"); } 
                System.out.println("file send!");
        }
	}
	public void copyFileLocally(NodeData nodedata1,FileData file1)
	{
		Path source = Paths.get(nodedata1.getMyLocalFolder()+"\\"+file1.getFileName());
		Path destination = Paths.get(nodedata1.getMyReplFolder()+"\\"+file1.getFileName());
		try {
			Files.copy(source,destination,StandardCopyOption.REPLACE_EXISTING);
			System.out.println("copy is done");
			file1.setFolderLocation(nodedata1.getMyReplFolder());
			nodedata1.replFiles.add(file1);
		} catch (IOException e) {System.out.println("couldn't copy file");}
	}
}
