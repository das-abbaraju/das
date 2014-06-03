package com.picsauditing.employeeguard.services;

import au.com.bytecode.opencsv.CSVWriter;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.util.Strings;
import com.picsauditing.util.web.UrlBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileImport {

	private static final Logger LOG = LoggerFactory.getLogger(FileImport.class);

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private AccountService accountService;
	@Autowired
	private EmailHashService emailHashService;
	@Autowired
	private EmailService emailService;

	private Map<String, Object> contractor_employee_import = new HashMap<>();
	private Map<String, Object> contractor_employee_export = new HashMap<>();

	private UrlBuilder urlBuilder;

	public void processUpload(final File upload, final int accountId, final int appUserId) throws Exception {
		AccountModel accountModel = accountService.getAccountById(accountId);

		importEmployees(upload, accountId, accountModel.getName(), appUserId);
	}

	public void importEmployees(final File file, final int accountId, final String accountName,
								final int appUserId) throws Exception {
		EmployeeFileImportService fileImportService = new EmployeeFileImportService();
		fileImportService.importFile(file);

		List<Employee> processedEmployees = fileImportService.getEntities();
		for (Employee employee : processedEmployees) {
			setEmployeeAuditingFields(employee, accountId, appUserId);
		}

		employeeDAO.save(processedEmployees);

		sendEmployeeEmails(processedEmployees, accountName);
	}

	private void setEmployeeAuditingFields(final Employee employee, int accountId, int appUserId) {
		employee.setAccountId(accountId);

		if (employee.getCreatedBy() == 0 || employee.getCreatedDate() == null) {
			EntityHelper.setCreateAuditFields(employee, appUserId, new Date());
		} else {
			EntityHelper.setUpdateAuditFields(employee, appUserId, new Date());
		}

		if (Strings.isEmpty(employee.getSlug())) {
			String hash = Strings.hashUrlSafe(employee.getAccountId() + employee.getEmail());
			employee.setSlug("EID-" + hash.substring(0, 8).toUpperCase());
		}
	}

	public byte[] exportEmployees(final int accountId) throws Exception {
		CSVWriter csvWriter = null;

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
			csvWriter = new CSVWriter(printWriter);

			addCsvHeader(csvWriter);

			List<Employee> employees = employeeEntityService.getEmployeesForAccount(accountId);
			for (Employee employee : employees) {
				csvWriter.writeNext(new String[]{
						employee.getFirstName(),
						employee.getLastName(),
						employee.getPositionName(),
						employee.getEmail(),
						employee.getPhone(),
						employee.getSlug()
				});
			}

			csvWriter.flush();
			return byteArrayOutputStream.toByteArray();
		} finally {
			safeCloseWriter(csvWriter);
		}
	}

	public byte[] exportTemplate() throws Exception {
		CSVWriter csvWriter = null;

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
			csvWriter = new CSVWriter(printWriter);

			addCsvHeader(csvWriter);
			csvWriter.flush();
			return byteArrayOutputStream.toByteArray();
		} finally {
			safeCloseWriter(csvWriter);
		}
	}

	private void addCsvHeader(CSVWriter csvWriter) {
		csvWriter.writeNext(new String[]{"First Name", "Last Name", "Title", "Email", "Phone", "Employee ID"});
	}

	private void safeCloseWriter(CSVWriter csvWriter) {
		try {
			if (csvWriter != null) {
				csvWriter.close();
			}
		} catch (Exception exception) {
			LOG.error("Exception closing resources", exception);
		}
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

	@Override
	public String download(final int accountId) throws Exception {
		byte[] output = null;
		try {
			output = employeeService.exportEmployees(accountId);
		} catch (Exception exception) {
			LOG.error("Error exporting employees for account {}\n{}", permissions.getAccountId(), exception);
			addActionError("Could not prepare download");
		}

		if (!hasActionErrors()) {
			fileContainer = new FileDownloadContainer.Builder()
					.contentType("text/csv")
					.contentDisposition("attachment; filename=" + filename())
					.fileInputStream(new ByteArrayInputStream(output)).build();
		}

		return FILE_DOWNLOAD;
	}

	public String template() throws Exception {
		byte[] output = null;
		try {
			output = employeeService.exportTemplate();
		} catch (Exception exception) {
			LOG.error("Error exporting template\n{}", exception);
			addActionError("Could not prepare download");
		}

		if (!hasActionErrors()) {
			fileContainer = new FileDownloadContainer.Builder()
					.contentType("text/csv")
					.contentDisposition("attachment; filename=EmployeeListTemplate.csv")
					.fileInputStream(new ByteArrayInputStream(output)).build();
		}

		return FILE_DOWNLOAD;
	}

	@Override
	protected String filename() {
		return "EmployeeList.csv";
	}

	@Override
	protected String invalidUploadRedirect() throws Exception {
		return setUrlForRedirect(urlBuilder().action("employee/import-export").build());
	}

	@Override
	protected String successfulUploadRedirect() throws Exception {
		return setUrlForRedirect(urlBuilder().action("employee").build());
	}

	public Map<String, Object> getContractor_employee_import() {
		return contractor_employee_import;
	}

	public void setContractor_employee_import(Map<String, Object> contractor_employee_import) {
		this.contractor_employee_import = contractor_employee_import;
	}

	public Map<String, Object> getContractor_employee_export() {
		return contractor_employee_export;
	}

	public void setContractor_employee_export(Map<String, Object> contractor_employee_export) {
		this.contractor_employee_export = contractor_employee_export;
	}

	private UrlBuilder urlBuilder() {
		if (urlBuilder == null) {
			urlBuilder = new UrlBuilder();
		}

		return urlBuilder;
	}

}
