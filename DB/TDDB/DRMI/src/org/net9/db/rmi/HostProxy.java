package org.net9.db.rmi;

public interface HostProxy {
	public HostSession openSession() throws Exception;
	public void closeSession(HostSession session) throws Exception;
	
	public String query(HostSession sessionId, String queryStr) throws Exception;
}
