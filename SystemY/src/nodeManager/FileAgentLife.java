package nodeManager;

import nodeStart.NodeData;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.TreeMap;

import networkFunctions.RMI;
import nodeAgent.AgentMain;
import nodeFileManagers.FileData;

public class FileAgentLife extends Thread 
{
	NodeData nodedata1;
	RMI rmi=new RMI();
	public FileAgentLife(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	
	public void run()
	{
		try {Thread.sleep(5000);} catch (InterruptedException e) {}
		int notPresTime=0;
		int misses=0;
		while(!Thread.interrupted())
		{
			try {Thread.sleep(5000);} catch (InterruptedException e) {}
			if (nodedata1.getPrevNode()!=nodedata1.getNextNode())
			{
				if (nodedata1.wasAgentHere())
				{
					nodedata1.setAgentWasHere(false);
					notPresTime=0;
					misses=0;
				}
				else
				{
					notPresTime=notPresTime+5;
				}
				int numOfnod=nodedata1.allNetworkFiles.size();
				int cycleTimeAgent=numOfnod*2;
				if (cycleTimeAgent==0) cycleTimeAgent=2;
				if (notPresTime>cycleTimeAgent)
				{
					misses=misses+(notPresTime/cycleTimeAgent);
					notPresTime=0;
				}
				if (nodedata1.getPrevNode()>nodedata1.getNextNode())
				{
					if(misses>3)
					{
						startAgent();
						misses=0;
					}
				}
				else if (nodedata1.getPrevNode()<nodedata1.getNextNode())
				{
					if(misses>6)
					{
						startAgent();
						misses=0;
					}
				}
				else
				{
					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(20);
					if(misses>(10+randomInt))
					{
						startAgent();
						misses=0;
					}
				}
			}
		}
	}

	private void startAgent() {
		TreeMap<Integer, TreeMap<Integer,FileData>> initTree = new TreeMap<Integer, TreeMap<Integer,FileData>>();
		TreeMap<Integer,FileData> agentLockList=new TreeMap<Integer,FileData>();
		TreeMap<Integer,Integer> removeMap=new TreeMap<Integer,Integer>();
		AgentMain fileAgent = new AgentMain(true, initTree,agentLockList,removeMap, 0, 0);
		fileAgent.setNodeData1(nodedata1);
		fileAgent.run();
		
		while(fileAgent.isAlive()){}
		
		RMICommunicationInt recInt=(RMICommunicationInt) rmi.getRMIObject(nodedata1.getPrevNode(), nodedata1.getPrevNodeIP(), "RMICommunication");
		try 
		{
			recInt.rmiFileAgentExecution(fileAgent);
		} catch (RemoteException e) {e.printStackTrace();}
		
	}
}
