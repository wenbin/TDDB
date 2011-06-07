package org.net9.db.rmi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
