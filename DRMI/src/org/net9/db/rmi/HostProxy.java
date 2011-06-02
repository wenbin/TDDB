package org.net9.db.rmi;

public interface HostProxy {
	public HostSession requestSession() throws Exception; 
	public String query(HostSession sessionId, String queryStr) throws Exception;
}
