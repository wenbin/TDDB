package org.net9.db.rmi;

import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HostServiceImpl extends UnicastRemoteObject implements HostService 
{	
	int requestCount = 0;
	String siteName; 
	HostProxy serviceProxy;
	static Object proxyLock = new Object();
	
	public HostServiceImpl() throws RemoteException {
		super();
	}
	
	protected HostProxy getProxy()
	{
		synchronized (proxyLock) {
			if (serviceProxy == null) {
				serviceProxy = HostProxyFactory.createInstance(siteName);
			}
			return serviceProxy;
		}
	}
	
	@Override
	public String echo(String s) throws RemoteException {
		requestCount++;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RemoteException("[RemoteException]: in echo", e);
		}
		return "[Echo]" + s;
	}

	@Override
	public HostSession openSession() throws RemoteException {
		requestCount++;
		HostProxy proxy = getProxy();
		try {
			HostSession ret = proxy.openSession();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("[RemoteException]: in requestSession", e);
		}
	}

	@Override
	public String query(HostSession session, String queryStr) throws RemoteException {
		requestCount++;
		HostProxy proxy = getProxy();
		try {
			String ret = proxy.query(session, queryStr);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("[RemoteException]: in query", e);
		}
	}

	@Override
	public void closeSession(HostSession session) throws RemoteException {
		requestCount++;
		HostProxy proxy = getProxy();
		try {
			proxy.closeSession(session);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("[RemoteException]: in closeSession", e);
		}
	}

	@Override
	public ArrayList<HashMap> runTreeNode(HostSession session, Object node)
			throws RemoteException {
		requestCount++;
		ArrayList<HashMap> result;
		HostProxy proxy = getProxy();
		try {
			result = proxy.runTreeNode(session, node);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("[RemoteException]: in query", e);
		}
		
		return result;
	}


	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

}
