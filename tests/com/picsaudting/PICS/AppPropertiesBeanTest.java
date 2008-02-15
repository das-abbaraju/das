package com.picsaudting.PICS;

import com.picsauditing.PICS.AppPropertiesBean;
import junit.framework.TestCase;

public class AppPropertiesBeanTest extends TestCase {
	private AppPropertiesBean props;

	public AppPropertiesBeanTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		props = new AppPropertiesBean();
		props.setConn(DefaultDatabase.getConnection());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		props = null;
	}

	public void testDesktopSubmitEmail() {
		try {
			String body = props.get("email_desktopsubmit_body");
			
			assertFalse(body.contains("${"));
			assertEquals(1368, body.length());
		} catch (Exception e) {
			fail("Exception thrown "+e.getMessage());
		}
	}
}
