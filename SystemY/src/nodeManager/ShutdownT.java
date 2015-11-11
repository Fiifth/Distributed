package nodeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

import fileManagers.FileData;
import fileManagers.FileDetectionT;
import fileManagers.FileOwnershipT;
import fileManagers.Receiver;
import fileManagers.Remover;
import fileManagers.Sender;
import neworkFunctions.Multicast;
import neworkFunctions.RMI;
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
	Multicast multi;
	RMI rmi=new RMI();
	public ShutdownT(NodeData nodedata1, FileDetectionT cLFQ, Remover rem, Receiver rQT, Sender sRFT, Multicast multi)
	{
		this.nodedata1=nodedata1;
		this.cLFQ=cLFQ;
		this.rem=rem;
		this.rQT=rQT;
		this.sRFT=sRFT;
		this.multi=multi;
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
				        try {
				        	recInt = (RMICommunicationInt) rmi.getRMIObject(tempfile.getReplicateOwnerID(), tempfile.getReplicateOwnerIP(), "RMICommunication");
							recInt.removeOwner(tempfile);
						} catch (RemoteException e) {e.printStackTrace();}
		    	}
				
					nodedata1.setToLeave(1);
					String text="1"+"-"+nodedata1.getNodeName()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode();
					
					multi.sendMulticast(text);

					stay = false;

					FileOwnershipT COT =new FileOwnershipT(nodedata1);
					COT.start();

					while(COT.isAlive()){}
					
					while(!nodedata1.sendQueue.isEmpty()){}
					multi.LeaveMulticast();
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
