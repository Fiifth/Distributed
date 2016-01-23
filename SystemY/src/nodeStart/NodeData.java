package nodeStart;

import java.io.File;
import java.io.Serializable;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import nodeFileManagers.FileData;
import nodeFileManagers.FileOwnershipT;
import splitAndMerge.Merge;

public class NodeData implements Serializable {
	private static final long serialVersionUID = 1L;
	private volatile boolean toQuit = false;
	private volatile boolean FApresent = true;
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
	private volatile boolean abortOpening=false;
	private volatile boolean debug=false;
	private volatile int receiving;
	private volatile boolean changed = true;
	private volatile String bind;
	private volatile int numberOfNodesStart;
	public volatile BlockingQueue<FileData> sendQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile BlockingQueue<FileData> receiveQueue=new ArrayBlockingQueue<FileData>(500);
	public volatile TreeMap<Integer, TreeMap<Integer,FileData>> allNetworkFiles = new TreeMap<Integer, TreeMap<Integer,FileData>>();
	public volatile TreeMap<Integer,String> lockRequestList = new TreeMap<Integer,String>();
	public volatile TreeMap<Integer, FileData> localFiles = new TreeMap<Integer,FileData>();
	public volatile TreeMap<Integer, FileData> replFiles = new TreeMap<Integer,FileData>();
	public volatile TreeMap<Integer,ArrayList<File>> partMap=new TreeMap<Integer,ArrayList<File>>();//map(fileID,Arraylist(nodeID))
	public volatile Semaphore semaphore = new Semaphore(1);
	private boolean agentWasHere;
	
	
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
	public int isReceiving() {
		return receiving;
	}
	public void plusReceive() {
		this.receiving = receiving+1;
	}
	public void minReceive() {
		this.receiving = receiving-1;
	}
	public void setSending(boolean sending) {
		this.sending = sending;
	}
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	public void acquire() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {return;}
	}
	public void release() {
		semaphore.release();
	}
	public int getNumberOfNodesStart() {
		return numberOfNodesStart;
	}
	public void setNumberOfNodesStart(int numberOfNodesStart) {
		this.numberOfNodesStart = numberOfNodesStart;
	}
	public void addAPart(int fileID, String fileOutput, int TotalNumberOfParts,String fileName) 
	{
		ArrayList<File> files=new ArrayList<File>();
		
		if (partMap.containsKey(fileID))
		{
			files.addAll(partMap.get(fileID));
			files.add(new File(fileOutput));
			
			if (files.size()==TotalNumberOfParts)
			{
				File destination=new File(myLocalFolder+"\\"+fileName);
				Collections.sort(files); //zorgt ervoor dat de parts in juiste volgorde staan om te mergen
				Merge merger=new Merge();
				merger.mergeFiles(files, destination);
				if(this.isDebug()) System.out.println("File merged; "+destination);
				partMap.remove(fileID);
			}
			else
				partMap.put(fileID, files);
		}
		else
		{
			files.add(new File(fileOutput));
			partMap.put(fileID, files);
		}
	}
	public boolean isFApresent() {
		return FApresent;
	}
	public void setFApresent(boolean FApresent) 
	{
		setAgentWasHere(true);
		this.FApresent = FApresent;
	}
	public boolean wasAgentHere() 
	{
		return agentWasHere;
	}
	public void setAgentWasHere(boolean agentWasHere) 
	{
		this.agentWasHere = agentWasHere;
	}
	public boolean isAbortOpening() {
		return abortOpening;
	}
	public void setAbortOpening(boolean abortOpening) {
		this.abortOpening = abortOpening;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
