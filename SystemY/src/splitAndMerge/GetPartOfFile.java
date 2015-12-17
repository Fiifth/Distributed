package splitAndMerge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetPartOfFile 
{
	@SuppressWarnings("resource")
	public void getPart(int sizeOfFiles, int part,Path source,Path Destinaion)
	{
		      
        FileChannel fc = null;
		try {
			fc = (FileChannel.open(source));
		} catch (IOException e) {}
        ByteBuffer buf = ByteBuffer.allocateDirect(sizeOfFiles);;
        ByteBuffer.allocateDirect(sizeOfFiles);
        
        try {
			fc.read(buf,sizeOfFiles*(part-1));
		} catch (IOException e) {}
        buf.flip();
        File file = new File(Destinaion+"."+String.format("%03d", part));
        FileChannel channel = null;
		try {
			channel = new FileOutputStream(file, true).getChannel();
		} catch (FileNotFoundException e) {}
        try {
			channel.write(buf);
		} catch (IOException e) {}
        buf.clear();
        try {
			channel.close();
		} catch (IOException e) {}        
	}
}
