package project;

import java.net.InetAddress;
import java.rmi.*; 
import java.util.*;


public class NameServer{
	
	Map<Integer, InetAddress> nodeMap = new HashMap();

	

	public void addNode(String nodeName, InetAddress nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
    	nodeMap.put(hashedNN,nodeIP);
	}

	public void rmIP(String nodeName, InetAddress nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		nodeMap.remove(hashedNN);
	}

	public void showList() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}