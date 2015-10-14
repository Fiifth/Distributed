package project;

import java.rmi.RemoteException;

public interface NameServerInterface extends java.rmi.Remote{
	
	void addIP() throws RemoteException;
	
	void rmIP() throws RemoteException;
	
	void showList() throws RemoteException;

}
