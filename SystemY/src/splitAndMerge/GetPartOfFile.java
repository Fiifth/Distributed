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
	public static void getPart(int sizeOfFiles, int part,Path f) throws FileNotFoundException, IOException
	{
		        
        FileChannel fc = (FileChannel.open(f));
        ByteBuffer buf = ByteBuffer.allocateDirect(sizeOfFiles);;
        ByteBuffer.allocateDirect(sizeOfFiles);
        
        fc.read(buf,sizeOfFiles*(part-1));
        buf.flip();
        File file = new File(f+"."+String.format("%03d", part));

        @SuppressWarnings("resource")
		FileChannel channel = new FileOutputStream(file, true).getChannel();
        channel.write(buf);
        buf.clear();
        channel.close();        
	}
	public static void main(String[] args) throws IOException 
    {
    	Path path = Paths.get("C:\\Tannenbaum.pdf");
    	int size=1024 * 1024*5;// 1MB
    	getPart(size,2,path);
    }
}
