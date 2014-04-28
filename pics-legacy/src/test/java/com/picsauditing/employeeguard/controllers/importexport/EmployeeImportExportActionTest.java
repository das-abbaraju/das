package com.picsauditing.employeeguard.controllers.importexport;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.factory.EmployeeServiceFactory;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.web.UrlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeImportExportActionTest extends PicsActionTest {

	public static final String TEST_ACCOUNT_NAME = "Test Account";
	private EmployeeImportExportAction employeeImportExportAction;

	private EmployeeService employeeService;

	@Mock
	private AccountService accountService;
	@Mock
	private File file;
	@Mock
	private UrlBuilder urlBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeImportExportAction = new EmployeeImportExportAction();
		employeeService = EmployeeServiceFactory.getEmployeeService();

		super.setUp(employeeImportExportAction);

		Whitebox.setInternalState(employeeImportExportAction, "accountService", accountService);
		Whitebox.setInternalState(employeeImportExportAction, "employeeService", employeeService);
		Whitebox.setInternalState(employeeImportExportAction, "urlBuilder", urlBuilder);

		when(file.length()).thenReturn(1l);
		when(permissions.getAccountId()).thenReturn(Account.PicsID);
		when(permissions.getAppUserID()).thenReturn(User.SYSTEM);

		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);

		when(accountService.getAccountById(anyInt()))
				.thenReturn(new AccountModel.Builder().name(TEST_ACCOUNT_NAME).build());
	}

	@Test
	public void testProcessUpload() throws Exception {
		employeeImportExportAction.setUpload(file);

		Whitebox.invokeMethod(employeeImportExportAction, "processUpload");

		verify(employeeService).importEmployees(file, Account.PicsID, TEST_ACCOUNT_NAME, User.SYSTEM);
	}

	@Test
	public void testDownload() throws Exception {
		assertEquals(PicsActionSupport.FILE_DOWNLOAD, employeeImportExportAction.download());
		assertNotNull(employeeImportExportAction.getFileContainer());
	}

	@Test
	public void testDownload_ExportHasError() throws Exception {
		when(permissions.getAccountId()).thenReturn(EmployeeServiceFactory.BAD_ACCOUNT_ID);

		assertEquals(PicsActionSupport.FILE_DOWNLOAD, employeeImportExportAction.download());
		assertNull(employeeImportExportAction.getFileContainer());
		assertTrue(employeeImportExportAction.hasActionErrors());
	}

	@Test
	public void testTemplate() throws Exception {
		assertEquals(PicsActionSupport.FILE_DOWNLOAD, employeeImportExportAction.template());
		assertNotNull(employeeImportExportAction.getFileContainer());
	}

	@Test
	public void testInvalidUploadRedirect() throws Exception {
		String redirect = "invalid";
		when(urlBuilder.build()).thenReturn(redirect);
		assertEquals(PicsActionSupport.REDIRECT, employeeImportExportAction.invalidUploadRedirect());
		assertEquals(redirect, employeeImportExportAction.getUrl());
	}

	@Test
	public void testSuccessfulUploadRedirect() throws Exception {
		String redirect = "valid";
		when(urlBuilder.build()).thenReturn(redirect);
		assertEquals(PicsActionSupport.REDIRECT, employeeImportExportAction.successfulUploadRedirect());
		assertEquals(redirect, employeeImportExportAction.getUrl());
	}
}
