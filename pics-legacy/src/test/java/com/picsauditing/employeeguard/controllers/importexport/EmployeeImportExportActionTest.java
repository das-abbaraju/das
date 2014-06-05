package com.picsauditing.employeeguard.controllers.importexport;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.util.file.UploadResult;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
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

		when(permissions.getAccountId()).thenReturn(Account.PicsID);
		when(permissions.getAppUserID()).thenReturn(User.SYSTEM);

		when(accountService.getAccountById(anyInt()))
				.thenReturn(new AccountModel.Builder()
						.id(CONTRACTOR_ID)
						.accountType(AccountType.CONTRACTOR)
						.name(TEST_ACCOUNT_NAME)
						.build());
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
		assertEquals("Could not prepare download", employeeImportExportAction.getActionErrors().iterator().next());
	}

	@Test
	public void testDownload_Successful() throws IOException {
		when(employeeEntityService.exportEmployees(anyInt())).thenReturn(new byte[] { 1 });

		String actionResult = employeeImportExportAction.download();

		assertEquals(PicsActionSupport.FILE_DOWNLOAD, actionResult);
		assertNotNull(employeeImportExportAction.getFileContainer());
	}

	@Test
	public void testTemplate_Error() throws Exception {
		when(employeeEntityService.employeeImportTemplate()).thenReturn(null);

		String actionResult = employeeImportExportAction.template();

		assertEquals(PicsActionSupport.REDIRECT, actionResult);
		assertEquals("Could not prepare download", employeeImportExportAction.getActionErrors().iterator().next());
	}

	@Test
	public void testTemplate_Successful() throws Exception {
		when(employeeEntityService.employeeImportTemplate()).thenReturn(new byte[] { 1 });

		String actionResult = employeeImportExportAction.template();

		assertEquals(PicsActionSupport.FILE_DOWNLOAD, actionResult);
		assertNotNull(employeeImportExportAction.getFileContainer());
	}
}
