package testCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import nameServer.StartNameServer;
import nodeP.StartNode;

public class FileReplicationTest {

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		StartNameServer nameserver= new StartNameServer();
		nameserver.startNameServer();
		Thread.sleep(1000);
		
		StartNode node2=new StartNode("5");		
		node2.startNewNode();
		Thread.sleep(1000);
		StartNode node1=new StartNode("2");		
		node1.startNewNode();
		Thread.sleep(1000);
		StartNode node3=new StartNode("7");		
		node3.startNewNode();
		Thread.sleep(1000);
		
		//file replication test
		copyFile("C:\\files",node2.nodedata1.getMyLocalFolder(),"4");
		copyFile("C:\\files",node2.nodedata1.getMyLocalFolder(),"6");
		copyFile("C:\\files",node2.nodedata1.getMyLocalFolder(),"8");
		Thread.sleep(5000);
		boolean check1=fileExists(node1.nodedata1.getMyReplFolder(),"4")&&node1.nodedata1.replFiles.containsKey(Math.abs("4".hashCode()%32768));
		boolean check2=fileExists(node2.nodedata1.getMyReplFolder(),"6")&&node2.nodedata1.replFiles.containsKey(Math.abs("6".hashCode()%32768));
		boolean check3=fileExists(node3.nodedata1.getMyReplFolder(),"8")&&node3.nodedata1.replFiles.containsKey(Math.abs("8".hashCode()%32768));
		boolean test1=check1&&check2&&check3;

		
		//remove file test
		removeFile(node2.nodedata1.getMyLocalFolder(),"8");
		Thread.sleep(1000);
		check3=fileExists(node3.nodedata1.getMyReplFolder(),"8");	
		boolean test2=!check3;
		
		//NEW REPLICATE FILE OWNER test
		StartNode newNode1=new StartNode("3");		
		newNode1.startNewNode();
		Thread.sleep(5000);
		check2=fileExists(newNode1.nodedata1.getMyReplFolder(),"4")&&newNode1.nodedata1.replFiles.containsKey(Math.abs("4".hashCode()%32768));;
		check3=fileExists(node1.nodedata1.getMyReplFolder(),"4")||node1.nodedata1.replFiles.containsKey(Math.abs("4".hashCode()%32768));;
		boolean test3=check2&&!check3;
		
		
		//leaving node test
		node2.nodedata1.setToQuit(true);
		Thread.sleep(5000);
		check1=fileExists(newNode1.nodedata1.getMyReplFolder(),"4")||newNode1.nodedata1.replFiles.containsKey(Math.abs("4".hashCode()%32768));
		check2=fileExists(node2.nodedata1.getMyReplFolder(),"6")||node2.nodedata1.replFiles.containsKey(Math.abs("6".hashCode()%32768));;
		boolean test4=!check1&&!check2;
		
		System.out.println(test1+", "+test2+", "+test3+", "+test4);
		if (test1&&test2&&test3&&test4)
			System.out.println("all tests were successful");
	
		System.exit(1);
	}
	
	public static void copyFile(String sourceFolder, String destinationFolder, String fileName)
	{
		//Path source = Paths.get(nodedata1.getMyLocalFolder()+"\\"+file1.getFileName());
		Path source = Paths.get(sourceFolder+"\\"+fileName);
		Path destination = Paths.get(destinationFolder+"\\"+fileName);
		try 
		{
			Files.copy(source,destination,StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {System.out.println("couldn't copy file");}
	}
	public static boolean fileExists(String folder, String fileName)
	{
		File varTmpDir = new File(folder+"\\"+fileName);
		return varTmpDir.exists();
	}
	
	public static void removeFile(String folder, String fileName)
	{
		Path source = Paths.get(folder+"\\"+fileName);
		try {
			Files.delete(source);
		} catch (IOException e) {}
		
	}

}
