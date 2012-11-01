package com.picsauditing.security;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class SessionSecurityTest {
	private static String secretKeyspec = "9KuRXTx0cnuZefrt0EIfXd1MFqKvMY9x7OSub0B1EGLpR69b1Z+sdB7p6PT3Sy5rhl6qXKYyINdPJoHMWCqBNQ==";
	private static String counterfeitSecretKeyspec = "iop9JP6FZiRZo2dRxFO8UqD/MTxRdkvwdnF5fnkrsp/RyqOQGmBCeFj7dSQRWp3r5vOhETFGZEmjt0g2qpym6A==";
	private SecretKey secretKey;
	private SecretKey counterfeitSecretKey;

	@After
	public void afterClass() throws Exception {
		Whitebox.setInternalState(SessionSecurity.class, "secretKeyspec", (String) null);
		Whitebox.setInternalState(SessionSecurity.class, "serverSecretKey", (SecretKey) null);
	}

	@Before
	public void setUp() throws Exception {
		// don't change the order of setting the keyspecs
		Whitebox.setInternalState(SessionSecurity.class, "secretKeyspec", counterfeitSecretKeyspec);
		counterfeitSecretKey = SessionSecurity.serverSecretKey();
		Whitebox.setInternalState(SessionSecurity.class, "serverSecretKey", (SecretKey) null);
		// don't change the order of setting the keyspecs
		Whitebox.setInternalState(SessionSecurity.class, "secretKeyspec", secretKeyspec);
		secretKey = SessionSecurity.serverSecretKey();
	}

	// As
	// Given
	// When
	// Then
	@Test
	public void testCookieIsValid_CookieWasNotAltered() throws Exception {
		String testCookie1 = "64036|1348850723991|{}";
		String testCookie2 = "64036|1348850723991|{\"switchTo\":941}";

		testCookie1 = testCookie1 + '|' + EncodedMessage.hmacBase64(testCookie1, secretKey);
		testCookie2 = testCookie2 + '|' + EncodedMessage.hmacBase64(testCookie2, secretKey);

		assertTrue(SessionSecurity.cookieIsValid(testCookie1));
		assertTrue(SessionSecurity.cookieIsValid(testCookie2));
	}

	@Test
	@Ignore
	public void testCookieIsValid_HashIsNull() throws Exception {
		Whitebox.setInternalState(SessionSecurity.class, "secretKeyspec", (String) null);
		Whitebox.setInternalState(SessionSecurity.class, "serverSecretKey", (SecretKey) null);

		String testCookieDataOriginal = "64036|1348850723991|";

		assertFalse(SessionSecurity.cookieIsValid(testCookieDataOriginal));
	}

	@Test
	public void testCookieIsValid_CookieDataWasAltered() throws Exception {
		String testCookieDataOriginal = "64036|1348850723991|{}";
		String testCookieDataAltered = "74036|1348850723991|{}";
		String testCookieDataAltered2 = "64036|1348850727791|{}";
		String testCookieDataAltered3 = "64036|1348850723991|{\"switchTo\":941}";

		String hmac = EncodedMessage.hmacBase64(testCookieDataOriginal, secretKey);
		String testCookieAltered = testCookieDataAltered + '|' + hmac;
		String testCookieAltered2 = testCookieDataAltered2 + '|' + hmac;
		String testCookieAltered3 = testCookieDataAltered3 + '|' + hmac;

		assertFalse(SessionSecurity.cookieIsValid(testCookieAltered));
		assertFalse(SessionSecurity.cookieIsValid(testCookieAltered2));
		assertFalse(SessionSecurity.cookieIsValid(testCookieAltered3));
	}

	// As
	// Given
	// When
	// Then
	@Test
	public void testName_Usecase() throws Exception {
		Date now = new Date();
		SessionCookie sessionCookie = new SessionCookie();
		sessionCookie.setUserID(123);
		sessionCookie.setCookieCreationTime(now);

		String hmac = EncodedMessage.hmacBase64(sessionCookie.toString(), secretKey);
		String expectedCookieContent = "123|" + now.getTime() + "||" + hmac;
		SessionSecurity.addValidationHashToSessionCookie(sessionCookie);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieContent)));
	}

	@Test
	public void testCookieIsValid_CookieHashWasAltered() throws Exception {
		String testCookieDataOriginal = "64036|1348850723991|{}";

		String hmac = EncodedMessage.hmacBase64(testCookieDataOriginal, secretKey);
		String alteredHmac = "A8363" + hmac.substring(5);
		String testCookieAltered = testCookieDataOriginal + '|' + alteredHmac;

		assertFalse(SessionSecurity.cookieIsValid(testCookieAltered));
	}

	@Test
	public void testCookieIsValid_CookieReencodedWithCounterfeitSecretKey() throws Exception {
		String testCookieDataOriginal = "64036|1348850723991|{}";

		String hmac = EncodedMessage.hmacBase64(testCookieDataOriginal, counterfeitSecretKey);
		String testCookieAltered = testCookieDataOriginal + '|' + hmac;

		assertFalse(SessionSecurity.cookieIsValid(testCookieAltered));
	}

	// As
	// Given
	// When
	// Then
	@Test
	public void testParseSessionCookie_Usecase() throws Exception {
		String testCookie = "64036|1348850723991|{ \"switchTo\" : 941 }|gQsT/YcdKDby8HOZ1uYQA10HbGI=";

		SessionCookie sessionCookie = SessionSecurity.parseSessionCookie(testCookie);

		assertThat(64036, is(equalTo(sessionCookie.getUserID())));
		assertThat(new Date(1348850723991L), is(equalTo(sessionCookie.getCookieCreationTime())));
		assertThat(941, is(equalTo(sessionCookie.getData("switchTo"))));
	}
}
