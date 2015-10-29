package project;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class CreateLocaleFileQueueThread extends Thread
{
	public static void main(String[] args){
		CreateLocaleFileQueueThread test = new CreateLocaleFileQueueThread();
		test.searchFilesToAdd();
	}
	Queue<String> myQueue;
	String dirToSearch;
	public CreateLocaleFileQueueThread()
	{
		myQueue = new LinkedList<String>();
		dirToSearch = "C:\\SystemYNodeFiles";
	}
	public void searchFilesToAdd()
	{
		File folder = new File(dirToSearch);
		File[] listOfFilesInDir = folder.listFiles();
		for (File file: listOfFilesInDir){
			if(file.isFile()){
				String fileName = file.getName();
				myQueue.add(fileName);
				System.out.println(fileName + "\n");
			}
		}
		
	}

}
