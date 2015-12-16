package nameServer;

import java.io.IOException;

public class NameServerMain 
{
	public static void main(String[] args) throws IOException
	{
		StartNameServer nameserver= new StartNameServer();
		nameserver.startNameServer();
	}
}
