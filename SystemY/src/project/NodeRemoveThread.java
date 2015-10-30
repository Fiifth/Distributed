package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NodeRemoveThread extends Thread

{
	String input = null;
	int myNodeID;
	int myPrevNode;
	int myNextNode;
	
	public NodeRemoveThread(int myNextNode,int myPrevNode,int myNodeID)
	{
		this.myNodeID=myNodeID;
		this.myPrevNode=myPrevNode;
		this.myNextNode=myNextNode;
	}
	public void run() 
	{
		boolean stay = true;
		while(stay)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(input.equals("quit"))
			{
				//sendMulticast = static?
				//Node rmNode = new Node();
				//rmNode.sendMulticast("1"+"-"+myNodeID+"-"+myPrevNode+"-"+myNextNode);
				
				Node.sendMulticast("1"+"-"+myNodeID+"-"+myPrevNode+"-"+myNextNode);
				stay = false;
			}
		}
		
	}
}
