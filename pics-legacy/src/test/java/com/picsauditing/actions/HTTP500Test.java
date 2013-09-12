package com.picsauditing.actions;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

		when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("Hello", "World") });
	}

	@Test
	public void testShowCookiesDefaultsTrue() throws Exception {
		http500.saveError();
		assertTrue(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesFalse() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "false" });
		http500.saveError();
		assertFalse(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesZero() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "0" });
		http500.saveError();
		assertFalse(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesOne() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "1" });
		http500.saveError();
		assertTrue(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesOther() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[] { "hello world" });
		http500.saveError();
		assertFalse(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesNull() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(null);
		http500.saveError();
		assertTrue(http500.isShowCookies());
	}

	@Test
	public void testSetShowCookiesEmpty() throws Exception {
		when(request.getParameterValues("showCookies")).thenReturn(new String[0]);
		http500.saveError();
		assertTrue(http500.isShowCookies());
	}

	@Test
	public void testMissingConstructorFields() throws Exception {
		http500 = new HTTP500(null, null, null);

		assertFalse(http500.hasRequest());
		assertFalse(http500.hasContext());
		assertFalse(http500.hasException());
	}

	@Test
	public void testConstructorFieldsExist() throws Exception {
		assertTrue(http500.hasRequest());
		assertTrue(http500.hasContext());
		assertTrue(http500.hasException());
	}

	@Test
	public void testEmailWasInserted() throws SQLException {
		http500.saveError();
		verify(database, only()).executeInsert(anyString());
	}

	@Test
	public void testExceptionWasLogged() throws SQLException {
		http500.saveError();
		verify(context, atLeastOnce()).log(anyString());
		verify(context, atLeastOnce()).log(anyString(), eq(exception));
	}
}
