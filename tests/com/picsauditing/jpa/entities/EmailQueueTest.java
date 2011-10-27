package com.picsauditing.jpa.entities;

import junit.framework.TestCase;

import org.junit.Test;

public class EmailQueueTest extends TestCase {
	@Test
	public void testCleanupEmailAddresses() {
		EmailQueue email = new EmailQueue();
		String correctAddress = "noemail@noemail.com,noemail@noemail.com";
		
		email.setToAddresses("[mailto:noemail@noemail.com], noemail@noemail.com");
		email.setFromAddress("[mailto:noemail@noemail.com], [mailto:noemail@noemail.com]  ");
		// we do not semi-colon separate our emails, so this should fail.
		email.setCcAddresses("  [mailto:noemail@noemail.com]; [mailto:noemail@noemail.com]  ");
		email.setBccAddresses("noemail@noemail.com, [mailto:noemail@noemail.com]");
		
		email.cleanupEmailAddresses();
		
		String to = email.getToAddresses();
		String from = email.getFromAddress();
		String cc = email.getCcAddresses();
		String bcc = email.getBccAddresses();
		
		assertTrue(correctAddress.equals(to));
		assertTrue(correctAddress.equals(from));
		assertFalse(correctAddress.equals(cc));
		assertTrue(correctAddress.equals(bcc));
	}

}
