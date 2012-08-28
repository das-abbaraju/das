package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "DBBeanTest-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager")
public class DBBeanTest {

	private static final int THREAD_COUNT = 500;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testThreadsafe() throws Exception {
		List<Thread> threads = createThreads(THREAD_COUNT);
		
		// The reason this needs to be done, is that the DBBean's staticDataSource
		// field gets set by afterPropertiesSet() method overridden in its implementation
		// of the InitializingBean.
		DBBean.staticDataSource = null;
		
		for (Thread thread : threads) {
			thread.start();
		}
		
		boolean allThreadsDone = false;
		while (!allThreadsDone) {
			int total = 0;
			for (Thread thread : threads) {
				if (Thread.State.TERMINATED == thread.getState()) {
					total++;
				}
			}
			
			if (total == THREAD_COUNT) {
				allThreadsDone = true;
			}
		}
		
		assertEquals(1, DBBean.instantiationCount.intValue());
	}

	private List<Thread> createThreads(int threadCount) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int count = 0; count < threadCount; count++) {
			threads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						DBBean.getDBConnection().close();
					} catch (Exception e) {
						System.out.println("Thread exception during JUnit test!");
					}
				}
			}));
		}

		return threads;
	}

}
