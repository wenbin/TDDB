// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package org.net9.db.rmi;

public final class HostServiceImpl_Stub
    extends java.rmi.server.RemoteStub
    implements org.net9.db.rmi.HostService, java.rmi.Remote
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_closeSession_0;
    private static java.lang.reflect.Method $method_echo_1;
    private static java.lang.reflect.Method $method_openSession_2;
    private static java.lang.reflect.Method $method_query_3;
    private static java.lang.reflect.Method $method_runTreeNode_4;
    
    static {
	try {
	    $method_closeSession_0 = org.net9.db.rmi.HostService.class.getMethod("closeSession", new java.lang.Class[] {org.net9.db.rmi.HostSession.class});
	    $method_echo_1 = org.net9.db.rmi.HostService.class.getMethod("echo", new java.lang.Class[] {java.lang.String.class});
	    $method_openSession_2 = org.net9.db.rmi.HostService.class.getMethod("openSession", new java.lang.Class[] {});
	    $method_query_3 = org.net9.db.rmi.HostService.class.getMethod("query", new java.lang.Class[] {org.net9.db.rmi.HostSession.class, java.lang.String.class});
	    $method_runTreeNode_4 = org.net9.db.rmi.HostService.class.getMethod("runTreeNode", new java.lang.Class[] {org.net9.db.rmi.HostSession.class, java.lang.Object.class});
	} catch (java.lang.NoSuchMethodException e) {
	    throw new java.lang.NoSuchMethodError(
		"stub class initialization failed");
	}
    }
    
    // constructors
    public HostServiceImpl_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    
    // methods from remote interfaces
    
    // implementation of closeSession(HostSession)
    public void closeSession(org.net9.db.rmi.HostSession $param_HostSession_1)
	throws java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_closeSession_0, new java.lang.Object[] {$param_HostSession_1}, 3268151010144058196L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of echo(String)
    public java.lang.String echo(java.lang.String $param_String_1)
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_echo_1, new java.lang.Object[] {$param_String_1}, 5525131960618330777L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of openSession()
    public org.net9.db.rmi.HostSession openSession()
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_openSession_2, null, -1942432451214292722L);
	    return ((org.net9.db.rmi.HostSession) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of query(HostSession, String)
    public java.lang.String query(org.net9.db.rmi.HostSession $param_HostSession_1, java.lang.String $param_String_2)
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_query_3, new java.lang.Object[] {$param_HostSession_1, $param_String_2}, -6719644661372169550L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of runTreeNode(HostSession, Object)
    public java.util.ArrayList runTreeNode(org.net9.db.rmi.HostSession $param_HostSession_1, java.lang.Object $param_Object_2)
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_runTreeNode_4, new java.lang.Object[] {$param_HostSession_1, $param_Object_2}, 2447545260730178001L);
	    return ((java.util.ArrayList) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}