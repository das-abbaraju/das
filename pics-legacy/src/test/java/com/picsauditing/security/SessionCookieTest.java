package com.picsauditing.security;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class SessionCookieTest {
	private SessionCookie sessionCookie;

	@Before
	public void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this);
		sessionCookie = new SessionCookie();
	}

	// As
	// Given
	// When
	// Then
	@Test
	public void testToString_Usecase() throws Exception {
		Date now = new Date();
		String embeddedData = "{\"switchTo\":1234}";
		String expectedCookieValue = "941|123|" + now.getTime() + '|' + embeddedData;
		sessionCookie.setUserID(941);
		sessionCookie.setAppUserID(123);
		sessionCookie.setCookieCreationTime(now);
		sessionCookie.setEmbeddedData(embeddedData);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieValue)));
	}

	@Test
	public void testToString_EmptyStringEmbeddedData() throws Exception {
		Date now = new Date();
		String embeddedData = "";
		String expectedCookieValue = "941|123|" + now.getTime() + '|' + embeddedData;
		sessionCookie.setUserID(941);
		sessionCookie.setAppUserID(123);
		sessionCookie.setCookieCreationTime(now);
		sessionCookie.setEmbeddedData(embeddedData);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieValue)));
	}

	@Test
	public void testToString_NoEmbeddedData() throws Exception {
		Date now = new Date();
		String expectedCookieValue = "941|123|" + now.getTime() + '|';
		sessionCookie.setUserID(941);
		sessionCookie.setAppUserID(123);
		sessionCookie.setCookieCreationTime(now);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieValue)));
	}
}
