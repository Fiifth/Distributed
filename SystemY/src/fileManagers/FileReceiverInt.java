package fileManagers;

import java.rmi.RemoteException;

public interface FileReceiverInt extends java.rmi.Remote
{
boolean addIP(FileData file1) throws RemoteException;
}
