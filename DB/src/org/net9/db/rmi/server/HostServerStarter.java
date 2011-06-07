package org.net9.db.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.spi.RegisterableService;

import org.net9.db.QueryProcess;
import org.net9.db.rmi.HostService;
import org.net9.db.rmi.HostServiceImpl;
import org.net9.db.rmi.ServiceConfig;

public class HostServerStarter
{
	public static void startService(ServiceConfig config)
	{
		try {
			Registry r = LocateRegistry.getRegistry();
			HostService service = new HostServiceImpl();
			r.rebind(config.getLocalBindUrl(), service);
			System.out.println(config.getRemoteBindUrl() + ": is Ready!");
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
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
		Iterator lit = map.entrySet().iterator();
		while (lit.hasNext()) {
			Map.Entry lentry = (Map.Entry)lit.next();
			String siteName = (String)lentry.getKey();
			ServiceConfig config = (ServiceConfig)lentry.getValue();
			startService(config);
		}
	}
}