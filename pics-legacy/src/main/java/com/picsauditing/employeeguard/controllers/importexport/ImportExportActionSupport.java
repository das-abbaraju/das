package com.picsauditing.employeeguard.controllers.importexport;

import au.com.bytecode.opencsv.CSVReader;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public abstract class ImportExportActionSupport extends PicsRestActionSupport {

	private static final Logger LOG = LoggerFactory.getLogger(ImportExportActionSupport.class);

	protected FileFormat type;
	protected File upload;
	protected String uploadFileName;
	protected List<String[]> rows;

	public String importExport() {
		return "import-export";
	}

	// TODO: i18n
	public String upload() throws Exception {
		if (uploadIsValid()) {
			try {
				processUpload();
			} catch (Exception exception) {
				LOG.error("Error parsing {}", new Object[]{uploadFileName, exception});
				addActionError("Error parsing " + uploadFileName);
			}
		} else {
			// TODO: I18n
			addActionError("Missing file or non CSV file uploaded");
			return invalidUploadRedirect();
		}

		return successfulUploadRedirect();
	}

	protected abstract String invalidUploadRedirect() throws Exception;

	protected abstract String successfulUploadRedirect() throws Exception;

	protected boolean uploadIsValid() {
		return upload != null &&
				upload.length() > 0 &&
				Strings.isNotEmpty(uploadFileName) &&
				uploadFileName.toLowerCase().endsWith("csv");
	}

	public abstract String download() throws Exception;

	protected String filename() {
		return this.getClass().getSimpleName().replace("Action", "") + ".csv";
	}

	protected void processUpload() throws Exception {
		CSVReader reader = new CSVReader(new FileReader(upload));
		rows = reader.readAll();
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