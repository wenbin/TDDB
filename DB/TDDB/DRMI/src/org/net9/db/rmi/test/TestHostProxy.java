package org.net9.db.rmi.test;

import junit.framework.*;

import org.net9.db.rmi.*;

public class TestHostProxy extends TestCase {
	private HostProxy proxy;
	
	public TestHostProxy(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception{
		super.setUp();
		
		proxy = HostProxyFactory.getInstance();
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetInstance() throws Exception {
		HostProxy newProxy = HostProxyFactory.getInstance();
		assertNotNull(newProxy);

		// should be singleton
		assertSame(newProxy, proxy);
	}
	
	public void testCast() throws Exception {
		assertTrue(proxy instanceof HostProxyImpl);
	}
	
	public void testSession() throws Exception
	{
		final HostProxyImpl impl = (HostProxyImpl)proxy;
		final int threadNum = 500;
		
		class TestThread extends Thread {
			public Exception exception; 
			
			public void run()
			{
				long id = Thread.currentThread().getId();
				System.out.println("id:" + id + " is running");
				try {
					HostSession session = impl.openSession();
					assertFalse(impl.containsEnvironment(session));
					
					impl.findEnvironment(session, false);
					assertFalse(impl.containsEnvironment(session));
					
					impl.findEnvironment(session, true);
					assertTrue(impl.containsEnvironment(session));
					
					impl.destroyEnvironment(session);
					assertFalse(impl.containsEnvironment(session));
					
					impl.findOrCreateEnvironment(session);
					assertTrue(impl.containsEnvironment(session));
					
					impl.destroyEnvironment(session);
					assertFalse(impl.containsEnvironment(session));
					
					String queryStr = "Select * from demo";
					String str = impl.query(session, queryStr);
					assertEquals(str, String.format("[%d]Query: %s", session.sessionId, queryStr));
					
					impl.closeSession(session);
				} catch (Exception e) {
					exception = e;
					e.printStackTrace();
				}
				System.out.println("id:" + id + " is stopped");
			}
		}
		
		TestThread[] threads = new TestThread[threadNum];
		for (int i = 0; i < threadNum; ++i)
		{
			threads[i] = new TestThread();
			threads[i].start();
		}
		
		for (int i = 0; i < threadNum; ++i)
		{
			threads[i].join();
			assertNull(threads[i].exception);
		}
	}
}
