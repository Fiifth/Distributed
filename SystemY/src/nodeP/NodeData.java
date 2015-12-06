package nodeP;

import java.io.Serializable;
import java.net.*;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fileManagers.FileData;
import fileManagers.FileOwnershipT;

public class NodeData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private volatile boolean toQuit = false;
	private volatile String nodeName;
	private volatile int prevNode;
	private volatile int nextNode;
	private volatile int myNodeID;
	private volatile int toLeave;
	private volatile String nameServerIP;
	private volatile String prevNodeIP;
	private volatile String nextNodeIP;
	private volatile String myIP;
	private volatile String myLocalFolder;
	private volatile String myReplFolder;
	private volatile boolean sending;
	private volatile String bind;
	public volatile BlockingQueue<FileData> sendQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile BlockingQueue<FileData> receiveQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile BlockingQueue<FileData> removeQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile TreeMap<Integer, TreeMap<Integer,FileData>> allNetworkFiles = new TreeMap<Integer, TreeMap<Integer,FileData>>();
	public volatile TreeMap<Integer,FileData> lockRequestList = new TreeMap<Integer,FileData>();//try to achieve lock in order to download
	public volatile TreeMap<Integer, FileData> localFiles = new TreeMap<Integer,FileData>();
	public volatile TreeMap<Integer, FileData> replFiles = new TreeMap<Integer,FileData>();
	
	
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.setMyNodeID(Math.abs(nodeName.hashCode()%32768));
		try {
			setMyIP(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {}
		this.nodeName = nodeName;
		this.setMyReplFolder("c:\\SystemYNodeFilesRep"+getMyNodeID());
		this.setMyLocalFolder("c:\\SystemYNodeFiles"+getMyNodeID());
	}
	
	public boolean getToQuit(){
		return toQuit;
	}
	
	public void setToQuit(boolean toQuit){
		this.toQuit = toQuit;		
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
	public String getNextNodeIP() {
		return nextNodeIP;
	}
	public void setNextNodeIP(String nextNodeIP) {
		FileOwnershipT COT =new FileOwnershipT(this);
		COT.start();
		this.nextNodeIP = nextNodeIP;
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
	public String getBind() {
		return bind;
	}
	public void setBind(String bind) {
		this.bind = bind;
	}
	public String getPrevNodeIP() {
		return prevNodeIP;
	}
	public void setPrevNodeIP(String prevNodeIP) {
		this.prevNodeIP = prevNodeIP;
	}
	public boolean isSending() {
		return sending;
	}
	public void setSending(boolean sending) {
		this.sending = sending;
	}

	
}
