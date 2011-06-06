package org.net9.db.rmi.client;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.dgc.*;

import org.net9.db.rmi.HostService;
import org.net9.db.rmi.ServiceConfig;
import org.net9.db.rmi.HostSession;

import com.sun.security.auth.login.ConfigFile;


public class DBClient {
	
	public static void testEcho(HostService service) throws RemoteException
	{
		String echoStr = service.echo("hello");
		System.out.println("Echoed: " + echoStr);
	}
	
	public static void testQuery(HostService service) throws RemoteException
	{
		System.out.println("request session");
		HostSession session = service.openSession();
		System.out.println("session: " + session.sessionId);
		
		String result = service.query(session, "Select * from demo;");
		System.out.println(result);
	}
	
	public static void main(String[] args)
	{
		if(System.getSecurityManager() == null)
	    {
	       System.setSecurityManager(new RMISecurityManager());
	    }
		
		ServiceConfig[] configs = new ServiceConfig[] { 
			new ServiceConfig("site1", "localhost", 0, HostService.SERVICE_NAME),
			new ServiceConfig("site2", "localhost", 1, HostService.SERVICE_NAME),
			new ServiceConfig("site3", "localhost", 2, HostService.SERVICE_NAME),
			new ServiceConfig("site4", "localhost", 3, HostService.SERVICE_NAME)
		};
		ServiceConfig config = configs[0];
		
		try {
			HostService service = DBClientManager.getHostService(config);
			testEcho(service);
			testQuery(service);
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
