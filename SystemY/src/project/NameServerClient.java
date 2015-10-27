package project;

import java.net.InetAddress;
import java.rmi.*;
//TODO alle testen die in ppt besproken werden moeten hier in komen
public class NameServerClient {

	public static void main(String[] args) {

		NameServerInterface nameserver;
		System.setProperty("java.security.policy","file:${workspace_loc}/Distributed/SystemY/bin/project/security.policy");
		System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/Distributed/SystemY/bin/project/NameServer.class");
		try{
			//System.setSecurityManager(new SecurityManager());
			nameserver = (NameServerInterface)Naming.lookup("//localhost:1099/NameServer");
			System.out.println("Excecuting remote method:");
			//InetAddress IPaddress=InetAddress.getLocalHost();
			InetAddress IPaddress1 = InetAddress.getByName("192.168.1.1");
			InetAddress IPaddress2 = InetAddress.getByName("192.168.1.2");
			InetAddress IPaddress3 = InetAddress.getByName("192.168.1.3");
			//voeg een Node toe met een unieke naam
			nameserver.addNode("test2", "192.168.1.2");
			//voeg een Node toe met een naam die reeds bestaat
			nameserver.addNode("test2", "192.168.1.2");
			//verwijder een node uit de map
			nameserver.rmNode("test2", "192.168.1.2");
			//probeer een node te verwijderen uit de map, die niet bestaat
			nameserver.rmNode("test3", "10.1.1.1");
			
		}catch(Exception e) {
			System.out.println("NameClient exception: " + e);
		}
	}

	}
