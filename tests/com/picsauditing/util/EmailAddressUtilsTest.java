package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class EmailAddressUtilsTest {

	@Test
	public void testFindUniqueEmailAddresses(){
		assertEquals(2, EmailAddressUtils.findUniqueEmailAddresses("lwang@picsauditing.com, test@picsauditing.com").size());
		assertEquals(2, EmailAddressUtils.findUniqueEmailAddresses("lwang@picsauditing.com, test@picsauditing.com, lwang$hotmail.com").size());
	}
	@Test
	public void testValidate(){
		assertEquals("lwang+123@picsauditing.com", EmailAddressUtils.validate("lwang+123@picsauditing.com"));
		assertEquals("lwang@picsauditing.com", EmailAddressUtils.validate("lwang@picsauditing.com    "));
		assertEquals("lwang@picsauditing.com", EmailAddressUtils.validate("      lwang@picsauditing.com"));
		assertEquals("lwang@picsauditing.com", EmailAddressUtils.validate("         lwang@picsauditing.com             "));
		assertEquals("billing@picsauditing.com", EmailAddressUtils.validate("billing@picsauditingcom"));
	}

	@Test
	public void testIsValidEmail(){
		assertTrue(EmailAddressUtils.isValidEmail("lwang+123@picsauditing.com"));
		assertTrue(EmailAddressUtils.isValidEmail("lwang@picsauditing.com    "));
		assertTrue(EmailAddressUtils.isValidEmail("      lwang@picsauditing.com"));
		assertTrue(EmailAddressUtils.isValidEmail("         lwang@picsauditing.com             "));
		assertFalse(EmailAddressUtils.isValidEmail("billing@picsauditingcom"));
	}
}
