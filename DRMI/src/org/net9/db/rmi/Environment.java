package org.net9.db.rmi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;

public class Environment {
	protected List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());

	public synchronized void destroy() {
		System.out.println("destory # threads: " + threads.size());
		for (Thread t : threads) {
			t.stop();
		}
	}
	
	public synchronized void addThread(Thread thread) {
		threads.add(thread);
	}
}
