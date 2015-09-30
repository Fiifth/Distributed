package TCPFileServerThirdEdition.copy;

import java.io.*;
import java.net.*;

class TCPServer {

   // private final static String fileToSend = "C:\\test1.txt";

    public static void main(String args[]) {
    	
        while (true) {
            ServerSocket welcomeSocket = null;
            Socket connectionSocket = null;
            BufferedOutputStream outToClient = null;

            try {
                welcomeSocket = new ServerSocket(3248);
                System.out.println("Listening...");
                while(true)
                {
                	connectionSocket = welcomeSocket.accept();
                	                	
                }
            } catch (IOException ex) {
                // Do exception handling
            	System.out.println("AJ1");
            }

            if (outToClient != null) {
            	TCPThread tcp1 = new TCPThread(connectionSocket);            	
                }
            }
        }
    }