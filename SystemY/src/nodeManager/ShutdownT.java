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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//true if node wants to quit
			if(nodedata1.getToQuit())
				{
				
				for(Map.Entry<Integer, FileData> entry : nodedata1.localFiles.entrySet())
				{
					
					FileData test = entry.getValue();
					test.refreshReplicateOwner(nodedata1);
					test.setSourceID(nodedata1.getMyNodeID());
					//if(!(entry.getValue().getReplicateOwnerID()==nodedata1.getMyNodeID()))
					//{
				        RMICommunicationInt recInt=null;
				        try {
				        	recInt = (RMICommunicationInt) rmi.getRMIObject(test.getReplicateOwnerID(), test.getReplicateOwnerIP(), "RMICommunication");
							recInt.removeThisOwner(test);
						} catch (RemoteException e) {e.printStackTrace();}
					//}
		    	}
				
				nodedata1.setToLeave(1);
				String text="1"+"-"+nodedata1.getNodeName()+"-"+nodedata1.getPrevNode()+"-"+nodedata1.getNextNode()+"-"+nodedata1.getPrevNodeIP()+"-"+nodedata1.getNextNodeIP();
				
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
