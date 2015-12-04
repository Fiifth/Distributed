package nodeManager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import fileManagers.FileData;
import fileManagers.FileOwnershipT;
import networkFunctions.Multicast;
import networkFunctions.RMI;
import nodeP.NodeData;

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
			//true if node wants to quit
			if(nodedata1.getToQuit())
				{
				
				for(Map.Entry<Integer, FileData> entry : nodedata1.localFiles.entrySet())
				{ 
					entry.getValue().refreshReplicateOwner(nodedata1, entry.getValue());
				        RMICommunicationInt recInt=null;
				        try {
				        	recInt = (RMICommunicationInt) rmi.getRMIObject(entry.getValue().getReplicateOwnerID(), entry.getValue().getReplicateOwnerIP(), "RMICommunication");
							recInt.removeThisOwner(entry.getValue());
						} catch (RemoteException e) {e.printStackTrace();}
		    	}
				
				nodedata1.setToLeave(1);
				String text="1"+"-"+nodedata1.getNodeName()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode();
				
				multi.sendMulticast(text);

				stay = false;
				try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}		

				FileOwnershipT COT =new FileOwnershipT(nodedata1);
				COT.start();

				while(COT.isAlive()){}
				
				while(!nodedata1.sendQueue.isEmpty()){}
	
				while(nodedata1.isSending()){}
				
				try {
					Naming.unbind(nodedata1.getBind());
				} catch (RemoteException | MalformedURLException | NotBoundException e) {}
				
				multi.LeaveMulticast();
				
				for (Object temp:threadList)
				{
					((Thread) temp).interrupt();
				}
				//System.exit(1);
			}
		}	
	}

}
