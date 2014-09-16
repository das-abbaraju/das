package com.picsauditing.employeeguard.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UUIDUtilTest {

 	private UUIDUtil uuidUtil;
	private ExecPool execPool;
	private AtomicInteger counter= new AtomicInteger(1);
	private static final Integer noOfThreads = 64;
	private static final Integer guidsToGeneratePerThread = 10000;
	private static final Integer totalNoOfGuids = noOfThreads*guidsToGeneratePerThread;
	private ConcurrentSkipListSet allGuids = new ConcurrentSkipListSet();

	@Before
	public void setUp() throws Exception {
		execPool = ExecPool.getInstance();
		uuidUtil = new UUIDUtil();
	}

	@After
	public void tearDown() throws Exception {
		execPool.shutdown();
	}

	@Ignore("Very long test")
	@Test
	public void testNewGuid() throws Exception {

		ExecutorService taskExecutor = execPool.getExecutor();

		for(int i=0; i<noOfThreads; i++) {
			taskExecutor.execute(new GenerateGuids());
		}
		taskExecutor.shutdown();
		taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);


		// if any were duplicates, the unique set would disallow it and it would be less
		// than the number we tried to create
		int guidsGenerated= allGuids.size();
		System.out.printf("Guids Generated=[%d] and expected size =[%d]%n",guidsGenerated, totalNoOfGuids);
		assertThat(guidsGenerated, is(equalTo(totalNoOfGuids)));

	}

	private class GenerateGuids implements Runnable {
		private AtomicInteger myThreadNo= new AtomicInteger(counter.getAndIncrement());


		@Override
		public void run() {
			try {
				//System.out.println("Executing Thread " + myThreadNo.get());
				 testGuid_ReasonablyUnique_newGuid();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return;
		}

		private void testGuid_ReasonablyUnique_newGuid() throws Exception {
			int numToCreate = guidsToGeneratePerThread;
			for(int i = 0; i < numToCreate; i++) {
				//System.out.printf("Guiid count=[%d] Thread=[%d] %n", i, myThreadNo.get());
				String guid = Whitebox.invokeMethod(uuidUtil, "newGuid");
				allGuids.add(guid);
			}

			return ;
		}

	}
}
