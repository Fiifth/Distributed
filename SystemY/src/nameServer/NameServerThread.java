package nameServer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.rmi.RemoteException;

import networkFunctions.TCP;

public class NameServerThread extends Thread {
	DatagramPacket messageIn;
	TCP tcp=new TCP();
	StartNameServer nameServer;
	public NameServerThread(DatagramPacket messageIn, StartNameServer nameServer)
	{
		this.messageIn=messageIn;
		this.nameServer=nameServer;
	}
	
	public void run() 
	{
			//bericht dat ontvangen wordt van de node bevat eerst 0(nieuwe node) of 1(node die weg gaat) 
			//hierna een '-' vervolgd door de nodeID
		String msgs = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		String[] message = msgs.split("-");
		int toLeave=Integer.parseInt(message[0]);
		InetAddress addr=messageIn.getAddress();
		String nodeIP = addr.getHostAddress().toString();
		if(toLeave == 1)//rmnode
		{
			try {	
				nameServer.rmNode(message[1],nodeIP);
			} catch (RemoteException e) {e.printStackTrace();}				
		}
		else//addnode
		{
			String numOfNodesString=null;
			boolean isNewNode=false;
			//Addnode gaat een boolean terug geven die ons weergeeft of de nodeID al in de map aanwezig was
			//Wanneer dit het geval is zal hij een "0" terug sturen naar de node zodat hij dit ook weet
			try 
			{
				isNewNode = nameServer.addNode(message[1], nodeIP);
			} catch (RemoteException e) {}
			
			if (isNewNode)
			{
				Integer numberOfNodes = nameServer.getNodeMap().size(); 
				numOfNodesString = numberOfNodes.toString();	
			}
			else
			{
				numOfNodesString="0";
			}
			tcp.sendTextWithTCP(numOfNodesString, nodeIP, 6790);
		}
	}
}

