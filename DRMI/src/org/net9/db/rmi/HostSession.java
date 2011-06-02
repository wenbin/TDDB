package org.net9.db.rmi;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

public class HostSession implements Serializable
{
	public int sessionId;
	public int hostName;
	public Date timestamp;
	
	public boolean equals(HostSession h)
	{
		if (sessionId != h.sessionId
			|| hostName != h.hostName 
			|| timestamp != h.timestamp ) {
			return false;
		}
		return false;
	}
}
