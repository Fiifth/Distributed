package fileManagers;

public class TODO extends Thread 
{
	/*TODO OF ALL TODO's
	 * getReplicateDataSet
-->get better name for this variable

FileReceiverT
-->Remove RMI functions

FileReceiverT and FileExchangeT and fileRemover toghetter
add field to filedata1 that determins if file has to be received/send or deleted

ownershipT has to work on its own (blocking variable: atomic boolean/queue) that gets activated when a new node sends hes name or when the node running this thread wants to quit

new nodemanager thread with RMI communication
*Receive file
*delete local owner
*add local owner

when someone downloads a file he gets added to the list on the replicate node
when a local owner deletes the file he gets removed from the list
the fileDataRemoveLocalOwner function will put the file in a queue with a 
delete file field active

FileDetection -->refresh node with new files already
file data -->replication is on local node

fileData object name has to be the same as file name 

	 */
}
