package Functions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Multicast 
{
	public MulticastSocket joinMulticastGroup(String multicastAdress,int socket)
	{
		MulticastSocket multicastSocket=null;
		InetAddress group;
		try {
			group = InetAddress.getByName(multicastAdress);
			multicastSocket = new MulticastSocket(socket);
			multicastSocket.joinGroup(group);
		} catch (IOException e) {	}
		
		return multicastSocket;
	}
	public void sendMulticast(String text,MulticastSocket multicastSocket,String multicastAdress,int socket)
	{
		byte [] m1 = text.getBytes();
		try 
		{
			InetAddress group = InetAddress.getByName(multicastAdress);
			DatagramPacket messageOut1 = new DatagramPacket(m1, m1.length, group, socket);
			multicastSocket.send(messageOut1);	
			multicastSocket.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());}
	}
	public String receiveMulticast(MulticastSocket multicastSocket)
	{
		byte[] buffer = new byte[100];
		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
		try {
			multicastSocket.receive(messageIn);
		} catch (IOException e) {e.printStackTrace();
		}
		String msgs = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		
		return msgs;
	}
	public void LeaveMulticast(MulticastSocket multicastSocket,String multicastAdress)
	{
		InetAddress group;
		try {
			group = InetAddress.getByName(multicastAdress);
			multicastSocket.leaveGroup(group);
			multicastSocket.close();
		} catch (IOException e) {	e.printStackTrace();
		}
		
	}
}
