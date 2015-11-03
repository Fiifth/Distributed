package nodeP;
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

public class SendReplicateFileThread extends Thread
{
	NodeData nodedata1;
	NameServerInterface nameserver;
	ReceiveQueueThreadInterface recInt;
	
	public SendReplicateFileThread(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		try {
			try {
				nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(nodedata1.getToLeave()==0)
		{
		String FileNameAndDir = null;
		String[] FileNameAndDirArray=null;
		try {
			FileNameAndDir = nodedata1.fnQueue.take();
			FileNameAndDirArray = FileNameAndDir.split("-");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ipAndId = null;
		try {
			System.out.println(FileNameAndDirArray[0]);
			ipAndId = nameserver.locateFile(FileNameAndDirArray[0]);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] ipAndIdArray = ipAndId.split("-");
		String ip = ipAndIdArray[0];
		try {
			System.out.println(ipAndIdArray[1]);
			recInt = (ReceiveQueueThreadInterface)Naming.lookup("//"+ip+":"+ipAndIdArray[1]+"/ReceiveQueueThread");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String myIP=null;
		try {
			myIP=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			recInt.addIP(myIP+"-"+FileNameAndDir);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String [] FileNameAndDirA=FileNameAndDir.split("-");
		sendFile(FileNameAndDirA);
		}

	}
	public void sendFile(String[] FileNameAndDir)
	{
		String fileToSend = FileNameAndDir[1] +"\\"+ FileNameAndDir[0];
		System.out.println(fileToSend);
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
                	System.out.println("AJ2");
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
                	System.out.println("AJ3");
                }
            }
        }

	}
	
}
