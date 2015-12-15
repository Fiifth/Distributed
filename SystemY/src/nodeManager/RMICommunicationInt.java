package nodeManager;

import java.rmi.RemoteException;

import agent.AgentMain;
import fileManagers.FileData;

public interface RMICommunicationInt extends java.rmi.Remote
{
	boolean receiveThisFile(FileData file1) throws RemoteException;
	void removeThisOwner(FileData file1) throws RemoteException;
	boolean addOwner(FileData file1) throws RemoteException;
	void rmiFileAgentExecution(AgentMain fileAgent) throws RemoteException;
}
