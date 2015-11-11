package neworkFunctions;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMI 
{
	public void bindObjectRMI(int RMIport,String locationIP, String locationName,Object object ) 
	{
			try{
				LocateRegistry.createRegistry(RMIport);
				
				Naming.rebind("//"+locationIP+":"+RMIport+"/"+locationName, (Remote) object);
				System.out.println("RMI is ready!");
				}
				catch(Exception e){System.out.println("could not start RMI");}
	}
	public void unbindObjectRMI(int RMIport,String locationIP, String locationName,Object object)
	{
		try {
			Naming.unbind("//"+locationIP+":"+RMIport+"/"+locationName);
		} catch (RemoteException | MalformedURLException | NotBoundException e) {e.printStackTrace();}
	}
	public Object getRMIObject(int RMIport,String locationIP, String locationName)
	{
		Object object=null;
		try {
			object=Naming.lookup("//"+locationIP+":"+RMIport+"/"+locationName);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {}
		return object;
	}
	

}
