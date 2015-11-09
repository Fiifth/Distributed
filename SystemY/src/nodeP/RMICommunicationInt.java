package nodeP;

import java.rmi.RemoteException;

import fileManagers.FileData;

public interface RMICommunicationInt extends java.rmi.Remote
{
	boolean receiveThisFile(FileData file1) throws RemoteException;
	boolean removeOwner(FileData file1) throws RemoteException;
	boolean addOwner(FileData file1) throws RemoteException;
}
