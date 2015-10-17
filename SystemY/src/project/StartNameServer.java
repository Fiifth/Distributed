package project;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class StartNameServer {
	
	public static void main(String[] args){
		try{
			LocateRegistry.createRegistry(2020);
			NameServerInterface nameint = new NameServer();
			Naming.rebind("//localhost/NameServer", nameint);
			System.out.println("NameServer is ready.");
		}catch(Exception e){
			System.out.println("NameServer error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
