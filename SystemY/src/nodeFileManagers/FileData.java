package nodeFileManagers;

import java.io.Serializable;
import java.rmi.Naming;
import java.util.TreeMap;

import nameServer.NameServerInterface;
import nodeStart.NodeData;

public class FileData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private volatile String fileName;
	private volatile String folderLocation;
	public volatile TreeMap<Integer,String> localOwners = new TreeMap<Integer,String>();
	private volatile String sourceIP;
	private volatile int sourceID;
	private volatile int replicateOwnerID;
	private volatile String replicateOwnerIP;
	private volatile boolean removeAfterSend;
	private volatile int destinationID;
	private volatile String destinationIP;
	private volatile String destinationFolder;
	private volatile long size;
	private volatile int partID;
	private volatile int partSize;
	private volatile boolean lock;
	
	public void deepCopy(FileData file1)
	{
		this.fileName=file1.fileName;
		this.folderLocation=file1.folderLocation;
		this.localOwners=file1.localOwners;
		this.sourceIP=file1.sourceIP;
		this.sourceID=file1.sourceID;
		this.destinationID=file1.destinationID;
		this.destinationIP=file1.destinationIP;
		this.destinationFolder=file1.destinationFolder;
		this.size=file1.size;
		this.localOwners=file1.localOwners;
		this.partID=file1.partID;
		this.partSize=file1.partSize;
	}
	public TreeMap<Integer, String> getLocalOwners()
	{
		return localOwners;
	}
	public int getNumberOfOwners()
	{
		return localOwners.size();
	}
	public void addOwner (int ownerID,String ip)
	{
		localOwners.put(ownerID, ip);
	}
	public boolean removeOwner (Integer ownerID)
	{
		if (localOwners.containsKey(ownerID))
			localOwners.remove(ownerID);
		return localOwners.isEmpty();
	}
	public boolean isOwner(int ownerID)
	{
		return localOwners.containsKey(ownerID);		
	}
	public void setNewFileData(String fileName, NodeData nodedata1)
	{
		this.fileName=fileName;
		lock=false;
		folderLocation=nodedata1.getMyLocalFolder();
	}
	
	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFolderLocation() {
		return folderLocation;
	}

	public int getReplicateOwnerID() {
		return replicateOwnerID;
	}
	public String getReplicateOwnerIP() {
		return replicateOwnerIP;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}

	public int getSourceID() {
		return sourceID;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}

	public boolean getRemoveAfterSend() {
		return removeAfterSend;
	}

	public void setRemoveAfterSend(boolean removeAfterSend) {
		this.removeAfterSend = removeAfterSend;
	}

	public int getDestinationID() {
		return destinationID;
	}

	public void setDestinationID(int destinationID) {
		this.destinationID = destinationID;
	}

	public String getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(String destinationIP) {
		this.destinationIP = destinationIP;
	}

	public String getDestinationFolder() {
		return destinationFolder;
	}

	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	public boolean isLock() {
		return lock;
	}
	public void setLock(boolean lock) {
		this.lock = lock;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public int getPartSize() {
		return partSize;
	}
	public void setPartSize(int partSize) {
		this.partSize = partSize;
	}
	public int getPartID() {
		return partID;
	}
	public void setPartID(int partID) {
		this.partID = partID;
	}
	public boolean refreshReplicateOwner(NodeData nodedata1)
	{
		String[] ipAndIDArray=null;
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			ipAndIDArray = nameserver.locateFile(getFileName());
		} catch (Exception e) {System.out.println("failed connection to RMI of the server and get ip");}
		this.replicateOwnerIP=ipAndIDArray[0];
		this.replicateOwnerID=Integer.parseInt(ipAndIDArray[1]);
		this.destinationID = replicateOwnerID;
		this.destinationIP = replicateOwnerIP;
		this.setDestinationFolder("rep");
		 return !(replicateOwnerID==nodedata1.getMyNodeID());	
	}
}
