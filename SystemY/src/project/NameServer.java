package project;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class NameServer extends UnicastRemoteObject implements NameServerInterface{
	
	Map nodeMap = new HashMap();

	

	public void addIP() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void rmIP() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void showList() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}