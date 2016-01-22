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
	    File outputFile = new File(Destinaion+"."+String.format("%03d", part)); //we gaan ervan uit dat we nooit meer dan 999 stukken nodig hebben
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        int packet_size=100000000;
        int remaining=sizeOfFiles;
        
		try {
			outChannel = new FileOutputStream(outputFile, true).getChannel();
			inChannel = (FileChannel.open(source));
			inChannel.position(sizeOfFiles*(part-1));
			
			while(remaining>0)
			{
				if(remaining<packet_size)
				{
					packet_size=packet_size/10;
				}
				else
				{
					ByteBuffer buf = ByteBuffer.allocateDirect(packet_size);
					inChannel.read(buf);
					buf.flip();
					outChannel.write(buf);
					buf.flip();
					remaining=remaining-packet_size;
					buf.clear();
				}
			}
			//lees de grote van buffer in (in buf) vertrekkend van de grote van de stukken maal het aantal stukken
			//er moet -1 gedaan worden aangezien we altijd met part 1 beginnen (zo zal eerste part van pos 0 beginnen)
			//(de rede hiervoor is dat de stukken dan gemakkelijk met winrar gemergt konden worden tijdens het ontwerpen.)
			
			//de grote van de buffer wordt gevarieerd van 100 MB naar 1 bit. Zo kan ervoor gezorgt worden dat we niet
			//de hele file in de buffer moeten plaatsen (te weinig heap memory voor grote files). Omdat we maar tot een 
			//bepaalde positie mogen inlezen moet er een precisie tot op de bit aanwezig zijn. Om te vermijden dat we 
			//bit per bit moeten overplaatsen (zou te intensief zijn voor de processor) laten we de inlees buffer grote varieren.
		} catch (IOException e) {}        
		try {
			outChannel.close();
			inChannel.close();
		} catch (IOException e) {}
	}
}
