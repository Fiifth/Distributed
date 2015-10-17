package project;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*; 


public class NameServer extends UnicastRemoteObject implements NameServerInterface{
	private static final long serialVersionUID = 1L;

	protected NameServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	HashMap<Integer,InetAddress> nodeMap = new HashMap<Integer,InetAddress>();

	

	public void addNode(String nodeName, InetAddress nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
    	nodeMap.put(hashedNN,nodeIP);
	}

	public void rmNode(String nodeName, InetAddress nodeIP) throws RemoteException {
		int hashedNN = Math.abs(nodeName.hashCode()%32768);
		nodeMap.remove(hashedNN);
	}

	public HashMap<Integer, InetAddress> showList() throws RemoteException {
		// TODO Auto-generated method stub
		return nodeMap;
	}
}