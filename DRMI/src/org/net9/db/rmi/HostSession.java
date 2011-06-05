package org.net9.db.rmi;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.Date;

public class HostSession implements Serializable
{
	public int sessionId;
	public int hostName;
	public Date startTime;
	public Date expiredTime;
	public String owner;
	
	public boolean equals(HostSession h)
	{
		if (sessionId != h.sessionId
			|| hostName != h.hostName 
			|| startTime != h.startTime 
			|| expiredTime != h.expiredTime
			|| owner != h.owner) {
			return false;
		}
		return false;
	}
}
