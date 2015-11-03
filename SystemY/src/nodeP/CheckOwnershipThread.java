package nodeP;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import nameServer.NameServerInterface;

public class CheckOwnershipThread extends Thread
{
	//TODO bij nieuwe node controleren of gerepliceerde bestanden een nieuwe eigenaar moeten hebben
	//bij received files alles bijhouden en lijst afgaan via RMI of de ontvange files
	//een nieuwe eigenaar hebben
	NodeData nodedata1;
	NameServerInterface nameserver;
	//String fileLocation="C:\\SystemYNodeFilesRep";
	public CheckOwnershipThread(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	public void run()
	{
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	for (String temp : nodedata1.replFiles) 
	{
		String [] tempArray=temp.split("-");
		String ipAndID = null;
		try {
			nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ipAndID = nameserver.locateFile(tempArray[1]);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] ipAndIDArray = ipAndID.split("-");
		if (Integer.parseInt(ipAndIDArray[1])!=nodedata1.getMyNodeID())
		{
			System.out.println("Changing owner to:"+ Integer.parseInt(ipAndIDArray[1]));
			nodedata1.fnQueue.add(tempArray[1]+"-"+tempArray[2]+"Rep");
			nodedata1.replFiles.remove(temp);
			//TODO preform remove after for loop otherwise ConcurrentModificationException
		}
	}
	
	}
}
