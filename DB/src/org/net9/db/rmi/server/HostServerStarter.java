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

import javax.imageio.spi.RegisterableService;

import org.net9.db.rmi.HostService;
import org.net9.db.rmi.HostServiceImpl;
import org.net9.db.rmi.ServiceConfig;

import com.sun.org.apache.bcel.internal.Constants;

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
		
		ServiceConfig[] configs = new ServiceConfig[]
        {
			new ServiceConfig("site1", "localhost", 0, HostService.SERVICE_NAME),
			new ServiceConfig("site2", "localhost", 1, HostService.SERVICE_NAME),
			new ServiceConfig("site3", "localhost", 2, HostService.SERVICE_NAME),
			new ServiceConfig("site4", "localhost", 3, HostService.SERVICE_NAME)
        };
		
		for (ServiceConfig c : configs) {
			startService(c);
		}
	}
}