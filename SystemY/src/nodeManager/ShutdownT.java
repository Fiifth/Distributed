package nodeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import fileManagers.FileData;
import fileManagers.FileDetectionT;
import fileManagers.FileOwnershipT;
import fileManagers.Receiver;
import fileManagers.Remover;
import fileManagers.Sender;
import nodeP.NodeData;
import nodeP.RMICommunicationInt;

public class ShutdownT extends Thread
{
	NodeData nodedata1;
	String input;
	FileDetectionT cLFQ;
	Remover rem;
	Receiver rQT;
	Sender sRFT;
	public ShutdownT(NodeData nodedata1, FileDetectionT cLFQ, Remover rem, Receiver rQT, Sender sRFT)
	{
		this.nodedata1=nodedata1;
		this.cLFQ=cLFQ;
		this.rem=rem;
		this.rQT=rQT;
		this.sRFT=sRFT;
	}

	public void run()
	{
		boolean stay = true;
		System.out.println("Type quit to stop this node.");
		while(stay)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(input.equals("quit"))
				{
				for (FileData tempfile : nodedata1.localFiles) 
		    	{
						tempfile.refreshReplicateOwner(nodedata1, tempfile);
				        RMICommunicationInt recInt=null;
				        try {//TODO change to RMI.getRMIObject
							recInt = (RMICommunicationInt) Naming.lookup("//"+tempfile.getReplicateOwnerIP()+":"+tempfile.getReplicateOwnerID()+"/RMICommunication");
							recInt.removeOwner(tempfile);
						} catch (MalformedURLException | RemoteException | NotBoundException e) {e.printStackTrace();}
		    	}
				
					nodedata1.setToLeave(1); //TODO change to TCP.sendMulticast
					nodedata1.sendMulticast("1"+"-"+nodedata1.getNodeName()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode());
					stay = false;

					FileOwnershipT COT =new FileOwnershipT(nodedata1);
					COT.start();

					while(COT.isAlive()){}
					
					while(!nodedata1.sendQueue.isEmpty()){}

					stopThreads();
					System.exit(1);
				}
			}	
	}
	
	public void stopThreads()
	{
		cLFQ.interrupt();
		try {
			cLFQ.watcher.close();
		} catch (IOException e) {e.printStackTrace();}
		rem.interrupt();
		rQT.interrupt();
		sRFT.interrupt();
	}
	public void checkThreadStatus()
	{
		System.out.println(cLFQ.isAlive());
		System.out.println(rem.isAlive());
		System.out.println(rQT.isAlive());
		System.out.println(sRFT.isAlive());
	}
}
