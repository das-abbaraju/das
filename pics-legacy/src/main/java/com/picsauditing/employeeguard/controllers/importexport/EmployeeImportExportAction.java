package com.picsauditing.employeeguard.controllers.importexport;

import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.util.web.UrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class EmployeeImportExportAction extends ImportExportActionSupport {
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeImportExportAction.class);

	@Autowired
	private EmployeeService employeeService;

	private Map<String, Object> contractor_employee_import = new HashMap<>();
	private Map<String, Object> contractor_employee_export = new HashMap<>();

	@Override
	protected void processUpload() throws Exception {
		employeeService.importEmployees(upload, permissions.getAccountId(), permissions.getUserId());
	}

	// TODO: i18n
	@Override
	public String download() throws Exception {
		byte[] output = null;
		try {
			output = employeeService.exportEmployees(permissions.getAccountId());
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
		return setUrlForRedirect(new UrlBuilder().action("employee/import-export").build());
	}

	@Override
	protected String successfulUploadRedirect() throws Exception {
		return setUrlForRedirect(new UrlBuilder().action("employee").build());
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
}
