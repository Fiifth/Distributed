package nodeFileManagers;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.rmi.RemoteException;

import networkFunctions.RMI;
import nodeManager.RMICommunicationInt;
import nodeStart.NodeData;

public class FileDetectionT extends Thread{
	public WatchService watcher;
	String dirToSearch;
	Path dir;
	RMI rmi=new RMI();

	NodeData nodedata1;
	
	public FileDetectionT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	@SuppressWarnings("unchecked")
	public void run()
	{
		dir = Paths.get(nodedata1.getMyLocalFolder());
		firstSearchFilesToAdd(); //vul lijst en queue met nieuwe map
		setupWatchService();		
		while(!Thread.interrupted())
		{
			WatchKey key;
			try
			{
				key = watcher.take();
			} catch(InterruptedException ex) 
			{
				try {watcher.close();} catch (IOException e) {}
				return;
			}
			for(WatchEvent<?> event : key.pollEvents())
			{
				WatchEvent.Kind<?> kind = event.kind(); //get event type
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path fileName = ev.context();	// get file name
				if(kind == ENTRY_CREATE)
				{
					//do nothing
				}
				else if(kind == ENTRY_MODIFY)
				{
					try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
					File varTmpDir = new File(nodedata1.getMyLocalFolder()+"\\"+fileName);
					if ((varTmpDir.exists()))
					{
					addFile(fileName.toString(),varTmpDir.length());
					}
				}
				else if(kind == ENTRY_DELETE)
				{
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}	
					File varTmpDir = new File(nodedata1.getMyLocalFolder()+"\\"+fileName);
					if (!(varTmpDir.exists()))
					{
						int fileNameHash=Math.abs(fileName.toString().hashCode()%32768);
						FileData removedFile=nodedata1.localFiles.get(fileNameHash);
				        removedFile.refreshReplicateOwner(nodedata1);
				        try 
				        {
				        	RMICommunicationInt recInt = (RMICommunicationInt) rmi.getRMIObject(removedFile.getReplicateOwnerID(), removedFile.getReplicateOwnerIP(), "RMICommunication");
							recInt.removeThisOwner(removedFile);
						} catch (RemoteException e) {e.printStackTrace();}
				        nodedata1.localFiles.remove(fileNameHash);
					}
				}
				key.reset();
			}
		}
		try {watcher.close();} catch (IOException e) {}
	}
	
	public void firstSearchFilesToAdd()
	{
		File folder1 = new File(nodedata1.getMyReplFolder());
		if (!folder1.exists())
			folder1.mkdir();
		
		File folder = new File(nodedata1.getMyLocalFolder());
		if (!folder.exists())
			folder.mkdir();

		File[] listOfFilesInDir = folder.listFiles();
			for (File file: listOfFilesInDir)
			{
				if(file.isFile())
				{
					addFile(file.getName(),file.length());
				}
			}
		}
	
	public void setupWatchService() 
	{
		try {
			watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (IOException e) {}
	}
	public void addFile(String fileName,long size)
	{
		int fileNameHash=Math.abs(fileName.hashCode()%32768);
		if (!nodedata1.localFiles.containsKey(fileNameHash))
		{
			FileData file1=new FileData();
			file1.setNewFileData(fileName, nodedata1);
			file1.setDestinationFolder("rep");
			file1.addOwner(nodedata1.getMyNodeID(),nodedata1.getMyIP());
			file1.setSourceID(nodedata1.getMyNodeID());
			file1.refreshReplicateOwner(nodedata1);
			file1.setSize(size);
			nodedata1.sendQueue.add(file1);
			nodedata1.localFiles.put(fileNameHash,file1);
			nodedata1.setChanged(true);
		}		
	}
}	