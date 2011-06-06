/**
 * 
 */
package org.net9.db.rmi;

import java.rmi.*;

/**
 * @author tt
 */
public interface HostService extends Remote {
	public static String SERVICE_NAME = "HostService";
	
	public String echo(String s) throws RemoteException;
	
	public HostSession requestSession() throws RemoteException;
	public String query(HostSession session, String query) throws RemoteException;
}
