package testCode;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import fileManagers.FileReceiverInt;

public class ReceiveQueueRMITest {

	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException 
	{
		FileReceiverInt RecInt;
		RecInt = (FileReceiverInt)Naming.lookup("//localhost:2000/ReceiveQueueThread");
		System.out.println("Excecuting remote method:");
		RecInt.addIP("192.168.1.1-test.jpg");
	}

}
