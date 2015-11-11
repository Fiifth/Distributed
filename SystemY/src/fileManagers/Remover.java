package fileManagers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		while(true)
		{
			FileData file1=null;
			try {
				file1=nodedata1.removeQueue.take();
			} catch (InterruptedException e) {return;}
			Path source = Paths.get(file1.getFolderLocation()+"\\"+file1.getFileName());
			try {
				Files.delete(source);
			} catch (IOException e) {System.out.println("file couldn't be deleted");}			
		}
		
	}
}
