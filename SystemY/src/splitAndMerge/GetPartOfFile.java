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
		       System.out.println("jow"); 
        FileChannel fc = null;
		try {
			fc = (FileChannel.open(source));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ByteBuffer buf = ByteBuffer.allocateDirect(sizeOfFiles);;
        ByteBuffer.allocateDirect(sizeOfFiles);
        
        try {
			fc.read(buf,sizeOfFiles*(part-1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        buf.flip();
        File file = new File(Destinaion+"."+String.format("%03d", part));

        @SuppressWarnings("resource")
		FileChannel channel = null;
		try {
			channel = new FileOutputStream(file, true).getChannel();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			channel.write(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        buf.clear();
        try {
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	}
	public static void main(String[] args) throws IOException 
    {
    	Path source = Paths.get("C:\\Tannenbaum.pdf");
    	Path dest = Paths.get("C:\\TannenbaumSplit.pdf");
    	int size=1024 * 1024*5;// 1MB
    	//getPart(size,2,source,dest);
    }
}
