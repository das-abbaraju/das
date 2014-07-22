package com.picsauditing.employeeguard.controllers.importexport;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.ResourceBundleMocking;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.util.file.UploadResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeeImportExportActionTest extends PicsActionTest {

	public static final String TEST_ACCOUNT_NAME = "Test Account";
	public static final int CONTRACTOR_ID = 788;
	public static final String FILE_NAME = "testfile.csv";
	public static final String ERROR_OCCURRED_DURING_UPLOAD = "Error occurred during upload";
	public static final int APP_USER_ID = 34123;

	// Class under test
	private EmployeeImportExportAction employeeImportExportAction;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private EmailService emailService;
	@Mock
	private EmailHashService emailHashService;
	@Mock
	private File file;

	private ResourceBundleMocking resourceBundleMocking;

	public static final List<Employee> FAKE_EMPLOYEES = new ArrayList<Employee>() {{

		add(new EmployeeBuilder().accountId(CONTRACTOR_ID).firstName("Test 1").build());
		add(new EmployeeBuilder().accountId(CONTRACTOR_ID).firstName("Test 2").build());

	}};

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeImportExportAction = new EmployeeImportExportAction();
		employeeImportExportAction.setUpload(file);
		employeeImportExportAction.setUploadFileName(FILE_NAME);

		super.setUp(employeeImportExportAction);

		Whitebox.setInternalState(employeeImportExportAction, "accountService", accountService);
		Whitebox.setInternalState(employeeImportExportAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(employeeImportExportAction, "emailService", emailService);
		Whitebox.setInternalState(employeeImportExportAction, "emailHashService", emailHashService);

		when(permissions.getAccountId()).thenReturn(CONTRACTOR_ID);
		when(permissions.getAppUserID()).thenReturn(APP_USER_ID);

		when(accountService.getAccountById(anyInt()))
				.thenReturn(new AccountModel.Builder()
						.id(CONTRACTOR_ID)
						.accountType(AccountType.CONTRACTOR)
						.name(TEST_ACCOUNT_NAME)
						.build());

		resourceBundleMocking = new ResourceBundleMocking();
		resourceBundleMocking.setUp();
	}

	@After
	public void tearDown() {
		resourceBundleMocking.tearDown();
	}

	@Test
	public void testUpload_FailedUpload() throws IOException {
		setupTestUpload_FailedUpload();

		String result = employeeImportExportAction.upload();

		verifyTestUpload_FailedUpload(result);
	}

	private void setupTestUpload_FailedUpload() {
		when(employeeEntityService.importEmployees(CONTRACTOR_ID, file, FILE_NAME)).thenReturn(new UploadResult.Builder()
				.uploadError(true)
				.errorMessage(ERROR_OCCURRED_DURING_UPLOAD)
				.build());
	}

	private void verifyTestUpload_FailedUpload(String result) {
		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(ERROR_OCCURRED_DURING_UPLOAD, employeeImportExportAction.getActionErrors().iterator().next());
	}

	@Test
	public void testUpload_FailedUpload_UnexpectedException() throws IOException {
		when(employeeEntityService.importEmployees(anyInt(), any(File.class), anyString()))
				.thenThrow(new RuntimeException());

		String result = employeeImportExportAction.upload();

		verifyTestUpload_FailedUpload_UnexpectedException(result);
	}

	private void verifyTestUpload_FailedUpload_UnexpectedException(final String result) {
		assertEquals(PicsActionSupport.REDIRECT, result);
		assertEquals(ResourceBundleMocking.DEFAULT_RESOURCE_BUNDLE_STRING,
				employeeImportExportAction.getActionErrors().iterator().next());
	}

	@Test
	public void testUpload_Successful() throws Exception {
		setupTestUpload_Successful();

		String result = employeeImportExportAction.upload();

		verifyTestUpload_Successful(result);
	}

	private void setupTestUpload_Successful() {

		when(employeeEntityService.importEmployees(CONTRACTOR_ID, file, FILE_NAME)).thenReturn(new UploadResult.Builder()
				.uploadError(false)
				.importedEntities(FAKE_EMPLOYEES)
				.build());
	}

	private void verifyTestUpload_Successful(final String result) throws Exception {
		assertEquals(PicsActionSupport.REDIRECT, result);

		verify(employeeEntityService).save(anyListOf(Employee.class), any(EntityAuditInfo.class));
		verify(emailHashService, times(2)).createNewHash(any(Employee.class));
		verify(emailService, times(2)).sendEGWelcomeEmail(any(EmailHash.class), anyString());
	}

	@Test
	public void testDownload_Error() throws IOException {
		when(employeeEntityService.exportEmployees(anyInt())).thenThrow(new IOException());

		String actionResult = employeeImportExportAction.download();

		assertEquals(PicsActionSupport.REDIRECT, actionResult);
		assertEquals(ResourceBundleMocking.DEFAULT_RESOURCE_BUNDLE_STRING,
				employeeImportExportAction.getActionErrors().iterator().next());
	}

	@Test
	public void testDownload_Successful() throws IOException {
		when(employeeEntityService.exportEmployees(anyInt())).thenReturn(new byte[]{1});

		String actionResult = employeeImportExportAction.download();

		assertEquals(PicsActionSupport.FILE_DOWNLOAD, actionResult);
		assertNotNull(employeeImportExportAction.getFileContainer());
	}

	@Test
	public void testTemplate_Error() throws Exception {
		when(employeeEntityService.employeeImportTemplate()).thenReturn(null);

		String actionResult = employeeImportExportAction.template();

		assertEquals(PicsActionSupport.REDIRECT, actionResult);
		assertEquals(ResourceBundleMocking.DEFAULT_RESOURCE_BUNDLE_STRING,
				employeeImportExportAction.getActionErrors().iterator().next());
	}

	@Test
	public void testTemplate_Successful() throws Exception {
		when(employeeEntityService.employeeImportTemplate()).thenReturn(new byte[]{1});

		String actionResult = employeeImportExportAction.template();

		assertEquals(PicsActionSupport.FILE_DOWNLOAD, actionResult);
		assertNotNull(employeeImportExportAction.getFileContainer());
	}
}
