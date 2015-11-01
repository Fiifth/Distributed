package project;

public class NodeData {
	private volatile String nodeName;
	private volatile int prevNode;
	private volatile int nextNode;
	private volatile int myNodeID;
	private volatile int toLeave;
	private volatile String nameServerIP;
	
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

	
}
