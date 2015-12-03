package testCode;

import java.io.IOException;
import nameServer.StartNameServer;
import nodeP.StartNode;

public class NodeOrderTest {

	public static void main(String[] args) throws IOException, InterruptedException 
	{
		StartNameServer nameserver= new StartNameServer();
		nameserver.startNameServer();
		Thread.sleep(1000);
		StartNode node2=new StartNode("3");		//first node
		node2.startNewNode();
		Thread.sleep(1000);
		StartNode node3=new StartNode("5");		//second node
		node3.startNewNode();
		Thread.sleep(1000);
		StartNode node5=new StartNode("7");		//add node at end
		node5.startNewNode();
		Thread.sleep(1000);
		StartNode dub=new StartNode("7");		//adding a nodename that already exists
		dub.startNewNode();
		Thread.sleep(1000);
		StartNode node1=new StartNode("1");		//add node in beginning
		node1.startNewNode();
		Thread.sleep(1000);
		StartNode node4=new StartNode("6");		//add node in the middle
		node4.startNewNode();
		
		Thread.sleep(5000);
		
		boolean check1=node1.nodedata1.getNextNode() == node2.nodedata1.getMyNodeID();
		boolean check2=node1.nodedata1.getPrevNode() == node5.nodedata1.getMyNodeID();
		boolean check3=node2.nodedata1.getNextNode() == node3.nodedata1.getMyNodeID();
		boolean check4=node2.nodedata1.getPrevNode() == node1.nodedata1.getMyNodeID();
		boolean check5=node3.nodedata1.getNextNode() == node4.nodedata1.getMyNodeID();
		boolean check6=node3.nodedata1.getPrevNode() == node2.nodedata1.getMyNodeID();
		boolean check7=node4.nodedata1.getNextNode() == node5.nodedata1.getMyNodeID();
		boolean check8=node4.nodedata1.getPrevNode() == node3.nodedata1.getMyNodeID();
		boolean check9=node5.nodedata1.getNextNode() == node1.nodedata1.getMyNodeID();
		boolean check10=node5.nodedata1.getPrevNode() == node4.nodedata1.getMyNodeID();
		//System.out.println(check1+", "+check2+", "+check3+", "+check4+", "+check5+", "+check6+", "+check7+", "+check8+", "+check9+", "+check10);
		
		if (check1&&check2&&check3&&check4&&check5&&check6&&check7&&check8&&check9&&check10)
		{
			System.out.println("Test 1 is successfull");
		}
		else
			System.out.println("nope");
		
		node2.nodedata1.setToQuit(true);
		Thread.sleep(10000);
		check1=node1.nodedata1.getNextNode() == node3.nodedata1.getMyNodeID();
		check2=node1.nodedata1.getPrevNode() == node5.nodedata1.getMyNodeID();
		check3=node3.nodedata1.getNextNode() == node4.nodedata1.getMyNodeID();
		check4=node3.nodedata1.getPrevNode() == node1.nodedata1.getMyNodeID();
		if (check1&&check2&&check3&&check4)
		{
			System.out.println("Test 2 is successfull");
		}
		else
			System.out.println("nope");
		System.exit(1);
	}

}
