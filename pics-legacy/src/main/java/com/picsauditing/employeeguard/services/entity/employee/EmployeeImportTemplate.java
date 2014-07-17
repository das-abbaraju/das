package com.picsauditing.employeeguard.services.entity.employee;

import au.com.bytecode.opencsv.CSVWriter;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

class EmployeeImportTemplate {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeImportExportProcess.class);

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
		csvWriter.writeNext(new String[] {
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.FIRST_NAME"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.LAST_NAME"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.TITLE"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.EMAIL"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.PHONE"),
				EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.TEMPLATE.HEADER.UNIQUE_ID")
		});
	}
}
