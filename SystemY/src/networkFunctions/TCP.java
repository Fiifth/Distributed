package networkFunctions;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

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
        int bytesRead;
        
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
                bytesRead = is.read(aByte, 0, aByte.length);
                do 
                {
                	bos.write(aByte);
                    bytesRead = is.read(aByte);
                } while (bytesRead != -1);
                
                bos.flush();
                bos.close();
                fos.close();
                clientSocket.close();
            } catch (IOException ex) {System.out.println("writing file failed");return false;}
        }
        return true;
	}
}


