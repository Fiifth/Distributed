package project;

import java.net.InetAddress;
import java.rmi.*;

public class NameServerClient {

	public static void main(String[] args) {

		NameServerInterface nameserver;
		try{
			System.setSecurityManager(new SecurityManager());
			nameserver = (NameServerInterface)Naming.lookup("//localhost:1099/NameServer");
			System.out.println("Excecuting remote method:");
			InetAddress temp=InetAddress.getLocalHost();
			nameserver.addNode("test", temp);
		}catch(Exception e) {
			System.out.println("NameClient exception: " + e);
		}
	}

	}
