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
	    File outputFile = new File(Destinaion+"."+String.format("%03d", part)); //we gaan ervan uit dat we nooit meer dan 999 stukken nodig hebben
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        
		try {
			inChannel = (FileChannel.open(source));
			inChannel.read(buf,sizeOfFiles*(part-1)); 
			//lees de grote van buf in (in buf) vertrekkend van de grote van de stukken maal het aantal stukken
			//er moet -1 gedaan worden aangezien we altijd met part 1 beginnen (zo zal eerste part van 0 beginnen)
			//(de rede hiervoor is dat we de stukken dan gemakkelijk met winrar ook konden mergen tijdens het ontwerpen.)
		} catch (IOException e) {}
        
        buf.flip(); //verander van lees naar schrijf
        
		try {
			outChannel = new FileOutputStream(outputFile, true).getChannel();
			outChannel.write(buf);
			buf.clear();
			outChannel.close();
			inChannel.close();
		} catch (IOException e) {}       
	}
}
