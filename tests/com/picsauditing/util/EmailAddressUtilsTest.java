package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.mail.EventSubscriptionBuilder;


public class EmailAddressUtilsTest {
	private EmailAddressUtils emailAddressUtils;

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
		assertEquals("info@picsauditing.com", EmailAddressUtils.validate("billing@picsauditingcom"));
	}

	@Test
	public void testIsValidEmail(){
		assertTrue(EmailAddressUtils.isValidEmail("lwang+123@picsauditing.com"));
		assertTrue(EmailAddressUtils.isValidEmail("lwang@picsauditing.com    "));
		assertTrue(EmailAddressUtils.isValidEmail("      lwang@picsauditing.com"));
		assertTrue(EmailAddressUtils.isValidEmail("         lwang@picsauditing.com             "));
		assertFalse(EmailAddressUtils.isValidEmail("billing@picsauditingcom"));
	}
	@Test
	public void testGetBillingEmail_UDS() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EmailAddressUtils.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.USD);
		assertEquals("\"PICS Billing\"<billing@picsauditing.com>", email);
	}
	@Test
	public void testGetBillingEmail_CAD() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EmailAddressUtils.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.CAD);
		assertEquals("\"PICS Billing\"<billing@picsauditing.com>", email);
	}
	@Test
	public void testGetBillingEmail_GBP() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EmailAddressUtils.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.GBP);
		assertEquals("\"PICS Billing\"<eubilling@picsauditing.com>", email);
	}
	@Test
	public void testGetBillingEmail_EUR() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EmailAddressUtils.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.EUR);
		assertEquals("\"PICS Billing\"<eubilling@picsauditing.com>", email);
	}
}
