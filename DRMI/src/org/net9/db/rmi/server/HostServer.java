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

public class HostServer
{
	public static void main(String[] args) 
	{
		if(System.getSecurityManager() == null)
	    {
	       System.setSecurityManager(new RMISecurityManager());
	    }
		
		try {
			Registry r = LocateRegistry.getRegistry();
			HostService service = new HostServiceImpl();
			ServiceConfig config = new ServiceConfig("localhost", 0, HostService.SERVICE_NAME);
			r.rebind(config.getLocalBindUrl(), service);
			System.out.println(HostService.SERVICE_NAME + ": is Ready!");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}