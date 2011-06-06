package org.net9.db.rmi;

import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HostProxyFactory  {
	private HostProxyFactory() {}
	
	public static HostProxy getInstance()
	{
		return HostProxyHolder.INSTANCE;
	}
	
	static class HostProxyHolder {
		static HostProxyImpl INSTANCE = new HostProxyImpl();
	}
}
