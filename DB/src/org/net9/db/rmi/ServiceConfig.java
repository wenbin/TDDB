package org.net9.db.rmi;

public class ServiceConfig
{
	private String siteName;
	
	private String host;
	private int port;
	private String serviceName;
	
	public ServiceConfig(String siteName, String host, int port, String serviceName)
	{
		this.siteName = siteName;
		this.host = host;
		this.port = port;
		this.serviceName = serviceName;
	}

	public String getSiteName() {
		return siteName;
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
