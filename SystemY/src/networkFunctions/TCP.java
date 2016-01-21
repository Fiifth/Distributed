package networkFunctions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import nodeFileManagers.FileData;

public class TCP 
{
	public boolean sendTextWithTCP(String text, String destinationIP, int socket )
	{
		Socket clientSocket;
			try {
				clientSocket = new Socket(destinationIP,socket);
				DataOutputStream outToNode = new DataOutputStream(clientSocket.getOutputStream());
				outToNode.writeBytes(text+ "\n");
				clientSocket.close();
				outToNode.close();
				return true;
			} catch (IOException e) {return false;}
	}
	public String[] receiveTextWithTCP(int socket,int timeout)
	{
		ServerSocket welcomeSocket = null;
		Socket connectionSocket = null;
		String[] text=new String[2];
		
		try {
			welcomeSocket = new ServerSocket(socket);
			welcomeSocket.setSoTimeout(timeout);
			connectionSocket = welcomeSocket.accept();
			InetAddress originIP=connectionSocket.getInetAddress();
			text[1]=originIP.getHostAddress();
			welcomeSocket.close();
			BufferedReader bufferIN = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			text[0] = bufferIN.readLine();	
			connectionSocket.close();
		} 
		catch (IOException e) {}
		return text;		
	}
	
	
	public boolean receiveFile(String sourceIP,int serverPort, String fileOutput)
	{
		FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Socket clientSocket = null;
        InputStream is = null;
		byte[] aByte = new byte[512];
        int bytesRead=1;
        
		try {
			clientSocket = new Socket(sourceIP, serverPort);
			is = clientSocket.getInputStream();
		} catch (IOException e) {System.out.println("coudln't open socket");return false;}

        if (is != null) 
        {
            try 
            {
                fos = new FileOutputStream(fileOutput);
                bos = new BufferedOutputStream(fos);
                
                while(bytesRead != -1)
                {
                	 bytesRead = is.read(aByte);
                	 if(bytesRead != -1)
                	 {
	                	 bos.write(Arrays.copyOfRange(aByte, 0, bytesRead));
		                	//door gebruik te maken van copy range wordt het potentiëel 
		            		//lege gedeelde van de array nooit in de file geschreven
                	 }
                }

                bos.close();
                fos.close();
                clientSocket.close();
            } catch (IOException ex) {System.out.println("writing file failed");return false;}
        }
        return true;
	}
	
	public void sendFile(FileData file1)
	{
		String filePath = file1.getFolderLocation()+"\\"+file1.getFileName();
        ServerSocket welcomeSocket = null;
        Socket connectionSocket = null;
        BufferedOutputStream outToClient = null;
        FileInputStream fis = null;
        BufferedInputStream bis=null;
        int bytesRead=1;
        try 
        {
            welcomeSocket = new ServerSocket(file1.getSourceID()+32768);
            connectionSocket = welcomeSocket.accept();
            outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());
            welcomeSocket.close();
        } catch (IOException ex) { 	System.out.println("Couldn't open socket1");}

        if (outToClient != null) 
        {
            File myFile = new File(filePath);
            byte[] bytearray = new byte[512];
            try 
            {
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
            } catch (FileNotFoundException ex) {System.out.println("File wasn't found!");}

            try 
            {           
            	while(bytesRead != -1)
            	{
            		bytesRead=bis.read(bytearray, 0, bytearray.length);
            		if(bytesRead != -1)
					{
	            		outToClient.write(Arrays.copyOfRange(bytearray, 0, bytesRead));
		            		//door gebruik te maken van copy range wordt het potentiëel 
		            		//lege gedeelde van de array nooit doorgestuurd. BytesRead
	            			//geeft namelijk op hoeveel bits er ni de array geplaatst zijn.
					}
                }

                outToClient.flush();
                outToClient.close();
                connectionSocket.close();
                fis.close();
				bis.close();
            } catch (IOException ex) {System.out.println("Sending file failed!"); } 
        }
	}
}


