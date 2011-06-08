package org.net9.db.rmi;

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
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.net9.db.QueryProcess;
import org.net9.db.SiteType;
import org.net9.db.TreeNode;
import org.net9.db.rmi.*;
import org.net9.db.rmi.client.DBClientManager;

public class HostProxyImpl implements HostProxy {
	protected static Map<HostSession, Environment> sessionPool
		= Collections.synchronizedMap(new HashMap<HostSession, Environment>());
	protected static Object sessionPoolMutex = new Object();
	
	protected QueryProcess queryProcess = new QueryProcess();

	protected String siteName;
	
	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}
	public String getSiteName() 
	{
		return siteName;
	}
	
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
		System.err.println(serviceConfig.getRemoteBindUrl() + " : ");
		System.err.println(node.printTree(""));
		
		HostService service = DBClientManager.getHostService(serviceConfig);
		return service;
	}
	private boolean isRunSelfSite(HostSession session, TreeNode node) throws MalformedURLException, RemoteException, NotBoundException
	{
		Environment en = findOrCreateEnvironment(session);

		final SiteType siteType = node.getSiteType();
		final String siteName = siteType.getSiteName();
		return siteName.equals(this.getSiteName());
	}
	
	class WorkerThread extends Thread 
	{
		public ArrayList<HashMap> ans;
		public HostSession session;
		public TreeNode node;
		public Exception exception;
		
		public void run()
		{
			try {
				if (isRunSelfSite(session, node)) {
					ans = runTreeNode(session, node);
				} else {
					HostService proxy = findService(session, node);
					ans = proxy.runTreeNode(session, node);
				}
			} catch (Exception e) {
				this.exception = e;
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<HashMap> runTreeNode(HostSession session, Object node)
		throws Exception
	{
		Environment en = findOrCreateEnvironment(session);
		
		System.out.println("RunTreeNode: On " + siteName);
		final TreeNode treeNode = (TreeNode)node;
		
		ArrayList<HashMap> lans = null;
		ArrayList<HashMap> rans = null;
		final TreeNode lson = treeNode.getLson();
		final TreeNode rson = treeNode.getRson();
		
		WorkerThread lThread = new WorkerThread();
		WorkerThread rThread = new WorkerThread();
		if (lson != null) {
			lThread.session = session;
			lThread.node = lson;
			en.addThread(lThread);
			lThread.start();
		}
		if (rson != null) {
			rThread.session = session;
			rThread.node = rson;
			en.addThread(rThread);
			rThread.start();
		}
		
		lThread.join();
		rThread.join();
		
		if (lThread.exception != null || rThread.exception != null)
		{
			System.err.println("lThread.exception:" + lThread.exception);
			System.err.println("rThread.exception:" + rThread.exception);
		}
		
		lans = lThread.ans;
		rans = rThread.ans;
		
		System.err.println(treeNode.printTree(""));
		System.err.println("Left: " + ((lans != null) ? lans.size() : "null"));
		System.err.println("Right: " + ((rans != null) ? rans.size() : "null") );
		
		SiteType localSite = queryProcess.getSiteType(this.getSiteName());
		treeNode.setLocalSite(localSite);
		ArrayList<HashMap> ans = treeNode.runAns(lans, rans);
		return ans;
	}
	
	@Override
	public String query(HostSession session, String queryStr)
			throws Exception {
		Environment en = findOrCreateEnvironment(session);
		
		System.err.println(queryStr);
		
		TreeNode treeNode = queryProcess.queryParse(queryStr);
		if (treeNode == null) {
			throw new Exception("Not valid query: " + queryStr);
		}
		
		WorkerThread thread = new WorkerThread();
		thread.session = session;
		thread.node = treeNode;
		
		en.addThread(thread);
		
		long startTime = System.currentTimeMillis();
		thread.start();
		thread.join();
		long endTime = System.currentTimeMillis();

		if (thread.exception != null)
		{
			System.err.println("thread.exception:" + thread.exception);
		}
		
		ArrayList<HashMap> ans = thread.ans;
		String treeStr = treeNode.printTree("");
		//dumpAns(ans);
		
		return String.format("[%d]Query: %s [Count:%d][Time:%f]", 
								session.sessionId, 
								queryStr, 
								ans.size(), 
								(double)(endTime - startTime) / (double)1000)
								+ "\n"
								+ treeStr;
	}
	
	private void dumpAns(ArrayList<HashMap> ans)
	{
		for (int i=0; i<ans.size(); i++) {
			HashMap item = (HashMap)ans.get(i);
			Iterator it = item.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String key = (String)entry.getKey();
				String value = (String)entry.getValue();
				System.err.print(key + ":" + value + "       ");
			}
			System.err.println();
		}
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
