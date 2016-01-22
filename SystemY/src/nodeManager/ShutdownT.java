package nodeManager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import networkFunctions.Multicast;
import networkFunctions.RMI;
import nodeFileManagers.FileData;
import nodeFileManagers.FileOwnershipT;
import nodeStart.NodeData;

public class ShutdownT extends Thread
{
	public boolean toquit = false;
	NodeData nodedata1;
	String input;
	Multicast multi;
	ArrayList<Object> threadList;
	RMI rmi=new RMI();
	public ShutdownT(NodeData nodedata1, ArrayList<Object> threadList, Multicast multi)
	{
		this.nodedata1=nodedata1;
		this.threadList=threadList;
		this.multi=multi;
	}

	public void run()
	{
		boolean stay = true;
		while(stay)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
			if(nodedata1.getToQuit())
			{
				while(!nodedata1.receiveQueue.isEmpty()){}
				while(nodedata1.isReceiving()!=0){}
				//waarschuw de replicatie eigenaar dat de file niet meer beschikbaar gaat zijn
				for(Map.Entry<Integer, FileData> entry : nodedata1.localFiles.entrySet())
				{
					FileData test = entry.getValue();
					test.refreshReplicateOwner(nodedata1);
					test.setSourceID(nodedata1.getMyNodeID());
			        RMICommunicationInt recInt=null;
			        try {
			        	recInt = (RMICommunicationInt) rmi.getRMIObject(test.getReplicateOwnerID(), test.getReplicateOwnerIP(), "RMICommunication");
						recInt.removeThisOwner(test);
					} catch (RemoteException e) {e.printStackTrace();}
		    	}
				
				nodedata1.setToLeave(1);
				String text="1"+"-"+nodedata1.getMyNodeID()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode()+"-"+nodedata1.getPrevNodeIP()+"-"+nodedata1.getNextNodeIP();
				//laat alle nodes weten dat de node weg gaat
				multi.sendMulticast(text);

				stay = false;
				try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}		
				//stuur alle files door die deze node gerepliceerd heeft naar een nieuwe rep eigenaar
				FileOwnershipT COT =new FileOwnershipT(nodedata1);
				COT.start();
				//wacht tot alle bestanden behandeld zijn
				while(COT.isAlive()){}
				//wacht tot alle bestanden verstuurd zijn
				while(!nodedata1.sendQueue.isEmpty()){}
				//het laatste bestand zal nog verstuurd moeten worden als de sendQueue leeg is dus wordt er
				//gebruik gemaakt van een extra flag die bepaald of de node aan het sturen is.
				while(nodedata1.isSending()){}
				
				try {
					Naming.unbind(nodedata1.getBind());
				} catch (RemoteException | MalformedURLException | NotBoundException e) {}
				
				multi.LeaveMulticast();
				//sluit alle threads
				for (Object temp:threadList)
				{
					((Thread) temp).interrupt();
				}
				System.exit(0);//sluit het systeem volledig af
			}
		}	
	}

}
