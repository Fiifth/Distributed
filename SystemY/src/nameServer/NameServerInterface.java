package nameServer;

import java.rmi.RemoteException;
import java.util.TreeMap;

public interface NameServerInterface extends java.rmi.Remote{
	
	boolean addNode(String nodeName, String nodeIP) throws RemoteException;
	
	void rmNode(String nodeName, String nodeIP) throws RemoteException;
	
	TreeMap<Integer, String> showList() throws RemoteException;
	
	String locateFile(String filename)throws RemoteException;

}
