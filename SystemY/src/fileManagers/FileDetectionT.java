package fileManagers;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import nodeP.NodeData;



public class FileDetectionT extends Thread{
	public WatchService watcher;
	String dirToSearch = "C:\\SystemYNodeFiles";
	Path dir = Paths.get(dirToSearch);

	NodeData nodedata1;
	
	public FileDetectionT(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;

		dirToSearch = "C:\\SystemYNodeFiles";
	}
	/*public static void main(String args[]){
		CreateLocaleFileQueueThread q = new CreateLocaleFileQueueThread();
		q.run();
	}*/
	public void run(){
		//vul lijst en queue met nieuwe map
		searchFilesToAdd();
		try {
			setupWatchService();
		} catch (IOException e) {}
		while(true)
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
					nodedata1.toSendFileNameAndDirList.add((fileName.toString())+"-"+dirToSearch);
					nodedata1.localFiles.add(fileName.toString());
				}
				else if(kind == ENTRY_MODIFY){
					System.out.println("file modified");
				}
				//TODO replicating node inlichten over removal
				/*else if(kind == ENTRY_DELETE){
					
					System.out.println("file removed");
					nodedata1.fnList.remove(fileName.toString());
					
				}*/
				boolean valid = key.reset();
				if(!valid){
					break;
				}
			}
		}
	}
	//bij startup de lijst en queue vullen me alle bestanden
	public void searchFilesToAdd()
	{
		File folder = new File(dirToSearch);
		File[] listOfFilesInDir = folder.listFiles();
		for (File file: listOfFilesInDir){
			if(file.isFile()){
				String fileName = file.getName();
				nodedata1.toSendFileNameAndDirList.add(fileName+"-"+dirToSearch);
				nodedata1.localFiles.add(fileName+"-"+dirToSearch);
			}
		}	
	}
	
	//waarschijnlijk nie meer nodig
	/*public void updateQueueWithNewFiles(){
		File folder = new File(dirToSearch);
		File[] listOfFilesInDir = folder.listFiles();
		for (File file: listOfFilesInDir){
			if(file.isFile()){
				String fileName = file.getName();
				if(!fnList.contains(fileName)){
					fnQueue.add(fileName);
				}
			}
		}	
	}*/
	//start watcher
	public void setupWatchService() throws IOException{
		watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	}

}	