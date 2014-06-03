package com.picsauditing.employeeguard.services.entity.util.file;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;

public class CsvFileImportReader implements FileImportReader {

	private CSVReader csvReader;

	@Override
	public boolean isValidFileType(File file) {
		return file.getName().toLowerCase().endsWith("csv");
	}

	@Override
	public void open(final File file) throws FileImportReaderException {
		try {
			csvReader = new CSVReader(new FileReader(file));
		} catch (Exception e) {
			throw new FileImportReaderException(e.getMessage());
		}
	}

	@Override
	public String[] readLine() throws FileImportReaderException {
		try {
			return csvReader.readNext();
		} catch (Exception e) {
			close();
			throw new FileImportReaderException(e.getMessage());
		}
	}

	@Override
	public void close() {
		try {
			if (csvReader != null) {
				csvReader.close();
			}
		} catch (Exception ignore) {
		}
	}
}
