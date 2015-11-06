package fileManagers;

import java.rmi.RemoteException;

public interface FileReceiverInt extends java.rmi.Remote
{
boolean receiveThisFile(FileData file1) throws RemoteException;
boolean removeThisFile(FileData file1) throws RemoteException;
}
