package fileManagers;

import java.io.Serializable;
import java.rmi.Naming;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private volatile String fileName;
	private volatile String sourcePath="C:\\SystemYNodeFiles";
	private volatile String localOwnerIP;
	private volatile int localOwnerID; //TODO keep list of localOwners
	private volatile int replicateOwnerID;
	private volatile String replicateOwnerIP;
	private volatile boolean replicateDataSet=false;
	
	public void setNewFileData(String fileName, String localPath, NodeData nodedata1)
	{
		this.fileName=fileName;
		this.sourcePath=localPath;
		localOwnerIP=nodedata1.getMyIP();
		localOwnerID=nodedata1.getMyNodeID();
	}
	
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getFileName() {
		return fileName;
	}
	public String getSourcePath() {
		return sourcePath;
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
	
	public boolean getReplicateDataSet() {
		return replicateDataSet;
	}

	public void setReplicateDataSet(boolean b) {
		this.replicateDataSet = b;
	}

	public boolean refreshReplicateOwner(NodeData nodedata1,FileData filedata1)
	{
		String[] ipAndIDArray=null;
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			String ipAndID = nameserver.locateFile(getFileName());
			ipAndIDArray=ipAndID.split("-");
		} catch (Exception e) {System.out.println("failed connect to RMI of the server and get ip");}
		filedata1.replicateOwnerIP=ipAndIDArray[0];
		filedata1.replicateOwnerID=Integer.parseInt(ipAndIDArray[1]);
		filedata1.setReplicateDataSet(true);
		//System.out.println(replicateOwnerIP+nodedata1.getMyIP()+replicateOwnerID+nodedata1.getMyNodeID());
		 return !(replicateOwnerID==nodedata1.getMyNodeID());
			
	}
}
