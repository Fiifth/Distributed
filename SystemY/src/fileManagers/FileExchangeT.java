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
import java.rmi.Naming;
import nodeP.NodeData;

public class FileExchangeT extends Thread
{
	NodeData nodedata1;
	FileReceiverInt recInt;
	
	public FileExchangeT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {System.out.println("interrupted sleep");}
		while(nodedata1.getToLeave()==0)
			{
			
			FileData file1=null;
			boolean newRepOwner=true;
			try {
				file1 = nodedata1.toSendFileNameAndDirList.take();
			} catch (InterruptedException e1) {System.out.println("interrupted while waiting for queue");}
			if (!file1.getReplicateDataSet())
			{
				newRepOwner=file1.refreshReplicateOwner(nodedata1,file1);
			}
			else
			{
				newRepOwner= true;
			}
			
			if (newRepOwner)
			{
				try {
					recInt = (FileReceiverInt)Naming.lookup("//"+file1.getReplicateOwnerIP()+":"+file1.getReplicateOwnerID()+"/FileReceiverT");
					recInt.receiveThisFile(file1);
				} catch (Exception e) {System.out.println("failed connect to RMI of the node");}
				
				sendFile(file1);
			}
			else
			{
				System.out.print("the file exist locally: ");
				Path source = Paths.get(nodedata1.getMyLocalFolder());
				Path destination = Paths.get(nodedata1.getMyReplFolder());
				try {
					Files.copy(source,destination,StandardCopyOption.REPLACE_EXISTING);
					System.out.println("copy is done");
					nodedata1.replFiles.add(file1);
				} catch (IOException e) {System.out.println("couln't copy file");}
				
			}
		}

	}
	public void sendFile(FileData file1)
	{
		String filePath = file1.getFolderLocation()+"\\"+file1.getFileName();
		System.out.println("Sending following file: "+ filePath);
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            BufferedOutputStream outToClient = null;
            FileInputStream fis = null;

            try {
                welcomeSocket = new ServerSocket(3248);
                connectionSocket = welcomeSocket.accept();
                outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
                welcomeSocket.close();
            } catch (IOException ex) { 	System.out.println("Couldn't open socket1");}

            if (outToClient != null) 
            {
            	System.out.println("sending file");
                File myFile = new File( filePath );
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

        }
	}
}
