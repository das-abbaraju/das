package com.picsauditing.service.importfile;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractFileImportService<E> {
	protected List<E> entities = new ArrayList<>();
	protected List<String> errors = new ArrayList<>();

	public final void importFile(File file) throws Exception {
		validateFile(file);

		CSVReader reader = null;
		try {
			reader = getCsvReader(file);

			String[] lineOfFile;
			int rowCounter = 0;
			while ((lineOfFile = reader.readNext()) != null) {
				rowCounter++;

				if (lineIsValid(lineOfFile)) {
					entities.add(buildEntity(lineOfFile, rowCounter));
				} else {
					errors.add(buildErrorMessage(lineOfFile, rowCounter));
				}
			}
		} finally {
			safeCloseReader(reader);
		}
	}

	public final List<E> getEntities() {
		return Collections.unmodifiableList(entities);
	}

	public final List<String> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	private void safeCloseReader(final CSVReader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
			// Ignore me!
		}
	}

	protected abstract void validateFile(final File file) throws InvalidFileFormatException;

	protected abstract CSVReader getCsvReader(final File file) throws Exception;

	protected abstract boolean lineIsValid(final String[] lineOfFile);

	protected abstract E buildEntity(final String[] lineOfFile, int rowCounter);

	protected abstract String buildErrorMessage(final String[] lineOfFile, int rowCounter);
}
