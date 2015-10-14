package project;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface NameServerInterface extends java.rmi.Remote{
	
	void addNode(String nodeName, InetAddress nodeIP) throws RemoteException;
	
	void rmNode(String nodeName, InetAddress nodeIP) throws RemoteException;
	
	HashMap showList() throws RemoteException;

}
