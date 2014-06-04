package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileImportService<E extends BaseEntity> {

	public UploadResult importFile(final FileImportCommand<E> fileImportCommand) {
		FileImportReader fileImportReader = fileImportCommand.getFileImportReader();
		File file = fileImportCommand.getFile();

		if (!validateFileType(fileImportReader, file)) {
			return new UploadResult.Builder<E>().uploadError(true).errorMessage("Invalid file").build();
		}

		return processFile(fileImportCommand, fileImportReader);
	}

	private UploadResult processFile(final FileImportCommand<E> fileImportCommand,
									 final FileImportReader fileImportReader) {
		List<E> importedEntities;
		try {
			importedEntities = parseEntitiesInFile(fileImportReader, fileImportCommand.getFileRowMapper());
		} catch (Exception e) {
			return new UploadResult.Builder<E>().uploadError(true).errorMessage(e.getMessage()).build();
		}

		return new UploadResult.Builder<E>().uploadError(false).importedEntities(importedEntities).build();
	}

	private boolean validateFileType(final FileImportReader fileImportReader, final File file) {
		if (file == null || !fileImportReader.isValidFileType(file)) {
			return false;
		}

		return true;
	}

	private List<E> parseEntitiesInFile(final FileImportReader fileImportReader, final FileRowMapper<E> fileRowMapper)
			throws FileImportReaderException {

		List<E> importedEntities = new ArrayList<>();

		String[] lineOfFile = fileImportReader.readLine();
		if (!fileRowMapper.isHeader(lineOfFile) && !fileRowMapper.isValid(lineOfFile)) {
			throw new FileImportReaderException("First row of the file is neither a header nor valid data format");
		}

		importedEntities.add(fileRowMapper.mapToEntity(lineOfFile));
		try {
			while ((lineOfFile = fileImportReader.readLine()) != null) {
				if (fileRowMapper.isEmptyRow(lineOfFile)) {
					continue;
				}

				if (!fileRowMapper.isValid(lineOfFile)) {
					throw new FileImportReaderException("Row in file is in invalid format");
				}

				importedEntities.add(fileRowMapper.mapToEntity(lineOfFile));
			}
		} finally {
			fileImportReader.close();
		}

		return importedEntities;
	}

}
