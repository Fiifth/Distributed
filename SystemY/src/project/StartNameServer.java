package project;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class StartNameServer {
	
	String ipServer
	
	public NameServer() throws RemoteException{
		try{
			LocateRegistry.createRegistry(2020);	
			NameServer nameserverobj = new NameServer();
			Naming.rebind("//")
		}catch(Exception e){
			System.out.println("NameServer error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
