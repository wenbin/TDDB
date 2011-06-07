package org.net9.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.net9.db.rmi.*;
import org.net9.db.rmi.client.DBClientManager;

public class HostProxyImpl implements HostProxy {
	protected static Map<HostSession, Environment> sessionPool
		= Collections.synchronizedMap(new HashMap<HostSession, Environment>());
	protected static Object sessionPoolMutex = new Object();
	
	protected QueryProcess queryProcess = new QueryProcess();

	public void initializeConfig()
	{
		queryProcess.initialDB();
	}
	
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
		// TODO		
	}
	
	private HostService findService(HostSession session, TreeNode node) throws MalformedURLException, RemoteException, NotBoundException
	{
		Environment en = findOrCreateEnvironment(session);

		final SiteType siteType = node.getSiteType();
		final String siteName = siteType.getSiteName();
		final HashMap serviceInfo = queryProcess.getServiceInfo();
		ServiceConfig serviceConfig = (ServiceConfig)serviceInfo.get(siteName);
		// TODO:
		// DBClientManager getHostService
		HostService service = DBClientManager.getHostService(serviceConfig);
		return service;
	}
	
	public ArrayList<HashMap> runTreeNode(HostSession session, Object node)
		throws Exception
	{
		Environment en = findOrCreateEnvironment(session);
		
		final TreeNode treeNode = (TreeNode)node;
		
		ArrayList<HashMap> lans = null;
		ArrayList<HashMap> rans = null;
		final TreeNode lson = treeNode.getLson();
		final TreeNode rson = treeNode.getRson();
		
		class WorkerThread extends Thread 
		{
			public boolean isSelf; //: TODO
			public ArrayList<HashMap> ans;
			public HostService proxy;
			public HostSession session;
			public TreeNode son;
			public Exception exception;
			
			public void run()
			{
				try {
					ans = proxy.runTreeNode(session, son);
				} catch (Exception e) {
					this.exception = e;
					e.printStackTrace();
				}
			}
		}
		WorkerThread lThread = new WorkerThread();
		WorkerThread rThread = new WorkerThread();
		if (lson != null) {
			HostService leftService = findService(session, lson); //: TODO
			lThread.proxy = leftService;
			lThread.session = session;
			lThread.son = lson;
			lThread.run();
		}
		if (rson != null) {
			HostService rightService = findService(session, lson); //: TODO
			rThread.proxy = rightService;
			rThread.session = session;
			rThread.son = rson;
			rThread.run();
		}
		
		en.addThread(lThread);
		en.addThread(rThread);
		lThread.start();
		rThread.start();
		lThread.join();
		rThread.join();
		if (lThread.exception != null || rThread.exception != null)
		{
			// TODO: Error Remote
		}
		
		lans = lThread.ans;
		rans = lThread.ans;
		ArrayList<HashMap> ans = treeNode.runAns(lans, rans);
		return ans;
	}
	
	
	@Override
	public String query(HostSession session, String queryStr)
			throws Exception {
		Environment en = findOrCreateEnvironment(session);
		
		System.out.println(queryStr);
		TreeNode treeNode = queryProcess.queryParse(queryStr);
		if (treeNode == null) {
			throw new Exception("Not valid query: " + queryStr);
		}
		
		class WorkerThread extends Thread 
		{
			public ArrayList<HashMap> result = null;
			public TreeNode treeNode;
			public void run()
			{
				result = treeNode.run();
			}
		}
		
		WorkerThread thread = new WorkerThread();
		thread.treeNode = treeNode;
		en.addThread(thread);
		
		long startTime = System.currentTimeMillis();
		thread.start();
		thread.join();
		long endTime = System.currentTimeMillis();

		ArrayList<HashMap> result = thread.result;
		
		return String.format("[%d]Query: %s [Count:%d][Time:%f]", 
								session.sessionId, 
								queryStr, 
								result.size(), 
								(double)(endTime - startTime) / (double)1000);
	}
	
	public void main(String[] argv) throws Exception
	{
		HostProxyImpl impl = new HostProxyImpl();
		impl.initializeConfig();
		
		HostSession session = impl.openSession();
		QueryProcess queryProcess = impl.queryProcess;
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			String newLine = stdin.readLine();
			while (!newLine.equals("exit")) {
				
				TreeNode rootNode = queryProcess.queryParse(newLine);
				if (rootNode != null) {
					queryProcess.run(rootNode);
				}
				newLine = stdin.readLine();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
