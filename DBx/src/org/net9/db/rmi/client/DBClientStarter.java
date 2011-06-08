package org.net9.db.rmi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.net9.db.QueryProcess;
import org.net9.db.rmi.HostService;
import org.net9.db.rmi.HostServiceImpl;
import org.net9.db.rmi.HostSession;
import org.net9.db.rmi.ServiceConfig;

public class DBClientStarter {
	
	public static void message(HashMap map)
	{
		System.out.println("[Client]Please type in DDB server name: (connect) <siteName>");
		System.out.println("  <siteNames>:");
		Iterator lit = map.entrySet().iterator();
		while (lit.hasNext()) {
			Map.Entry lentry = (Map.Entry)lit.next();
			String siteName = (String)lentry.getKey();
			System.out.println("    " + siteName);
		}
	}
	
	public static void testQuery(HostService service, String query) throws RemoteException
	{
		HostSession session = service.openSession();
		System.out.println("session: " + session.sessionId);
		String result = service.query(session, query);
		System.out.println(result);
		service.closeSession(session);
	}
	
	public static void main(String[] args) 
	{
		if(System.getSecurityManager() == null)
	    {
	       System.setSecurityManager(new RMISecurityManager());
	    }
		

		QueryProcess process = new QueryProcess();
		process.initialDB();
		HashMap map = process.getServiceInfo();
		message(map);
		
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			String newLine = stdin.readLine();
			try {
				String[] strs = newLine.split(" ");
				String command = strs[0];
				String siteName = strs[1];
									
				if ( map.containsKey(siteName) ) {
					ServiceConfig config = (ServiceConfig)map.get(siteName);
					if (command.equalsIgnoreCase("connect")) {
						HostService service = DBClientManager.getHostService(config);
						System.out.println("Connected to: " + siteName);
						newLine = stdin.readLine();
						while (!newLine.equals("exit")) {
							try {
								testQuery(service, newLine);
							} catch (RemoteException re) {
								System.out.println("[Query Error]:" + newLine);
								re.printStackTrace();
							}
							newLine = stdin.readLine();
						}
						return;
					} 
				} else {
					System.out.println("[Terminate]Site invalid:" + newLine);
				}
			} catch (Exception e) {
				System.out.println("[Input Error]:" + newLine);
				e.printStackTrace();
				message(map);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
