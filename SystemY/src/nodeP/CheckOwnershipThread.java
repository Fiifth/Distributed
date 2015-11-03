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
	String fileLocation="C:\\SystemYNodeFilesRep\\";
	public CheckOwnershipThread(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	public void run()
	{
	for (String temp : nodedata1.replFiles) 
	{
		String ipAndID = null;
		try {
			nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ipAndID = nameserver.locateFile(temp);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] ipAndIDArray = ipAndID.split("-");
		
		if (Integer.parseInt(ipAndIDArray[1])!=nodedata1.getMyNodeID())
		{
			try {
				nodedata1.fnQueue.put(temp+"-"+fileLocation);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nodedata1.replFiles.remove(temp);
		}
		System.out.println(temp);
	}
	}
}
