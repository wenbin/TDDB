package org.net9.db.rmi;

public class ServiceConfig
{
	private String host;
	private int port;
	private String serviceName;
	
	public ServiceConfig(String host, int port, String serviceName)
	{
		this.host = host;
		this.port = port;
		this.serviceName = serviceName;
	}
	
	public String getLocalBindUrl()
	{
		return String.format("(host_%s)(port_%d)%s",
				host,
				port,
				serviceName);
	}
	
	public String getRemoteBindUrl()
	{
		return String.format("rmi://%s/%s",
							host,
							getLocalBindUrl());
	}
	
}
