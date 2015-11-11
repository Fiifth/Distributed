package fileManagers;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import nodeP.NodeData;
import nodeP.RMICommunicationInt;

public class FileDetectionT extends Thread{
	public WatchService watcher;
	String dirToSearch;
	Path dir;

	NodeData nodedata1;
	
	public FileDetectionT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	public void run(){
		
		dirToSearch = nodedata1.getMyLocalFolder();
		dir = Paths.get(dirToSearch);
		//vul lijst en queue met nieuwe map
		firstSearchFilesToAdd();
		try {
			setupWatchService();
		} catch (IOException e) {}
		while(true&&!Thread.interrupted())
		{
			WatchKey key;
			try{
				key = watcher.take();
				
			} catch(InterruptedException ex) {
				return;
			}
			for(WatchEvent<?> event : key.pollEvents()){
				//get event type
				WatchEvent.Kind<?> kind = event.kind();
				 // get file name
				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path fileName = ev.context();
				System.out.println(kind.name() + ": " + fileName);
				 
				if(kind == OVERFLOW){
					continue;
				}
				else if(kind == ENTRY_CREATE){
					System.out.println("new file added");
				}
				else if(kind == ENTRY_MODIFY){
					System.out.println("file modified");
					
					FileData file1=new FileData();
					file1.setNewFileData(fileName.toString(), dirToSearch, nodedata1);
					file1.setSourceIP(file1.getLocalOwnerIP());
					file1.setSourceID(nodedata1.getMyNodeID());
					file1.refreshReplicateOwner(nodedata1, file1);
					nodedata1.sendQueue.add(file1);
					nodedata1.localFiles.add(file1);
				}
				//aan de hand van de filenaam het filedata1 object opzoeken en zo de data doorsturen naar replicatie eigenaar
				else if(kind == ENTRY_DELETE)
				{
					System.out.println("file removed");
					FileData removedFile=null;
			        for (FileData tempfile : nodedata1.localFiles) 
			    	{
			        	if(tempfile.getFileName().equals(fileName.toString()))
			        	{
			        		removedFile = tempfile;
			       		}
			    	}
			        nodedata1.localFiles.remove(removedFile);
			        removedFile.refreshReplicateOwner(nodedata1, removedFile);
			        RMICommunicationInt recInt=null;
			        try {//TODO change to RMI.getRMIObject
						recInt = (RMICommunicationInt) Naming.lookup("//"+removedFile.getReplicateOwnerIP()+":"+removedFile.getReplicateOwnerID()+"/RMICommunication");
						recInt.removeOwner(removedFile);
					} catch (MalformedURLException | RemoteException | NotBoundException e) {e.printStackTrace();}
			        
				}
				boolean valid = key.reset();
				if(!valid){
					return;
				}
			}
		}
		
	}
	//bij startup de lijst en queue vullen me alle bestanden
	public void firstSearchFilesToAdd()
	{
		String DirReplFiles=nodedata1.getMyReplFolder();
		File folder1 = new File(DirReplFiles);
		if (!folder1.exists())
			folder1.mkdir();
		
		File folder = new File(dirToSearch);
		if (!folder.exists())
			folder.mkdir();

		File[] listOfFilesInDir = folder.listFiles();
			for (File file: listOfFilesInDir)
			{
				if(file.isFile())
				{
					String fileName = file.getName();
					FileData file1=new FileData();
					file1.setNewFileData(fileName, dirToSearch, nodedata1);
					file1.refreshReplicateOwner(nodedata1, file1);
					nodedata1.sendQueue.add(file1);
					nodedata1.localFiles.add(file1);
				}
			}	
		}
	
	
	//start watcher
	public void setupWatchService() throws IOException{
		watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		
	}

}	