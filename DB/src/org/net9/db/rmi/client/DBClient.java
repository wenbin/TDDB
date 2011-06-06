package org.net9.db.rmi.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.dgc.*;

import org.net9.db.TreeNode;
import org.net9.db.rmi.HostService;
import org.net9.db.rmi.ServiceConfig;
import org.net9.db.rmi.HostSession;



public class DBClient {
	
	public static void testEcho(HostService service) throws RemoteException
	{
		String echoStr = service.echo("hello");
		System.out.println("Echoed: " + echoStr);
	}
	
	public static void testQuery(HostService service, String query) throws RemoteException
	{
		System.out.println("request session");
		HostSession session = service.openSession();
		System.out.println("session: " + session.sessionId);
		
		String result = service.query(session, query);
		System.out.println(result);
	}
	
	
	
	public static void main(String[] args)
	{
		if(System.getSecurityManager() == null)
	    {
	       System.setSecurityManager(new RMISecurityManager());
	    }
		
		ServiceConfig[] configs = new ServiceConfig[] { 
			new ServiceConfig("site1", "127.0.0.1", 1, HostService.SERVICE_NAME),
			//new ServiceConfig("site2", "localhost", 2, HostService.SERVICE_NAME),
			//new ServiceConfig("site3", "localhost", 3, HostService.SERVICE_NAME),
			//new ServiceConfig("site4", "localhost", 4, HostService.SERVICE_NAME)
		};
		ServiceConfig config = configs[0];
		
		try {
			HostService service = DBClientManager.getHostService(config);
			testEcho(service);
			testQuery(service, "select * from Customer");
			
			try {
				BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
				String newLine = stdin.readLine();
				while (!newLine.equals("exit")) {
					newLine = stdin.readLine();
					testQuery(service, newLine);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
