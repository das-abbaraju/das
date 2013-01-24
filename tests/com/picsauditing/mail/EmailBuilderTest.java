package com.picsauditing.mail;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class EmailBuilderTest {
	private EmailBuilder builder;
	@Mock
	private Permissions permissions;
	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private User user;

	@Before
	public void setUp() throws Exception {
		builder = new EmailBuilder();
		MockitoAnnotations.initMocks(this);

		permissions = new Permissions();
		contractorAccount = new ContractorAccount();
		user = new User();
	}

	@Test
	public void testGetUserLocale() throws Exception {
		// When nothing is provided the language fallback should be English
		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertNotNull(locale);
		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_Permissions() throws Exception {
		permissions.setLocale(Locale.SIMPLIFIED_CHINESE);
		builder.addToken("permissions", permissions);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.SIMPLIFIED_CHINESE, locale);
	}

	@Test
	public void testGetUserLocale_PermissionsInstanceOfSomethingElse() throws Exception {
		permissions.setLocale(Locale.SIMPLIFIED_CHINESE);
		builder.addToken("permissions", "Permissions String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_Contractor() throws Exception {
		contractorAccount.setLocale(Locale.FRENCH);
		builder.addToken("contractor", contractorAccount);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.FRENCH, locale);
	}

	@Test
	public void testGetUserLocale_ContractorInstanceOfSomethingElse() throws Exception {
		contractorAccount.setLocale(Locale.FRENCH);
		builder.addToken("contractor", "Contractor String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_User() throws Exception {
		user.setLocale(Locale.GERMAN);
		builder.addToken("user", user);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.GERMAN, locale);
	}

	@Test
	public void testGetUserLocale_UserInstanceOfSomethingElse() throws Exception {
		user.setLocale(Locale.GERMAN);
		builder.addToken("user", "User String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_UserNamedPrimaryContact() throws Exception {
		user.setLocale(Locale.ITALIAN);
		builder.addToken("primaryContact", user);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ITALIAN, locale);
	}

	@Test
	public void testGetUserLocale_UserNamedPrimaryContactInstanceOfSomethingElse() throws Exception {
		user.setLocale(Locale.ITALIAN);
		builder.addToken("primaryContact", "User named Primary Contact String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}
}
