package com.picsauditing.mail;

import junit.framework.TestCase;

public class EmailMergeTest extends TestCase {
	public final void testCreateEmail() {
		try {
			EmailMerge merge = new EmailMerge("welcome");
			merge.addTokens("user.name", "John Doe");
			merge.addTokens("user.username", "testuser");
			merge.addTokens("user.password", "f85gj29");
			Email email = merge.createEmail("tester@picsauditing.com");
			EmailSender sender = new EmailSender();
			sender.sendMail(email);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
