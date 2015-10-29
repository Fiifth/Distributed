package project;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class CreateLocaleFileQueueThread extends Thread
{
	Queue<String> fnQueue;
	ArrayList<String> fnList;
	String dirToSearch;
	public CreateLocaleFileQueueThread()
	{
		fnQueue = new LinkedList<String>();
		fnList = new ArrayList<String>();
		dirToSearch = "C:\\SystemYNodeFiles";
	}
	public void searchFilesToAdd()
	{
		File folder = new File(dirToSearch);
		File[] listOfFilesInDir = folder.listFiles();
		for (File file: listOfFilesInDir){
			if(file.isFile()){
				String fileName = file.getName();
				fnQueue.add(fileName);
				fnList.add(fileName);
			}
		}	
	}
	
	public void updateQueueWithNewFiles(){
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
	}

}
