package nodeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import fileManagers.FileData;
import fileManagers.FileOwnershipT;
import neworkFunctions.Multicast;
import neworkFunctions.RMI;
import nodeP.NodeData;
import nodeP.RMICommunicationInt;

public class ShutdownT extends Thread
{
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
		System.out.println("Type quit to stop this node.");
		while(stay)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(input.equals("quit "+nodedata1.getNodeName()))
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
				try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}		

				FileOwnershipT COT =new FileOwnershipT(nodedata1);
				COT.start();

				while(COT.isAlive()){}
				
				while(!nodedata1.sendQueue.isEmpty()){}
	
				while(nodedata1.isSending()){}
				
				try {
					Naming.unbind(nodedata1.getBind());
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
