package org.net9.db.rmi;

import java.io.IOException;

import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;

public class Environment {
	public Thread thread = new Thread();
	public Thread controlThread = new Thread();
	public Thread dataThread = new Thread();
	
	public synchronized void destroy() {
		
	}
}
