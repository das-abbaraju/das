package com.picsauditing.security;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;


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
		String expectedCookieValue = "941|" + now.getTime() + '|' + embeddedData;
		sessionCookie.setUserID(941);
		sessionCookie.setCookieCreationTime(now);
		sessionCookie.setEmbeddedData(embeddedData);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieValue)));
	}

	@Test
	public void testToString_EmptyStringEmbeddedData() throws Exception {
		Date now = new Date();
		String embeddedData = "";
		String expectedCookieValue = "941|" + now.getTime() + '|' + embeddedData;
		sessionCookie.setUserID(941);
		sessionCookie.setCookieCreationTime(now);
		sessionCookie.setEmbeddedData(embeddedData);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieValue)));
	}

	@Test
	public void testToString_NoEmbeddedData() throws Exception {
		Date now = new Date();
		String expectedCookieValue = "941|" + now.getTime() + '|';
		sessionCookie.setUserID(941);
		sessionCookie.setCookieCreationTime(now);

		assertThat(sessionCookie.toString(), is(equalTo(expectedCookieValue)));
	}
}
