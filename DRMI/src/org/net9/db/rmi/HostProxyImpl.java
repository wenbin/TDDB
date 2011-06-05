package org.net9.db.rmi;

import java.util.Calendar;
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
			if (sessionPool.containsKey(session)) {
				Environment enviroment = sessionPool.get(session);
				enviroment.destroy();
				sessionPool.remove(session);
			}
		}
	}
	
	/////////////////////////////////////////////////
	// interfaces:
	@Override
	public HostSession openSession() throws Exception {
		HostSession session = new HostSession();
		session.sessionId = new Random().nextInt();
		session.startTime = new Date();
		// TODO: set later -- TT
		session.expiredTime = null; 
		session.owner = "";
		return session;
	}
	
	@Override
	public void closeSession(HostSession session) throws Exception {
		destroyEnvironment(session);
	}
	
	@Override
	public String query(HostSession session, String queryStr)
			throws Exception {
		Environment en = findOrCreateEnvironment(session);
		
		Thread thread = new Thread() {
			public void run()
			{
				// TODO: set query processor. --TT
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				/**
				 * 
				 * Process Query Here
				 *  
				 **/
			}
		};
		
		en.addThread(thread);
		thread.start();
		thread.join();
		
		return String.format("[%d]Query: %s", session.sessionId, queryStr);
	}	
}
