package project;
import java.net.*;
import java.io.*;

public class Node 
{
	
	public static void main(String[] args) 
	{
		
		MulticastSocket multiastSocket =null;
		try 
		{	
			InetAddress mijnIP0=InetAddress.getLocalHost();
			String mijnIP= mijnIP0.getHostAddress();
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multiastSocket = new MulticastSocket(6789);
			multiastSocket.joinGroup(group);
 			byte [] m = mijnIP.getBytes(); 			
			DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
			
			multiastSocket.send(messageOut);
			
			byte[] buffer = new byte[1000];
			
 			for(int i=0; i< 2;i++)	//receive 3 messages
			 {		
 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				multiastSocket.receive(messageIn);
 				String msg = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
 				System.out.println("Received:" + msg);
 				//if i = 2 slaag aantal nodes op en bepaal ip adres van server
 				//if i = 3 afleiden wie vorige en volgende nodes zijn ofzo
			 }
			multiastSocket.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(multiastSocket != null) multiastSocket.close();}


	}

}
