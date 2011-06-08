package org.net9.db.rmi;

import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HostProxyFactory  {
	private HostProxyFactory() {}
	
	public static HostProxy createInstance(String siteName)
	{
		HostProxyImpl instance = new HostProxyImpl();
		instance.initializeConfig();
		instance.setSiteName(siteName);
		return instance;
	}
}
