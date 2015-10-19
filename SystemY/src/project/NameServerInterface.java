package project;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.TreeMap;

public interface NameServerInterface extends java.rmi.Remote{
	
	void addNode(String nodeName, InetAddress nodeIP) throws RemoteException;
	
	void rmNode(String nodeName, InetAddress nodeIP) throws RemoteException;
	
	TreeMap<Integer, InetAddress> showList() throws RemoteException;
	
	InetAddress locateFile(String filename)throws RemoteException;

}
