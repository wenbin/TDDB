// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package org.net9.db.rmi;

public final class HostServiceImpl_Stub
    extends java.rmi.server.RemoteStub
    implements org.net9.db.rmi.HostService, java.rmi.Remote
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_echo_0;
    private static java.lang.reflect.Method $method_query_1;
    private static java.lang.reflect.Method $method_requestSession_2;
    
    static {
	try {
	    $method_echo_0 = org.net9.db.rmi.HostService.class.getMethod("echo", new java.lang.Class[] {java.lang.String.class});
	    $method_query_1 = org.net9.db.rmi.HostService.class.getMethod("query", new java.lang.Class[] {org.net9.db.rmi.HostSession.class, java.lang.String.class});
	    $method_requestSession_2 = org.net9.db.rmi.HostService.class.getMethod("requestSession", new java.lang.Class[] {});
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
    
    // implementation of echo(String)
    public java.lang.String echo(java.lang.String $param_String_1)
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_echo_0, new java.lang.Object[] {$param_String_1}, 5525131960618330777L);
	    return ((java.lang.String) $result);
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
	    Object $result = ref.invoke(this, $method_query_1, new java.lang.Object[] {$param_HostSession_1, $param_String_2}, -6719644661372169550L);
	    return ((java.lang.String) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of requestSession()
    public org.net9.db.rmi.HostSession requestSession()
	throws java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_requestSession_2, null, -4374653250848447244L);
	    return ((org.net9.db.rmi.HostSession) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}