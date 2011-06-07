package org.net9.db.rmi.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
			System.out.println(config.getRemoteBindUrl() + ": is started!");
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
	}
	
	public static void stopService(ServiceConfig config) throws NotBoundException
	{
		try {
			Registry r = LocateRegistry.getRegistry();
			HostService service = new HostServiceImpl();
			r.unbind(config.getLocalBindUrl());
			System.out.println(config.getRemoteBindUrl() + ": is stopped!");
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
	}
	public static void message(HashMap map)
	{
		System.out.println("[Server]Please type in DDB server command: (start|stop|exit) <siteName>");
		System.out.println("  <siteNames>:");
		Iterator lit = map.entrySet().iterator();
		while (lit.hasNext()) {
			Map.Entry lentry = (Map.Entry)lit.next();
			String siteName = (String)lentry.getKey();
			System.out.println("    " + siteName);
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
		message(map);
		
		try {
			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
			
			while (true) {
				String newLine = stdin.readLine();
				try {
					if (newLine.equalsIgnoreCase("exit")) { 
						System.out.println("Exit server");
						return;
					}

					String[] strs = newLine.split(" ");
					String command = strs[0];
					String siteName = strs[1];
										
					if ( map.containsKey(siteName) ) {
						ServiceConfig config = (ServiceConfig)map.get(siteName);
						if (command.equalsIgnoreCase("start")) {
							startService(config);
							continue;
						} else if (command.equalsIgnoreCase("stop")) {
							stopService(config);
							continue;
						}
					}
					message(map);
				} catch (Exception e) {
					System.out.println("[Input Error]:" + newLine);
					e.printStackTrace();
					message(map);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}