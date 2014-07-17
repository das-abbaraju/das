package com.picsauditing.employeeguard.controllers.importexport;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.email.EmailHashService;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.util.file.UploadResult;
import com.picsauditing.strutsutil.FileDownloadContainer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.CONTRACTOR_EMPLOYEE_IMPORT;

public class EmployeeImportExportAction extends PicsRestActionSupport {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeImportExportAction.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private EmailHashService emailHashService;

	private File upload;
	private String uploadFileName;

	public String importExport() {
		return "import-export";
	}

	public String upload() throws IOException {
		try {
			UploadResult<Employee> uploadResult = uploadEmployeesAndSendWelcomeEmails();
			if (uploadResult.isUploadError()) {
				addActionError(uploadResult.getErrorMessage());
			} else {
				addActionMessage(EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.SUCCESSFUL_IMPORT_MSG"));
			}

		} catch (Exception e) {
			LOG.error("Unexpected exception while uploading employees.", e);
			addActionError(EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.IMPORT_ERROR_MSG"));
		}

		return redirectToEmployeeImportPage();
	}

	private UploadResult<Employee> uploadEmployeesAndSendWelcomeEmails() throws IOException {
		int contractorId = permissions.getAccountId();
		UploadResult<Employee> uploadResult = employeeEntityService.importEmployees(contractorId,
				upload, uploadFileName);

		if (!uploadResult.isUploadError()) {
			employeeEntityService.save(uploadResult.getImportedEntities(),
					new EntityAuditInfo.Builder().appUserId(permissions.getAppUserID()).timestamp(DateBean.today())
							.build());

			AccountModel contractorAccount = accountService.getAccountById(contractorId);
			sendEmployeeEmails(uploadResult.getImportedEntities(), contractorAccount.getName());
		}

		return uploadResult;
	}

	private String redirectToEmployeeImportPage() throws IOException {
		return setUrlForRedirect(CONTRACTOR_EMPLOYEE_IMPORT);
	}

	private void sendEmployeeEmails(final List<Employee> processedEmployees, final String accountName) {
		if (CollectionUtils.isEmpty(processedEmployees)) {
			return;
		}

		try {
			for (Employee employee : processedEmployees) {
				EmailHash hash = emailHashService.createNewHash(employee);
				emailService.sendEGWelcomeEmail(hash, accountName);
			}
		} catch (Exception e) {
			LOG.error("Error while sending emails to uploaded employees", e);
		}
	}

	public String download() throws IOException {
		try {
			fileContainer = buildFileContainer(employeeEntityService.exportEmployees(permissions.getAccountId()),
					"EmployeeList.csv");

			return FILE_DOWNLOAD;

		} catch (Exception e) {
			LOG.error("Error exporting employees for account {}\n{}", permissions.getAccountId(), e);
			addActionError(EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.EMPLOYEE_DOWNLOAD_ERROR"));
			return redirectToEmployeeImportPage();
		}
	}

	public String template() throws IOException {
		byte[] employeeImportTemplate = employeeEntityService.employeeImportTemplate();
		if (employeeImportTemplate == null) {
			addActionError(EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE_DOWNLOAD_ERROR"));
			return redirectToEmployeeImportPage();
		}

		fileContainer = buildFileContainer(employeeImportTemplate, "EmployeeListTemplate.csv");

		return FILE_DOWNLOAD;
	}


	private FileDownloadContainer buildFileContainer(final byte[] fileContents, final String filename) {
		return new FileDownloadContainer.Builder()
				.contentType("text/csv")
				.contentDisposition("attachment; filename=" + filename)
				.fileInputStream(new ByteArrayInputStream(fileContents)).build();
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
}
