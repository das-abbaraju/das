package com.picsauditing.util.generic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

public class GenericUtilTest {

	@Mock
	private Logger loggerForTesting;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		GenericUtil.loggerForTesting = loggerForTesting;
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		GenericUtil.loggerForTesting = null;
	}

	@Test
	public void testNewInstance_NullReturnsNull() {
		assertNull(GenericUtil.newInstance(null));
	}

	@Test
	public void testNewInstance() {
		try {
			String result = GenericUtil.newInstance(String.class);
			assertNotNull(result);
		} catch (Exception e) {
			fail("No exception should be thrown when creating a new instance of a String.");
		}
	}

	@Test
	public void testNewInstance_CannotCreateNewInstance_NullIsReturned() {
		fail("Purposely failing");
		
		Logger logger = Mockito.mock(Logger.class);
		GenericUtil.loggerForTesting = logger;

		try {
			TestClass result = GenericUtil.newInstance(TestClass.class);
			assertNull(result);

			verify(logger, times(1)).warn(anyString(), any(), any());
		} catch (Exception e) {
			fail("No exception should be thrown when creating a new instance of a TestClass, even though a new instance is not created.");
		}
	}

	// class that is used as a test case where a new instance cannot be created by the GenericUtil
	private class TestClass {

		private TestClass() { }

	}

}
