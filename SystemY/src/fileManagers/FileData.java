package fileManagers;

import java.io.Serializable;
import java.rmi.Naming;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private volatile String fileName;
	private volatile String folderLocation;
	private volatile String localOwnerIP;
	private volatile int localOwnerID;
	private volatile String sourceIP;
	private volatile int sourceID;
	private volatile int replicateOwnerID;
	private volatile String replicateOwnerIP;
	private volatile boolean removeAfterSend;

	
	public void setNewFileData(String fileName, NodeData nodedata1)
	{
		this.fileName=fileName;
		folderLocation=nodedata1.getMyLocalFolder();
		localOwnerIP=nodedata1.getMyIP();
		localOwnerID=nodedata1.getMyNodeID();
	}
	
	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}

	public String getFileName() {
		return fileName;
	}
	public String getFolderLocation() {
		return folderLocation;
	}
	public String getLocalOwnerIP() {
		return localOwnerIP;
	}
	public int getLocalOwnerID() {
		return localOwnerID;
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

	public boolean refreshReplicateOwner(NodeData nodedata1,FileData filedata1)
	{
		String[] ipAndIDArray=null;
		try {//TODO if return false --> start failure
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			String ipAndID = nameserver.locateFile(getFileName());
			ipAndIDArray=ipAndID.split("-");
		} catch (Exception e) {System.out.println("failed connection to RMI of the server and get ip");}
		filedata1.replicateOwnerIP=ipAndIDArray[0];
		filedata1.replicateOwnerID=Integer.parseInt(ipAndIDArray[1]);
		//System.out.println(replicateOwnerID+nodedata1.getMyNodeID());
		 return !(replicateOwnerID==nodedata1.getMyNodeID());
			
	}
}
