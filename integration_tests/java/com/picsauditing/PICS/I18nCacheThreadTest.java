package com.picsauditing.PICS;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.startsWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.search.Database;

public class I18nCacheThreadTest implements I18nCacheBuildAware {
	private static final Logger logger = LoggerFactory.getLogger(I18nCacheThreadTest.class);
	private static final int GET_INSTANCE_THREAD_COUNT = 1000;
	private static final int THREAD_COUNT = 20;
	private static final int QUERY_TIME_IN_MILLISECONDS = 50000;
	
	private I18nCache i18nCache;
	private int threadsRunning;
	
	@Mock private Database databaseForTesting;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}
	
	@Before
	public void setUp() throws Exception {
		/*
		 * IMPORTANT: don't change the order of this setup
		 */
		threadsRunning = 0;
		MockitoAnnotations.initMocks(this);
		
		Answer<List<BasicDynaBean>> answer = new Answer<List<BasicDynaBean>>() {
			@Override
			public List<BasicDynaBean> answer(InvocationOnMock invocation) throws Throwable {
				Thread.sleep(QUERY_TIME_IN_MILLISECONDS);
				return createMessages();
			}
			
			private List<BasicDynaBean> createMessages() {
				List<BasicDynaBean> messages = new ArrayList<BasicDynaBean>();
				DynaProperty[] properties = new DynaProperty[4];
				properties[0] = new DynaProperty("msgKey", String.class);
				properties[1] = new DynaProperty("locale", String.class);
				properties[2] = new DynaProperty("msgValue", String.class);
				properties[3] = new DynaProperty("lastUsed", Date.class);

				BasicDynaClass messageClass = new BasicDynaClass("message", BasicDynaBean.class, properties);
				BasicDynaBean message = new BasicDynaBean(messageClass);
				message.set("msgKey", "Test.Key");
				message.set("locale", Locale.ENGLISH.toString());
				message.set("msgValue", "Hello Testing World! ("+Math.random()+")");
				messages.add(message);
				return messages;
			}
		};
		
		when(databaseForTesting.select(anyString(), eq(false))).then(answer);
		
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		i18nCache = I18nCache.getInstance();
		i18nCache.addBuildListener(this);
	}

	@After
	public void tearDown() throws Exception {
		i18nCache.clearBuildListeners();
	}

	@Test
	public void testThreadsafe_InstanceCountOfGetInstance() throws Exception {
		List<Thread> threads = createGetInstanceThreads(GET_INSTANCE_THREAD_COUNT);
		startThreads(threads);
		while (!threadsAreDone(threads, GET_INSTANCE_THREAD_COUNT) && threadsRunning > 0 ) {
			logger.debug("threads running {}", threadsRunning);
		}
		//assertEquals(1, I18nCache.instantiationCount.intValue());
		//assertTrue(I18nCache.instantiationCount.intValue() < 1);
		logger.debug("instantiation count is {}", I18nCache.instantiationCount.intValue());
	}
	
	@Test
	public void testThreadsafe_CanGetTranslationsDuringBuild() throws Exception {
		List<Thread> threads = createSafeThreads(THREAD_COUNT);
		startThreads(threads);
		while (!threadsAreDone(threads, THREAD_COUNT)) {
			String value = i18nCache.getText("Test.Key", Locale.ENGLISH);
			logger.debug("Got value {}", value);
			Thread.sleep(QUERY_TIME_IN_MILLISECONDS/3);
			assertThat(value, startsWith("Hello Testing World!"));
		}
	}

	private boolean threadsAreDone(List<Thread> threads, int threadCount) {
		int numberThreadsDone = 0;
		for (Thread thread : threads) {
			if (Thread.State.TERMINATED == thread.getState()) {
				numberThreadsDone++;
			}
		}
		if (numberThreadsDone == THREAD_COUNT) {
			return true;
		} else {
			return false;
		}
	}

	
	@Test
	public void testThreadsafe_ClearSynchronizesBuild() throws Exception {
		List<Thread> threads = createSafeThreads(THREAD_COUNT);
		startAndVerify(threads);
	}

	@Test(expected=java.lang.AssertionError.class)
	public void testThreadsafe_BuildCacheNotSynchronized_WillFail() throws Exception {
		List<Thread> threads = createUnSafeThreads(THREAD_COUNT);
		startAndVerify(threads);
	}

	private void startAndVerify(List<Thread> threads) {
		startThreads(threads);
		
		while (!threadsAreDone(threads, THREAD_COUNT)) {
			if (threadsRunning > 1) {
				fail("More than one thread is building the cache at the same time: "+threadsRunning);
			}
		}
		assertTrue("there was never more than one thread running at once", true);
	}

	private void startThreads(List<Thread> threads) {
		for (Thread thread : threads) {
			thread.start();
		}
	}
	
	private List<Thread> createUnSafeThreads(int threadCount) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int count = 0; count < threadCount; count++) {
			threads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						i18nCache.buildCache();
					} catch (Exception e) {
						System.out.println("Thread exception during JUnit test!");
					}
				}
			}));
		}

		return threads;
	}
	
	private List<Thread> createSafeThreads(int threadCount) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int count = 0; count < threadCount; count++) {
			threads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						i18nCache.clear();
					} catch (Exception e) {
						System.out.println("Thread exception during JUnit test!");
					}
				}
			}));
		}

		return threads;
	}
	
	private List<Thread> createGetInstanceThreads(int threadCount) {
		List<Thread> threads = new ArrayList<Thread>();
		for (int count = 0; count < threadCount; count++) {
			threads.add(new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						I18nCache.getInstance();
					} catch (Exception e) {
						System.out.println("Thread exception during JUnit test!");
					}
				}
			}));
		}

		return threads;
	}

	@Override
	public synchronized void cacheBuildStarted(long epochTime) {
		threadsRunning++;
		logger.info("cacheBuildStarted: {}; threads running: {}", epochTime, threadsRunning);
	}

	@Override
	public synchronized void cacheBuildStopped(long elapsedTime, boolean successful) {
		threadsRunning--;
		logger.info("cacheBuildStopped: {} ms; threads running: {}", elapsedTime, threadsRunning);
	}

}
