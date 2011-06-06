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
		HostSession session = service.requestSession();
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
			new ServiceConfig("localhost", 0, HostService.SERVICE_NAME),
			new ServiceConfig("localhost", 1, HostService.SERVICE_NAME),
			new ServiceConfig("localhost", 2, HostService.SERVICE_NAME),
			new ServiceConfig("localhost", 3, HostService.SERVICE_NAME)
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
