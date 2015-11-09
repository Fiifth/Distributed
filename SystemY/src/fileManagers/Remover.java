package fileManagers;

import nodeP.NodeData;

public class Remover extends Thread 
{
	NodeData nodedata1;
	Remover(NodeData nodedata1)
	{
		this.nodedata1=nodedata1;
	}
	public void run()
	{
		//queue removeQueue afgaan
		//een file(object FileData wordt in removeQueue gezet als de lijst van zijn local owner
		//leeg is
		//in dit geval gaat het gewoon op 0 komen te staan als er een locale owner weg gaat
		//aangezien we nog geen lijst gaan voorzien omdat er niet meer dan 1 persoon
		//de file lokaal kan hebben staan (via rmi zegt owner tege replication dat hij de file
		//weg doet(bij shutdown)
		
		//je kan ook in de lijst komen te staan wanneer een replicate file naar een andere node
		//verzonden wordt
		
	}
	/*TODO OF ALL TODO's


when someone downloads a file he gets added to the list on the replicate node
when a local owner deletes the file he gets removed from the list
the fileDataRemoveLocalOwner function will put the file in a queue with a 
delete file field active

fileData object name has to be the same as file name 

	 */
}
