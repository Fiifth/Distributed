package fileManagers;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileOwnershipT extends Thread
{
	NodeData nodedata1;
	NameServerInterface nameserver;
	public FileOwnershipT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	public void run()
	{
		ArrayList<String> removeFileList=new ArrayList<String>();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	for (String temp : nodedata1.replFiles) 
	{
		String[] tempArray=temp.split("-");
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
			nodedata1.toSendFileNameAndDirList.add(tempArray[1]+"-"+tempArray[2]+"Rep");
			removeFileList.add(temp);
		}
	}
		for (String temp:removeFileList)
		{
			nodedata1.replFiles.remove(temp);
			//TODO remove the file from folder after sending
		}
	
	}
}
