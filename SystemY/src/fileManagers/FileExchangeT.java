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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileExchangeT extends Thread
{
	NodeData nodedata1;
	NameServerInterface nameserver;
	FileReceiverInt recInt;
	
	public FileExchangeT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		String ipAndId = null;
		String myIP=null;
		
			try {
				nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
				Thread.sleep(1000);
			} catch (Exception e) {System.out.println("failed connect to RMI of the server");}
			
		
		while(nodedata1.getToLeave()==0)
			{
			String FileNameAndDir = null;
			String[] FileNameAndDirArray=null;
			try {
				FileNameAndDir = nodedata1.toSendFileNameAndDirList.take();
				FileNameAndDirArray = FileNameAndDir.split("-");
			} catch (InterruptedException e1) {System.out.println("interrupted while waiting for queue");}
			
				try {
					ipAndId = nameserver.locateFile(FileNameAndDirArray[0]);
					String[] ipAndIdArray = ipAndId.split("-");
					String ip = ipAndIdArray[0];
					recInt = (FileReceiverInt)Naming.lookup("//"+ip+":"+ipAndIdArray[1]+"/ReceiveQueueThread");
				} catch (Exception e) {System.out.println("failed connect to RMI of the node");}
				
				try {
					myIP=InetAddress.getLocalHost().getHostAddress();
					recInt.addIP(myIP+"-"+FileNameAndDir);
				} catch (UnknownHostException | RemoteException e1) {System.out.println("failed connect place data in queue");}
		
				sendFile(FileNameAndDirArray);
			}

	}
	public void sendFile(String[] FileNameAndDir)
	{
		String filePath = FileNameAndDir[1] +"\\"+ FileNameAndDir[0];
		System.out.println("Sending following file:"+ filePath);
		        while (true) 
        {
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
            return;
        }
	}
}
