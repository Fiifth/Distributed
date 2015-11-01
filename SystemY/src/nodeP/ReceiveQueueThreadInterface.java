package nodeP;

import java.rmi.RemoteException;

public interface ReceiveQueueThreadInterface extends java.rmi.Remote
{
boolean addIP(String ip) throws RemoteException;
}
