package project;
import java.net.*;
import java.io.*;

public class Node 
{

	public static void main(String[] args) 
	{
		/*
		MulticastSocket multiastSocket =null;
		try 
		{
			String msg="testFromKwinten";
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multiastSocket = new MulticastSocket(6789);
			multiastSocket.joinGroup(group);
 			byte [] m = msg.getBytes();
			DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
			multiastSocket.send(messageOut);	
			byte[] buffer = new byte[1000];
 			for(int i=0; i< 3;i++)	//receive 3 messages
			 {		
				// get messages from others in group
 				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
 				multiastSocket.receive(messageIn);
 				System.out.println("Received:" + new String(messageIn.getData()));
  		}
			multiastSocket.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(multiastSocket != null) multiastSocket.close();}

*/
	}

}
