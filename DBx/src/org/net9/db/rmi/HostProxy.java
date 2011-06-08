package org.net9.db.rmi;

import java.util.ArrayList;
import java.util.HashMap;

public interface HostProxy {
	public HostSession openSession() throws Exception;
	public void closeSession(HostSession session) throws Exception;
	
	public String query(HostSession sessionId, String queryStr) throws Exception;
	public ArrayList<HashMap> runTreeNode(HostSession session, Object node) throws Exception;
}
