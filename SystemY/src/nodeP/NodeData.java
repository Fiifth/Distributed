package nodeP;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fileManagers.FileData;

public class NodeData {
	private volatile String nodeName;
	private volatile int prevNode;
	private volatile int nextNode;
	private volatile int myNodeID;
	private volatile int toLeave;
	private volatile String nameServerIP;
	private volatile String myIP;
	private volatile String myLocalFolder;
	private volatile String myReplFolder;
	public volatile BlockingQueue<FileData> sendQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile ArrayList<FileData> localFiles=new ArrayList<FileData>();	
	public volatile ArrayList<FileData> replFiles=new ArrayList<FileData>();
	public volatile BlockingQueue<FileData> receiveQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile BlockingQueue<FileData> removeQueue=new ArrayBlockingQueue<FileData>(500);
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		setMyNodeID(Math.abs(nodeName.hashCode()%32768));
		try {
			setMyIP(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {}
		this.nodeName = nodeName;
		setMyReplFolder("c:\\SystemYNodeFilesRep"+getMyNodeID());
		setMyLocalFolder("c:\\SystemYNodeFiles"+getMyNodeID());
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
	

	public String getMyIP() {
		return myIP;
	}
	public void setMyIP(String myIP) {
		this.myIP = myIP;
	}
	public String getMyLocalFolder() {
		return myLocalFolder;
	}
	public void setMyLocalFolder(String myLocalFolder) {
		this.myLocalFolder = myLocalFolder;
	}
	public String getMyReplFolder() {
		return myReplFolder;
	}
	public void setMyReplFolder(String myReplFolder) {
		this.myReplFolder = myReplFolder;
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
