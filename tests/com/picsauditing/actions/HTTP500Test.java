package com.picsauditing.actions;

import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.search.Database;

public class HTTP500Test {
	private HTTP500 http500;

	@Mock
	Database database;
	@Mock
	HttpServletRequest request;
	@Mock
	ServletContext context;
	@Mock
	Throwable exception;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		http500 = new HTTP500(request, context, exception);
		PicsTestUtil.forceSetPrivateField(http500, "database", database);

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("Hello", "World") });
	}

	@Test
	public void testShowCookiesDefaultsTrue() throws Exception {
		http500.saveError();
		Assert.assertTrue(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesFalse() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "false" });
		http500.saveError();
		Assert.assertFalse(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesZero() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "0" });
		http500.saveError();
		Assert.assertFalse(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesOne() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "1" });
		http500.saveError();
		Assert.assertTrue(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesOther() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "hello world" });
		http500.saveError();
		Assert.assertFalse(http500.isShowCookies());
	}
}
