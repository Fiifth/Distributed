package Functions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP 
{
	public void sendTextWithTCP(String text, String destinationIP, int socket )
	{
		Socket clientSocket;
			try {
				clientSocket = new Socket(destinationIP,socket);
				DataOutputStream outToNode = new DataOutputStream(clientSocket.getOutputStream());
				outToNode.writeBytes(text+ "\n");
				clientSocket.close();
				outToNode.close();
			} catch (IOException e) {e.printStackTrace();}
	}
	public String receiveTextWithTCP(int socket,int timeout)
	{
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		String text = null;
		
		try {
			welcomeSocket = new ServerSocket(socket);
			welcomeSocket.setSoTimeout(timeout);
			connectionSocket = welcomeSocket.accept();
			welcomeSocket.close();
			BufferedReader bufferIN = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			text = bufferIN.readLine();			
			connectionSocket.close();
		} 
		catch (IOException e) {e.printStackTrace();	}
		return text;		
	}
	public void sendFile(String filePath,int socket)
	{
		System.out.print("Sending following file: "+ filePath+": ");
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            BufferedOutputStream outToClient = null;
            FileInputStream fis = null;

            try {
                welcomeSocket = new ServerSocket(socket);
                connectionSocket = welcomeSocket.accept();
                outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
                welcomeSocket.close();
            } catch (IOException ex) { 	System.out.println("Couldn't open socket1");}

            if (outToClient != null) 
            {
                File myFile = new File(filePath);
                byte[] mybytearray = new byte[(int) myFile.length()];

                try {
                    fis = new FileInputStream(myFile);
                } catch (FileNotFoundException ex) {System.out.println("File wasn't found!");}
                BufferedInputStream bis = new BufferedInputStream(fis);

                try {
                    bis.read(mybytearray, 0, mybytearray.length);
                    outToClient.write(mybytearray, 0, mybytearray.length);
                    outToClient.flush();
                    outToClient.close();
                    connectionSocket.close();
                    fis.close();
					bis.close();
                } catch (IOException ex) {System.out.println("Sending file failed!"); } 
                System.out.println("file send!");
        }
	}
	public void receiveFile(String sourceIP,int serverPort, String fileOutput)
	{
		
		FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket clientSocket = null;
        InputStream is = null;
		byte[] aByte = new byte[1];
        int bytesRead;

            try {
				clientSocket = new Socket(sourceIP, serverPort);
				is = clientSocket.getInputStream();
			} catch (IOException e) {System.out.println("couldn't open socket");}	
           
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) 
        {
            try 
            {
                fos = new FileOutputStream(fileOutput);
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(aByte, 0, aByte.length);
                do 
                {
                        baos.write(aByte);
                        bytesRead = is.read(aByte);
                } while (bytesRead != -1);
                bos.write(baos.toByteArray());
                bos.flush();
                bos.close();
                baos.close();
                fos.close();
                clientSocket.close();
            } 
            catch (IOException ex) {System.out.println("File not received");}
        }
	}
}


