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
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import nameServer.NameServerInterface;

public class SendReplicateFileThread 
{
	NodeData nodedata1;
	NameServerInterface nameserver;
	ReceiveQueueThreadInterface recInt;
	
	public SendReplicateFileThread(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run() throws InterruptedException, MalformedURLException, RemoteException, NotBoundException
	{
		while(nodedata1.getToLeave()==0)
		{
		String filename =nodedata1.fnQueue.take();
		nameserver = (NameServerInterface)Naming.lookup("//localhost:1099/NameServer");
		String ip=nameserver.locateFile(filename);
		
		recInt = (ReceiveQueueThreadInterface)Naming.lookup("//localhost:2000/ReceiveQueueThread");
		recInt.addIP(ip+"-"+filename);
		sendFile(filename);
		}

	}
	public void sendFile(String filename)
	{
		String fileToSend = "C:\\SystemYNodeFiles\""+filename;

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
