package com.picsauditing.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CookieSupportTest {

	@Mock
	private HttpServletRequest request;
	@Mock
	private Cookie cookie1;
	@Mock
	private Cookie cookie2;
	@Mock
	private Cookie cookie3;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		Cookie[] cookies = new Cookie[] { cookie1, cookie2, cookie3 };
		when(request.getCookies()).thenReturn(cookies);
	}

	@Test
	public void testCookiesFromRequestThatStartWith_HappyPath_1() throws Exception {
		when(cookie1.getName()).thenReturn("MyNameIsCookie");
		when(cookie1.getValue()).thenReturn("MyValueIsImmeasurable");

		assertTrue(CookieSupport.cookiesFromRequestThatStartWith(request, "MyNameIs").size() == 1);
	}

	@Test
	public void testCookiesFromRequestThatStartWith_HappyPath_2() throws Exception {
		when(cookie1.getName()).thenReturn("MyNameIsCookie");
		when(cookie1.getValue()).thenReturn("MyValueIsImmeasurable");
		when(cookie2.getName()).thenReturn("MyNameIsAlsoCookie");
		when(cookie2.getValue()).thenReturn("MyValueIsNotSoImmeasurable");

		assertTrue(CookieSupport.cookiesFromRequestThatStartWith(request, "MyNameIs").size() == 2);
		assertTrue(CookieSupport.cookiesFromRequestThatStartWith(request, "MyNameIsAlso").size() == 1);
	}
	@Test
	public void testCookiesFromRequestThatStartWith_NullNameReturnsEmptySet() throws Exception {
		assertTrue(CookieSupport.cookiesFromRequestThatStartWith(request, "").isEmpty());
	}

	@Test
	public void testCookiesFromRequestThatStartWith_NullRequestReturnsEmptySet() throws Exception {
		assertTrue(CookieSupport.cookiesFromRequestThatStartWith(null, "not null").isEmpty());
	}

	@Test
	public void testCookieFromRequest_HappyPath() throws Exception {
		when(cookie1.getName()).thenReturn("MyNameIsCookie");
		when(cookie1.getValue()).thenReturn("MyValueIsImmeasurable");

		assertThat(CookieSupport.cookieFromRequest(request, "MyNameIsCookie").getValue(),
				is(equalTo("MyValueIsImmeasurable")));
	}

	@Test
	public void testCookieFromRequest_NullCookiesReturnsNull() throws Exception {
		when(request.getCookies()).thenReturn(null);

		assertTrue(null == CookieSupport.cookieFromRequest(request, "not null"));
	}

	@Test
	public void testCookieFromRequest_NullRequestReturnsNull() throws Exception {
		assertTrue(null == CookieSupport.cookieFromRequest(null, "not null"));
	}

	@Test
	public void testCookieFromRequest_NullCookieNameReturnsNull() throws Exception {
		assertTrue(null == CookieSupport.cookieFromRequest(request, null));
	}

	@Test
	public void testCookieFromRequest_EmptyCookieNameReturnsNull() throws Exception {
		assertTrue(null == CookieSupport.cookieFromRequest(request, ""));
	}
}
