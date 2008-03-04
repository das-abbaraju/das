package com.picsauditing.PICS;

import junit.framework.TestCase;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.EmailBean;

public class EmailBeanTest extends TestCase {
	private static String accountID = "249"; // Pacific Industrial Contractor Screening
	public void testSendWelcomeEmail() {
		try {
			AccountBean aBean = new AccountBean();
			aBean.setConn(DefaultDatabase.getConnection());
			aBean.setFromDB(accountID);
			EmailBean emailer = new EmailBean();
			emailer.setConn(DefaultDatabase.getConnection());
			//emailer.sendWelcomeEmail(aBean, "junit Tester");
		} catch (Exception e) {
			fail("Exception thrown: "+e.getMessage());
		}
    }
}
