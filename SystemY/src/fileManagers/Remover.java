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
			while(nodedata1.replFiles.contains(file1))
			{nodedata1.replFiles.remove(file1);}
			Path source = Paths.get(file1.getFolderLocation()+"\\"+file1.getFileName());
			try {
				Files.delete(source);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TODO in fileData --> als localowner op 0 wordt gezet via RMIcommunication moet de file in kwestie
			//ook hier geplaatst worden
			//TODO er moet nog een extra check in gevoerd worden zodat hij pas verwijderd wordt wanneer de file
			//niet meer in gebruik is (belangrijk bij het verzenden van replicatie bestanden naar nieuwe replicatie
			//eigenaar (extra atomic boolean, nu opgelost met delay)
		}
		
	}
}
