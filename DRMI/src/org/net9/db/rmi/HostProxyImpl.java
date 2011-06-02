package org.net9.db.rmi;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HostProxyImpl implements HostProxy {
		
	protected static Map<HostSession, Environment> sessionPool
		= Collections.synchronizedMap(new HashMap<HostSession, Environment>());
	protected static Object sessionPoolMutex = new Object();
	
	public Environment findOrCreateEnvironment(HostSession session)
	{
		return findEnvironment(session, true);
	}
	
	public Environment findEnvironment(HostSession session, boolean created)
	{
		synchronized (sessionPoolMutex) {
			Environment enviroment = null;
			if (sessionPool.containsKey(session)) {
				enviroment = sessionPool.get(session);
			} else if (created) {
				enviroment = new Environment();
				sessionPool.put(session, enviroment);
			}
			return enviroment;
		}
	}
	
	public boolean containsEnvironment(HostSession session)
	{
		synchronized (sessionPoolMutex) {
			return sessionPool.containsKey(session);
		}
	}
	
	public void destroyEnvironment(HostSession session)
	{
		synchronized (sessionPoolMutex) {
			if ( sessionPool.containsKey(session) ) {
				Environment enviroment = sessionPool.get(session);
				enviroment.destroy();
				sessionPool.remove(session);
			}
		}
	}
	
	@Override
	public HostSession requestSession() throws Exception {
		HostSession session = new HostSession();
		session.sessionId = new Random().nextInt();
		session.timestamp = new Date();
		return session;
	}
	
	@Override
	public String query(HostSession session, String queryStr)
			throws Exception {
		Environment en = findOrCreateEnvironment(session);
		
		en.thread.start();
		en.thread.join();
		Thread.sleep(3000);
		destroyEnvironment(session);
		
		return String.format("[%d]Query: %s", session.sessionId, queryStr);
	}
	
}
