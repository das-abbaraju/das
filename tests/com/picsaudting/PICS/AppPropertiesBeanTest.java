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
		props.setConnection(DefaultDatabase.getConnection());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		props = null;
	}

	public void testDesktopSubmitEmail() {
		try {
			props.addToken("contact_name", "Trevor Allred");
			props.addToken("account_name", "Allred Crane");
			props.addToken("email", "${main_email}");
			props.addToken("fax", "${main_fax}");
			props.addToken("ext", "");
			String body = props.get("email_desktopsubmit_body");
			
			assertFalse(body.contains("${"));
			assertEquals(1368, body.length());
		} catch (Exception e) {
			fail("Exception thrown "+e.getMessage());
		}
	}
}
