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
			InetAddress IPaddress=InetAddress.getLocalHost();
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
			//stuur een bestandsnaam op en ping het ip adres
			String IPtest = IPaddress.getHostAddress();
			nameserver.addNode("test1",IPtest);
			String location = nameserver.locateFile("testFile");
			System.out.println("location: " + location);
			InetAddress address = InetAddress.getByName(location);
			boolean reachable = address.isReachable(10000);
			System.out.println("is host reachable? " + reachable);
			//stuur een bestandsnaam op waarvan de hash kleiner is dan de hash van de kleinste node
			nameserver.locateFile("test0");
		}catch(Exception e) {
			System.out.println("NameClient exception: " + e);
		}
	}

	}
