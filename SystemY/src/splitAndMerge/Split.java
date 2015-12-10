package splitAndMerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

class Split 
{
    public static void splitFile(Path f) throws IOException 
    {
    	int partNum=1;
        int sizeOfFiles = 1024 * 1024;// 1MB
        
        FileChannel fc = (FileChannel.open(f));
        ByteBuffer buf = ByteBuffer.allocateDirect(sizeOfFiles);;
        ByteBuffer.allocateDirect(sizeOfFiles);
        
        while(fc.read(buf,sizeOfFiles*(partNum-1))>0)
        {
	        buf.flip();
	        File file = new File(f+"."+String.format("%03d", partNum++));
	
	        @SuppressWarnings("resource")
			FileChannel channel = new FileOutputStream(file, true).getChannel();
	        channel.write(buf);
	        buf.clear();
	        channel.close();
        }
   }
    

    public static void main(String[] args) throws IOException 
    {
    	Path path = Paths.get("C:\\Tannenbaum.pdf");
    	
        splitFile(path);
    }
}