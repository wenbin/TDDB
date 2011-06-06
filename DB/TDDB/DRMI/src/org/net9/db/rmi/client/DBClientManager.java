package org.net9.db.rmi.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.net9.db.rmi.HostService;
import org.net9.db.rmi.ServiceConfig;
import org.net9.db.rmi.HostSession;

public class DBClientManager {
	protected static Map<ServiceConfig, Remote> dict = 
		Collections.synchronizedMap(new HashMap<ServiceConfig, Remote>());
	
	// public static HostService FindBy() {}
	
	public synchronized static HostService getHostService(ServiceConfig config) throws MalformedURLException, RemoteException, NotBoundException
	{
		Remote r = null;
		if (dict.containsKey(config)) {
			r = dict.get(config);
		} else {
			r = Naming.lookup(config.getRemoteBindUrl());
			dict.put(config, r);
		}
		HostService service = (HostService)r;
		return service;
	}
}
