/**
 * 
 */
package org.net9.db.rmi;


import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tt
 */
public interface HostService extends Remote {
	public static String SERVICE_NAME = "HostService";
	
	public String echo(String s) throws RemoteException;
	
	public HostSession openSession() throws RemoteException;
	public void closeSession(HostSession session) throws RemoteException;
	
	public ArrayList<HashMap> runTreeNode(HostSession session, Object node) throws RemoteException;
	public String query(HostSession session, String query) throws RemoteException;
}
