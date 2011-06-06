

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
	// <siteName, ServiceConfig>
	//protected HashMap<String, ServiceConfig> services = new HashMap<String, ServiceConfig>();
	// <siteName, SiteType>
	//protected HashMap<String, SiteType> sites = new HashMap<String, SiteType>();
	
	public void initializeConfig()
	{
		//HashMap<String, ServiceConfig> services, 
		//HashMap<String, SiteType> sites
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
	}
	
	private HostProxyImpl findService(HostSession session, TreeNode node)
	{
		Environment en = findOrCreateEnvironment(session);

		final SiteType siteType = node.getSiteType();
		final String siteName = siteType.getSiteName();
		final HashMap serviceInfo = queryProcess.getServiceInfo();
		ServiceConfig serviceConfig = (ServiceConfig)serviceInfo.get(siteName);
		// TODO:
		// DBClientManager getHostService
		return this;
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
		if (lson != null) {
			HostProxyImpl leftService = findService(session, lson); //: TODO
			lans = leftService.runTreeNode(session, lson);
		}
		if (rson != null) {
			HostProxyImpl rightService = findService(session, rson); //: TODO
			rans = rightService.runTreeNode(session, rson);
		}
		ArrayList<HashMap> ans = treeNode.runAns(lans, rans);
		
		return ans;
	}
	
	
	@Override
	public String query(HostSession session, String queryStr)
			throws Exception {
		Environment en = findOrCreateEnvironment(session);
		
		final TreeNode treeNode = queryProcess.queryParse(queryStr);
		if (treeNode == null) {
			throw new Exception("Not valid query: " + queryStr);
		}
		
		
		class WorkerThread extends Thread 
		{
			public ArrayList<HashMap> result = null;
			public void run()
			{
				result = treeNode.run();
			}
		}
		
		WorkerThread thread = new WorkerThread();
		en.addThread(thread);
		thread.start();
		thread.join();
		
		ArrayList<HashMap> result = thread.result;
		
		return String.format("[%d]Query: %s", session.sessionId, queryStr);
	}
	
	public void main(String[] argv) throws Exception
	{
		HostProxyImpl impl = new HostProxyImpl();
		HostSession session = impl.openSession();
		QueryProcess queryProcess = impl.queryProcess;
		queryProcess.initialDB();
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
