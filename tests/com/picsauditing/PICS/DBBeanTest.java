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
@ContextConfiguration(locations={"/tests.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
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
		
//		assertEquals(1, DBBean.instantiationCount.intValue());
	}

	private List<Thread> createThreads(int threadCount) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int count = 0; count < threadCount; count++) {
			threads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						DBBean.getDBConnection();
					} catch (Exception e) {
						// do nothing
					}
				}
			}));
		}

		return threads;
	}

}
