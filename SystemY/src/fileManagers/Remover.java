package fileManagers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import nodeP.NodeData;

public class Remover extends Thread 
{
	NodeData nodedata1;
	public Remover(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		while(nodedata1.getToLeave()==0)
		{
			FileData file1=null;
			try {
				file1=nodedata1.removeQueue.take();
			} catch (InterruptedException e) {e.printStackTrace();}
			Path source = Paths.get(file1.getFolderLocation()+"\\"+file1.getFileName());
			try {
				Files.delete(source);
			} catch (IOException e) {System.out.println("file couldn't be deleted");}			
		}
		
	}
}
