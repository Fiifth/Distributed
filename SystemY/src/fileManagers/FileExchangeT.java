package fileManagers;
//TODO gaat queue af met files die gerepliceerd moeten worden, hierna vraagt deze via RMI
//naar nameserver op met waar deze file naartoe zou moeten
//daarna maakt hij een RMI connectie met de node in kwestie om zijn IP adres in een lijst te plaatsen
//van zodra deze node tijd heeft gaat hij connectie maken en klaar zijn om de file te ontvangen

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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
			} catch (MalformedURLException | RemoteException | NotBoundException|InterruptedException e) {
				// TODO Auto-generated catch block
			}
			
		
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
		} catch (MalformedURLException | RemoteException | NotBoundException e) {System.out.println("RMI error");}
		
		try {
			myIP=InetAddress.getLocalHost().getHostAddress();
			recInt.addIP(myIP+"-"+FileNameAndDir);
		} catch (UnknownHostException | RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		sendFile(FileNameAndDirArray);
		}

	}
	public void sendFile(String[] FileNameAndDir)
	{
		String fileToSend = FileNameAndDir[1] +"\\"+ FileNameAndDir[0];
		System.out.println("Sending following file:"+ fileToSend);
        while (true) {
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            BufferedOutputStream outToClient = null;

            try {
                welcomeSocket = new ServerSocket(3248);
                connectionSocket = welcomeSocket.accept();
                outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
                welcomeSocket.close();
            } catch (IOException ex) {
                // Do exception handling
            	System.out.println("AJ1");
            }

            if (outToClient != null) {
            	System.out.println("sending file");
                File myFile = new File( fileToSend );
                byte[] mybytearray = new byte[(int) myFile.length()];

                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(myFile);
                } catch (FileNotFoundException ex) {
                	System.out.println("File wasn't found");
                }
                BufferedInputStream bis = new BufferedInputStream(fis);

                try {
                    bis.read(mybytearray, 0, mybytearray.length);
                    outToClient.write(mybytearray, 0, mybytearray.length);
                    outToClient.flush();
                    outToClient.close();
                    connectionSocket.close();
                    // File sent, exit the main method
                    return;
                } catch (IOException ex) {
                	System.out.println("Sending file failed");
                }
                try {
					fis.close();
					 bis.close();
				} catch (IOException e) {System.out.println("couldn't close files");}
               
            }
        }

	}
	
}
