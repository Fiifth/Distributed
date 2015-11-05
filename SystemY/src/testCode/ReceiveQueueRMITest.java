package testCode;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import fileManagers.FileData;
import fileManagers.FileReceiverInt;

public class ReceiveQueueRMITest {

	public static void main(String[] args)
	{
		FileData fileData1=null;
		FileReceiverInt RecInt=null;
		try {
			RecInt = (FileReceiverInt)Naming.lookup("//localhost:14393/ReceiveQueueThread");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("test");
			e.printStackTrace();
		}
		System.out.println("Excecuting remote method:");
		try {
			RecInt.addIP(fileData1);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
