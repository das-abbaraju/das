package com.picsauditing.mail;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Token;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.VelocityAdaptorTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
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
    @Mock
    private Token token;

	@Before
	public void setUp() throws Exception {
		builder = new EmailBuilder();
		MockitoAnnotations.initMocks(this);
        ArrayList<Token> picsTags = new ArrayList<>();
        picsTags.add(token);
        Whitebox.setInternalState(builder, "picsTags", picsTags);
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
		when(permissions.getLocale()).thenReturn(Locale.SIMPLIFIED_CHINESE);
		builder.addToken(EmailBuilder.PERMISSIONS, permissions);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.SIMPLIFIED_CHINESE, locale);
	}

	@Test
	public void testGetUserLocale_PermissionsInstanceOfSomethingElse() throws Exception {
		when(permissions.getLocale()).thenReturn(Locale.SIMPLIFIED_CHINESE);
		builder.addToken(EmailBuilder.PERMISSIONS, "Permissions String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_Contractor() throws Exception {
		when(contractorAccount.getLocale()).thenReturn(Locale.FRENCH);
		builder.addToken(EmailBuilder.CONTRACTOR, contractorAccount);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.FRENCH, locale);
	}

	@Test
	public void testGetUserLocale_ContractorInstanceOfSomethingElse() throws Exception {
		when(contractorAccount.getLocale()).thenReturn(Locale.FRENCH);
		builder.addToken(EmailBuilder.CONTRACTOR, "Contractor String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_User() throws Exception {
		when(user.getLocale()).thenReturn(Locale.GERMAN);
		builder.addToken(EmailBuilder.USER, user);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.GERMAN, locale);
	}

	@Test
	public void testGetUserLocale_UserInstanceOfSomethingElse() throws Exception {
		when(user.getLocale()).thenReturn(Locale.GERMAN);
		builder.addToken(EmailBuilder.USER, "User String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test
	public void testGetUserLocale_UserNamedPrimaryContact() throws Exception {
		when(user.getLocale()).thenReturn(Locale.ITALIAN);
		builder.addToken(EmailBuilder.PRIMARY_CONTACT, user);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ITALIAN, locale);
	}

	@Test
	public void testGetUserLocale_UserNamedPrimaryContactInstanceOfSomethingElse() throws Exception {
		when(user.getLocale()).thenReturn(Locale.ITALIAN);
		builder.addToken(EmailBuilder.PRIMARY_CONTACT, "User named Primary Contact String");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}


	@Test
	public void testGetUserLocale_TokenNamedLocale() throws Exception {
		builder.addToken(EmailBuilder.LOCALE, Locale.JAPANESE);

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.JAPANESE, locale);
	}

	@Test
	public void testGetUserLocale_TokenNamedLocaleNotInstanceOfLocale() throws Exception {
		builder.addToken(EmailBuilder.LOCALE, "String passed in as locale");

		Locale locale = Whitebox.invokeMethod(builder, "getUserLocale");

		assertEquals(Locale.ENGLISH, locale);
	}

	@Test(expected = EmailBuildErrorException.class)
	public void testBuild_templateWithABadSubject_shouldThrowAHelpfulException_PICS_13365() throws Exception {

		// Note: the original ticket applies to the body template (not the subject), but this is the same issue.
		EmailTemplate template = buildEmailTemplateWithABadSubject();
		builder.setTemplate(template);
		builder.addToken("i18nCache", null);
		builder.setConID(55);
		builder.setToAddresses("recipient@example.com");
		Whitebox.setInternalState(builder, "picsTags", new ArrayList<Token>());

		builder.build();
	}

    @Test
    public void testConvertPicsTagsToVelocity() throws Exception {
        when(token.getName()).thenReturn("PicsSignature");
        when(token.getVelocityCode(Locale.ENGLISH)).thenReturn("I am English");

        String text = Whitebox.invokeMethod(builder, "convertPicsTagsToVelocity", "<PicsSignature>", true, Locale.ENGLISH);

        assertEquals("I am English", text);
    }

    private EmailTemplate buildEmailTemplateWithABadSubject() {
		EmailTemplate template = new EmailTemplate();
		template.setId(10);
		template.setSubject(VelocityAdaptorTest.EmailTemplate_107_translatedBody_sv);
		template.setAllowsVelocity(true);

		return template;
	}
}
