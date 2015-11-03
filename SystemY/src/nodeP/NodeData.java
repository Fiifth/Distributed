package nodeP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NodeData {
	private volatile String nodeName;
	private volatile int prevNode;
	private volatile int nextNode;
	private volatile int myNodeID;
	private volatile int toLeave;
	private volatile String nameServerIP;
	public volatile BlockingQueue<String> toSendFileNameAndDirList=new ArrayBlockingQueue<String>(500);
	public volatile ArrayList<String> localFiles=new ArrayList<String>();	
	public volatile ArrayList<String> replFiles=new ArrayList<String>();
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public int getPrevNode() {
		return prevNode;
	}
	public void setPrevNode(int prevNode) {
		this.prevNode = prevNode;
	}
	public int getNextNode() {
		return nextNode;
	}
	public void setNextNode(int nextNode) {
		this.nextNode = nextNode;
	}
	public int getMyNodeID() {
		return myNodeID;
	}
	public void setMyNodeID(int myNodeID) {
		this.myNodeID = myNodeID;
	}
	public int getToLeave() {
		return toLeave;
	}
	public void setToLeave(int toLeave) {
		this.toLeave = toLeave;
	}
	public String getNameServerIP() {
		return nameServerIP;
	}
	public void setNameServerIP(String nameServerIP) {
		this.nameServerIP = nameServerIP;
	}
	

	public void sendMulticast(String name)
	{
		MulticastSocket multicastSocket =null;
		byte [] m1 = name.getBytes();
		try 
		{	
			InetAddress group = InetAddress.getByName("228.5.6.7");
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(group);
			DatagramPacket messageOut1 = new DatagramPacket(m1, m1.length, group, 6789);
			multicastSocket.send(messageOut1);	
			multicastSocket.leaveGroup(group);		
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(multicastSocket != null) multicastSocket.close();}
	}
	
	

	
}
