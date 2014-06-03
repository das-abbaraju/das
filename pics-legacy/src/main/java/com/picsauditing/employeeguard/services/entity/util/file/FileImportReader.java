package com.picsauditing.employeeguard.services.entity.util.file;

import java.io.File;

public interface FileImportReader {

	boolean isValidFileType(File file);

	void open(File file) throws FileImportReaderException;

	String[] readLine() throws FileImportReaderException;

	void close();

}
