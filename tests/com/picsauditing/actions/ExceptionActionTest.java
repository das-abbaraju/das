package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Enumeration;

import javax.mail.Transport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import com.ibm.icu.util.Calendar;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.ErrorLog;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.GridSender;

public class ExceptionActionTest extends PicsActionTest {
	private ExceptionAction exceptionAction;

	@Mock
	private BasicDAO dao;
	@Mock
	private EmailSender emailSender;
	@SuppressWarnings("rawtypes")

	@Mock
	private Exception exception;
	@Mock
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		exceptionAction = new ExceptionAction();
		super.setUp(exceptionAction);

		Whitebox.setInternalState(exceptionAction, "dao", dao);
		Whitebox.setInternalState(exceptionAction, "emailSender", emailSender);
		Whitebox.setInternalState(exceptionAction, "logger", logger);
		Whitebox.setInternalState(exceptionAction, "permissions", permissions);

		exceptionAction.setException(exception);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -3);
		when(httpSession.getCreationTime()).thenReturn(calendar.getTimeInMillis());
	}

	@Test
	public void testExecute_Exception() {
		doThrow(new IllegalArgumentException()).when(emailSender).send(any(EmailQueue.class));

		assertEquals("Exception", exceptionAction.execute());
		verify(logger, atLeastOnce()).error(anyString());
	}

	@Test
	public void testExecute_SessionLessThanASecond() {
		// move it forward a minute since we can't be sure this test will take <
		// 1 second
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		when(httpSession.getCreationTime()).thenReturn(calendar.getTimeInMillis());

		assertEquals(PicsActionSupport.REDIRECT, exceptionAction.execute());
		assertEquals("//www.picsorganizer.com/", exceptionAction.getUrl());

		verify(dao).save(any(ErrorLog.class));
	}

	@Test
	public void testExecute_SessionLongerThanASecondLoggedInAdmin() {
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getAdminID()).thenReturn(1);

		assertEquals("Exception", exceptionAction.execute());

		verify(dao).save(any(ErrorLog.class));
		verify(permissions).getName();
		verify(permissions).getUsername();
		verify(permissions).getAccountId();
		// SetAuditColumns as well
		verify(permissions, times(4)).getAdminID();
		verify(permissions).getAccountType();
		verify(request).getLocalName();
		verify(request).getRequestURI();
		verify(request).getQueryString();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();
		verify(emailSender).send(any(EmailQueue.class));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void testExecute_SessionLongerThanASecondLoggedInNonAdmin() {
		when(permissions.isLoggedIn()).thenReturn(true);

		assertEquals("Exception", exceptionAction.execute());

		verify(dao).save(any(ErrorLog.class));
		verify(permissions).getName();
		verify(permissions).getUsername();
		verify(permissions).getAccountId();
		verify(permissions, times(2)).getAdminID();
		verify(permissions).getAccountType();
		verify(request).getLocalName();
		verify(request).getRequestURI();
		verify(request).getQueryString();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();
		verify(emailSender).send(any(EmailQueue.class));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void testExecute_SessionLongerThanASecondNotLoggedIn() {
		assertEquals("Exception", exceptionAction.execute());

		verify(dao).save(any(ErrorLog.class));
		verify(permissions, never()).getName();
		verify(permissions, never()).getUsername();
		verify(permissions, never()).getAccountId();
		verify(permissions).getAdminID();
		verify(permissions, never()).getAccountType();
		verify(request).getLocalName();
		verify(request).getRequestURI();
		verify(request).getQueryString();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();
		verify(emailSender).send(any(EmailQueue.class));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void testSendExceptionEmail_LoggedInAdminNoExceptionsEmptyUserMessageEmptyExceptionStack() {
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.getAdminID()).thenReturn(1);

		assertEquals("Submitted", exceptionAction.sendExceptionEmail());

		verify(dao, never()).save(any(ErrorLog.class));
		verify(permissions).getName();
		verify(permissions).getUsername();
		verify(permissions).getAccountId();
		verify(permissions, times(2)).getAdminID();
		verify(permissions).getAccountType();
		verify(permissions).getUserId();
		verify(permissions, times(2)).getEmail();
		verify(request).getLocalName();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();
		verify(emailSender).send(any(EmailQueue.class));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void testSendExceptionEmail_LoggedInNotAdminNoExceptionsUserMessageExceptionStack() {
		when(permissions.isLoggedIn()).thenReturn(true);

		exceptionAction.setUser_message("User Message");
		exceptionAction.setExceptionStack("Exception Stack");

		assertEquals("Submitted", exceptionAction.sendExceptionEmail());

		verify(dao, never()).save(any(ErrorLog.class));
		verify(permissions).getName();
		verify(permissions).getUsername();
		verify(permissions).getAccountId();
		verify(permissions).getAdminID();
		verify(permissions).getAccountType();
		verify(permissions).getUserId();
		verify(permissions, times(2)).getEmail();
		verify(request).getLocalName();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();
		verify(emailSender).send(any(EmailQueue.class));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void testSendExceptionEmail_NotLoggedIn() {
		assertEquals("Submitted", exceptionAction.sendExceptionEmail());

		verify(permissions, never()).getName();
		verify(permissions, never()).getUsername();
		verify(permissions, never()).getAccountId();
		verify(permissions, never()).getAdminID();
		verify(permissions, never()).getAccountType();
		verify(permissions, never()).getUserId();
		verify(permissions, never()).getEmail();
		verify(request).getLocalName();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();

		verify(emailSender).send(any(EmailQueue.class));
		verify(logger, never()).error(anyString());
	}

	@Test
	public void testSendExceptionEmail_NotLoggedInExceptionBeforeMailing() throws Exception {
		GridSender gridSender = mock(GridSender.class);
		Whitebox.setInternalState(exceptionAction, "gridSenderForTesting", gridSender);

		doThrow(new IllegalArgumentException()).when(emailSender).send(any(EmailQueue.class));

		assertEquals("Submitted", exceptionAction.sendExceptionEmail());

		verify(permissions, never()).getName();
		verify(permissions, never()).getUsername();
		verify(permissions, never()).getAccountId();
		verify(permissions, never()).getAdminID();
		verify(permissions, never()).getAccountType();
		verify(permissions, never()).getUserId();
		verify(permissions, never()).getEmail();
		verify(request).getLocalName();
		verify(request).getRemoteAddr();
		verify(request).getHeaderNames();

		verify(gridSender).sendMail(any(EmailQueue.class));
		verify(logger, atLeastOnce()).error(anyString());
	}

	@Test
	public void testGetException() {
		assertEquals(exception, exceptionAction.getException());
	}

	@Test
	public void testGetExceptionStack() {
		assertNull(exceptionAction.getExceptionStack());

		exceptionAction.setExceptionStack("ExceptionStack");

		assertEquals("ExceptionStack", exceptionAction.getExceptionStack());
	}

	@Test
	public void testGetPriority() {
		assertEquals(1, exceptionAction.getPriority());

		exceptionAction.setPriority(12);

		assertEquals(12, exceptionAction.getPriority());
	}

	@Test
	public void testGetUser_message() {
		assertNull(exceptionAction.getUser_message());

		exceptionAction.setUser_message("User Message");

		assertEquals("User Message", exceptionAction.getUser_message());
	}

	@Test
	public void testGetFrom_address() {
		assertNull(exceptionAction.getFrom_address());

		exceptionAction.setFrom_address("test@test.com");

		assertEquals("test@test.com", exceptionAction.getFrom_address());
	}

	@Test
	public void testGetUser_name() {
		assertNull(exceptionAction.getUser_name());

		exceptionAction.setUser_name("test@test.com");

		assertEquals("test@test.com", exceptionAction.getUser_name());
	}

	@Test
	public void testCreateExceptionMessage() throws Exception {
		when(exception.toString()).thenReturn("Exception String");
		assertEquals("Exception String", Whitebox.invokeMethod(exceptionAction, "createExceptionMessage").toString());

		when(exception.getMessage()).thenReturn("Message");
		assertEquals("Message", Whitebox.invokeMethod(exceptionAction, "createExceptionMessage").toString());
	}
}
