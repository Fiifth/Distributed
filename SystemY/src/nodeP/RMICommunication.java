package nodeP;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import fileManagers.FileData;

public class RMICommunication extends UnicastRemoteObject implements RMICommunicationInt  {
	private static final long serialVersionUID = 1L;
	NodeData nodedata1;
	protected RMICommunication(NodeData nodedata1) throws RemoteException {
		super();
		this.nodedata1=nodedata1;
	}
	public void setUpRMI() 
	{
			try{
				LocateRegistry.createRegistry(nodedata1.getMyNodeID());
				RMICommunicationInt RMI = new RMICommunication(nodedata1);
				Naming.rebind("//localhost:"+nodedata1.getMyNodeID()+"/RMICommunication", RMI);
				System.out.println("RMI is ready!");
				}
				catch(Exception e){System.out.println("could not start RMI");}
	}

	public boolean receiveThisFile(FileData file1) throws RemoteException 
	{
		boolean queue=nodedata1.receiveQueue.offer(file1);

		return queue;
	}
	public boolean removeOwner(FileData file1) throws RemoteException {
		System.out.println("I should remove: "+file1.getFileName());
		FileData removedFile=null;
        for (FileData tempfile : nodedata1.replFiles) 
    	{
        	if(tempfile.getFileName().equals(file1.getFileName()))
        	{
        		removedFile = tempfile;
       		}
    	}
        nodedata1.replFiles.remove(removedFile);
        nodedata1.removeQueue.add(removedFile);
		return false;
	}
	@Override
	public boolean addOwner(FileData file1) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

}
