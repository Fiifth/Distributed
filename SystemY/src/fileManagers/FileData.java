package fileManagers;

import java.io.Serializable;
import java.rmi.Naming;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileData implements Serializable
{
	private static final long serialVersionUID = 1L;
	private volatile String fileName;
	private volatile String folderLocation="C:\\SystemYNodeFiles";
	private volatile String localOwnerIP;
	private volatile String sourceIP;
	private volatile int localOwnerID; //TODO keep list of localOwners
	private volatile int replicateOwnerID;
	private volatile String replicateOwnerIP;

	
	public void setNewFileData(String fileName, String folderLocation, NodeData nodedata1)
	{
		this.fileName=fileName;
		this.folderLocation=folderLocation;
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

	public boolean refreshReplicateOwner(NodeData nodedata1,FileData filedata1)
	{
		String[] ipAndIDArray=null;
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			String ipAndID = nameserver.locateFile(getFileName());
			ipAndIDArray=ipAndID.split("-");
		} catch (Exception e) {System.out.println("failed connection to RMI of the server and get ip");}
		filedata1.replicateOwnerIP=ipAndIDArray[0];
		filedata1.replicateOwnerID=Integer.parseInt(ipAndIDArray[1]);
		//System.out.println(replicateOwnerIP+nodedata1.getMyIP()+replicateOwnerID+nodedata1.getMyNodeID());
		 return !(replicateOwnerID==nodedata1.getMyNodeID());
			
	}
}
