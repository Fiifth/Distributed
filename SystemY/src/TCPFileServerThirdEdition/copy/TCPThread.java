package TCPFileServerThirdEdition.copy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class TCPThread extends Thread{
		
	Socket connectionSocket = null;
	BufferedOutputStream outToClient = null;
	
	public TCPThread(Socket connectionSocket) {
		
		
		try {
			outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.start();
	}

	public void run()
	{
		String fileToSend = "C:\\test.rar";
		File myFile = new File( fileToSend );
        byte[] mybytearray = new byte[(int) myFile.length()];

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(myFile);
        } catch (FileNotFoundException ex) {
        	System.out.println("AJ2");
        }
        BufferedInputStream bis = new BufferedInputStream(fis);

        try {
            bis.read(mybytearray, 0, mybytearray.length);
            outToClient.write(mybytearray, 0, mybytearray.length);
            outToClient.flush();
            outToClient.close();
            connectionSocket.close();
            // File sent, exit the main method
            return;
        } catch (IOException ex) {
        	System.out.println("AJ3");
	}

}
}
