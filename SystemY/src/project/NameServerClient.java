package project;

import java.net.InetAddress;
import java.rmi.*;

public class NameServerClient {

	public static void main(String[] args) {

		NameServerInterface nameserver;
		System.setProperty("java.security.policy","file:$git/Distributed/SystemY/bin/project/security.policy");
		System.setProperty("java.rmi.server.codebase","file:$git/Distributed/SystemY/bin/project/NameServer.class");
		try{
			System.setSecurityManager(new SecurityManager());
			nameserver = (NameServerInterface)Naming.lookup("//localhost:1099/NameServer");
			System.out.println("Excecuting remote method:");
			//InetAddress IPaddress=InetAddress.getLocalHost();
			InetAddress IPaddress1 = InetAddress.getByName("192.168.1.1");
			InetAddress IPaddress2 = InetAddress.getByName("192.168.1.2");
			InetAddress IPaddress3 = InetAddress.getByName("192.168.1.3");
			
			nameserver.addNode("test1", IPaddress1);
			nameserver.addNode("test3", IPaddress3);
			nameserver.addNode("test2", IPaddress2);
			
		}catch(Exception e) {
			System.out.println("NameClient exception: " + e);
		}
	}

	}
