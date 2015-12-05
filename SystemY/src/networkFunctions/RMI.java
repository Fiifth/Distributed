package networkFunctions;

import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;

import java.rmi.*;

public class RMI 
{
	public String bindObjectRMI(int RMIport,String locationIP, String locationName,Object object ) 
	{
		String bind="//"+locationIP+":"+RMIport+"/"+locationName;
		try
		{
			LocateRegistry.createRegistry(RMIport);
			Naming.rebind("//"+locationIP+":"+RMIport+"/"+locationName, (Remote) object);
		}catch(Exception e){System.out.println("could not start RMI");}
		return bind;	
	}
	
	public void unbindObjectRMI(String locationName)
	{
		try 
		{
			Naming.unbind(locationName);
		} catch (RemoteException | MalformedURLException | NotBoundException e) {e.printStackTrace();}
	}
	
	public Object getRMIObject(int RMIport,String locationIP, String locationName)
	{
		Object object=null;
		try 
		{
			object=Naming.lookup("//"+locationIP+":"+RMIport+"/"+locationName);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {return false;}
		return object;
	}
}
