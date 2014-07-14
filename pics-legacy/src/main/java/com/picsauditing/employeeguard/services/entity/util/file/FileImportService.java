package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.employeeguard.msgbundle.EGI18n;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileImportService<E extends BaseEntity> {

	public UploadResult importFile(final FileImportCommand<E> fileImportCommand) {
		FileImportReader fileImportReader = fileImportCommand.getFileImportReader();
		File file = fileImportCommand.getFile();

		if (!validateFileType(fileImportReader, file, fileImportCommand.getFilename())) {
			return new UploadResult.Builder<E>()
					.uploadError(true)
					.errorMessage(EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.INVALID_FILE"))
					.build();
		}

		return processFile(file, fileImportCommand, fileImportReader);
	}

	private UploadResult processFile(final File file,
									 final FileImportCommand<E> fileImportCommand,
									 final FileImportReader fileImportReader) {
		List<E> importedEntities;
		try {
			importedEntities = parseEntitiesInFile(file, fileImportReader, fileImportCommand.getFileRowMapper());
		} catch (Exception e) {
			return new UploadResult.Builder<E>().uploadError(true).errorMessage(e.getMessage()).build();
		}

		return new UploadResult.Builder<E>().uploadError(false).importedEntities(importedEntities).build();
	}

	private boolean validateFileType(final FileImportReader fileImportReader, final File file, final String filename) {
		if (file == null || !fileImportReader.isValidFileType(filename)) {
			return false;
		}

		return true;
	}

	private List<E> parseEntitiesInFile(final File file,
										final FileImportReader fileImportReader,
										final FileRowMapper<E> fileRowMapper)
			throws FileImportReaderException {

		List<E> importedEntities = new ArrayList<>();

		fileImportReader.open(file);
		String[] lineOfFile = fileImportReader.readLine();
		if (!fileRowMapper.isHeader(lineOfFile) && !fileRowMapper.isValid(lineOfFile)) {
			throw new FileImportReaderException(
					EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.USE_TEMPLATE"));
		}

		if (!fileRowMapper.isHeader(lineOfFile)) {
			importedEntities.add(fileRowMapper.mapToEntity(lineOfFile));
		}

		try {
			while ((lineOfFile = fileImportReader.readLine()) != null) {
				if (fileRowMapper.isEmptyRow(lineOfFile)) {
					continue;
				}

				if (!fileRowMapper.isValid(lineOfFile)) {
					throw new FileImportReaderException(
							EGI18n.getTextFromResourceBundle("EMPLOYEE.IMPORT_EXPORT.REQUIRED_FIELDS_NOT_PROVIDED"));
				}

				importedEntities.add(fileRowMapper.mapToEntity(lineOfFile));
			}
		} finally {
			fileImportReader.close();
		}

		return importedEntities;
	}

}
