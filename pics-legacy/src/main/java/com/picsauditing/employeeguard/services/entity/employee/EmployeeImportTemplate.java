package com.picsauditing.employeeguard.services.entity.employee;

import au.com.bytecode.opencsv.CSVWriter;
import com.picsauditing.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

class EmployeeImportTemplate {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeImportExportProcess.class);

	static final String[] IMPORT_FILE_HEADER = new String[]{"First Name", "Last Name", "Title", "Email",
			"Phone", "Employee ID"};

	public byte[] template() {
		CSVWriter csvWriter = null;
		ByteArrayOutputStream byteArrayOutputStream = null;

		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			csvWriter = new CSVWriter(new PrintWriter(byteArrayOutputStream));

			addHeader(csvWriter);
			csvWriter.flush();

			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			LOG.warn("Error while building employee export template", e);
			return null;
		} finally {
			FileUtils.safeClose(byteArrayOutputStream);
			FileUtils.safeClose(csvWriter);
		}
	}

	private void addHeader(final CSVWriter csvWriter) {
		csvWriter.writeNext(IMPORT_FILE_HEADER);
	}
}
