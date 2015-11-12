package nodeP;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import fileManagers.FileData;
import neworkFunctions.RMI;

public class RMICommunication extends UnicastRemoteObject implements RMICommunicationInt  {
	private static final long serialVersionUID = 1L;
	NodeData nodedata1;
	RMI rmi=new RMI();
	protected RMICommunication(NodeData nodedata1) throws RemoteException {
		super();
		this.nodedata1=nodedata1;
	}
	public void setUpRMI()
	{
		RMICommunicationInt rmiInt = this;
		nodedata1.setBind(rmi.bindObjectRMI(nodedata1.getMyNodeID(), "localhost", "RMICommunication", rmiInt));
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
	
	public boolean addOwner(FileData file1) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
}
