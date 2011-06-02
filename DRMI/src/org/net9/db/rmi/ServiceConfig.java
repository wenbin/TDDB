package org.net9.db.rmi;

public class ServiceConfig
{
	private String host;
	private int sessionId;
	private String serviceName;
	
	public ServiceConfig(String host, int sessionId, String serviceName)
	{
		this.host = host;
		this.sessionId = sessionId;
		this.serviceName = serviceName;
	}
	
	public String getLocalBindUrl()
	{
		return String.format("(host_%s)(session_%d)%s",
				host,
				sessionId,
				serviceName);
	}
	
	public String getRemoteBindUrl()
	{
		return String.format("rmi://%s/%s",
							host,
							getLocalBindUrl());
	}
	
}
