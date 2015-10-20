package project;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.TreeMap;

public interface NameServerInterface extends java.rmi.Remote{
	
	void addNode(String nodeName, String nodeIP) throws RemoteException;
	
	void rmNode(String nodeName, String nodeIP) throws RemoteException;
	
	TreeMap<Integer, String> showList() throws RemoteException;
	
	String locateFile(String filename)throws RemoteException;

}
