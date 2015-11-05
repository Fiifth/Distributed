package fileManagers;

import java.rmi.Naming;

import nameServer.NameServerInterface;
import nodeP.NodeData;

public class FileData 
{
	private volatile String fileName;
	private volatile String localPath="C:\\SystemYNodeFiles";
	private volatile String localOwnerIP;
	private volatile int localOwnerID;
	private volatile int replicateOwnerID;
	private volatile String replicateOwnerIP;
	private volatile String replicatePath="c:\\SystemYNodeFilesRep";
	
	public void setNewFileData(String fileName, String localPath, NodeData nodedata1)
	{
		this.fileName=fileName;
		this.localPath=localPath;
		localOwnerIP=nodedata1.getMyIP();
		localOwnerID=nodedata1.getMyNodeID();
	}
	
	public String getFileName() {
		return fileName;
	}
	public String getLocalPath() {
		return localPath;
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
	public String getReplicatePath() {
		return replicatePath;
	}
	
	
	public boolean refreshReplicateOwner(NodeData nodedata1)
	{
		String[] ipAndIDArray=null;
		try {
			NameServerInterface nameserver = (NameServerInterface)Naming.lookup("//"+nodedata1.getNameServerIP()+":1099/NameServer");
			String ipAndID = nameserver.locateFile(getFileName());
			ipAndIDArray=ipAndID.split("-");
		} catch (Exception e) {System.out.println("failed connect to RMI of the server and get ip");}
		replicateOwnerIP=ipAndIDArray[0];
		replicateOwnerID=Integer.parseInt(ipAndIDArray[1]);
		if (replicateOwnerIP==nodedata1.getMyIP()&&(replicateOwnerID==nodedata1.getMyNodeID()))
			return false;
			else return true;
	}
}
