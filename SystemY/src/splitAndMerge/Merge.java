package splitAndMerge;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import nodeFileManagers.FileData;

public class Merge 
{	
	public void mergeFiles(ArrayList<File> files, File into) 
	{
	    try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))) 
	    {
	        for (File f : files) 
	        {
	            Files.copy(f.toPath(), mergingStream);
	        	Files.delete(f.toPath());
	        }
	    } catch (Exception e) {System.out.println("could not merge file");}
	}
}
