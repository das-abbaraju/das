package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DBBeanTest {

	private static final int THREAD_COUNT = 500;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testThreadsafe() throws Exception {
		List<Thread> threads = createThreads(THREAD_COUNT);
		for (Thread thread : threads) {
			thread.start();
		}
		
		assertTrue(DBBean.serviceLocatorCount.intValue() <= 1);
	}

	private List<Thread> createThreads(int threadCount) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int count = 0; count < threadCount; count++) {
			threads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						DBBean.getJdbcPics();
					} catch (Exception e) {
						// do nothing
					}
				}
			}));
		}

		return threads;
	}

}
