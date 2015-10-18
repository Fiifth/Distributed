package project;

import java.net.InetAddress;
import java.rmi.*;

public class NameServerClient {

	public static void main(String[] args) {

		NameServerInterface nameserver;
		System.setProperty("java.rmi.server.codebase","file:$git/Distributed/SystemY/bin/project/NameServer.class");
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
