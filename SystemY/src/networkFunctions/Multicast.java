package networkFunctions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Multicast 
{
	int socket;
	String multicastAddress;
	MulticastSocket multicastSocket;
	
	public Multicast(String multicastAddress,int socket)
	{
		this.multicastAddress=multicastAddress;
		this.socket=socket;
	}
	public void joinMulticastGroup()
	{
		InetAddress group;
		try 
		{
			group = InetAddress.getByName(multicastAddress);
			multicastSocket = new MulticastSocket(socket);
			multicastSocket.joinGroup(group);
		} catch (IOException e) {	}
	}
	public void sendMulticast(String text)
	{
		byte [] m1 = text.getBytes();
		try 
		{
			InetAddress group = InetAddress.getByName(multicastAddress);
			DatagramPacket messageOut1 = new DatagramPacket(m1, m1.length, group, socket);
			multicastSocket.send(messageOut1);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());}
	}
	
	public DatagramPacket receiveMulticast()
	{
		byte[] buffer = new byte[100];
		DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
		try 
		{
			multicastSocket.receive(messageIn);
		} catch (IOException e) {e.printStackTrace();}
		//String msgs = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
		return messageIn;
	}
	
	public void LeaveMulticast()
	{
		InetAddress group;
		try 
		{
			group = InetAddress.getByName(multicastAddress);
			multicastSocket.leaveGroup(group);
			multicastSocket.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
