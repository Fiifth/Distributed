package splitAndMerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class GetPartOfFile 
{
	@SuppressWarnings("resource")
	public void getPart(int sizeOfFiles, int part,Path source,Path Destinaion)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(sizeOfFiles);;
	    ByteBuffer.allocateDirect(sizeOfFiles);
	    File outputFile = new File(Destinaion+"."+String.format("%03d", part));
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        
		try {
			inChannel = (FileChannel.open(source));
			inChannel.read(buf,sizeOfFiles*(part-1));
		} catch (IOException e) {}
        
        buf.flip();
        
		try {
			outChannel = new FileOutputStream(outputFile, true).getChannel();
			outChannel.write(buf);
			buf.clear();
			outChannel.close();
		} catch (IOException e) {}       
	}
}
