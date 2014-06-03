package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileImportService<E extends BaseEntity> {

	public UploadResult importFile(final FileImportCommand<E> fileImportCommand) throws FileImportReaderException {
		FileImportReader fileImportReader = fileImportCommand.getFileImportReader();
		File file = fileImportCommand.getFile();

		if (file == null || !fileImportReader.isValidFileType(file)) {
			return new UploadResult.Builder<E>().uploadError(true).errorMessage("Invalid file").build();
		}

		List<E> importedEntities = new ArrayList<>();

		FileRowMapper<E> fileRowMapper = fileImportCommand.getFileRowMapper();
		String[] firstLineOfFile = fileImportReader.readLine();

		if (!fileRowMapper.isValid(firstLineOfFile)) {
			return new UploadResult.Builder<E>().uploadError(true).errorMessage("Invalid file content").build();
		}

		if (!fileRowMapper.isHeader(firstLineOfFile)) {
			importedEntities.add(fileRowMapper.mapToEntity(firstLineOfFile));
		}

		String[] lineOfFile = null;
		try {
			while ((lineOfFile = fileImportReader.readLine()) != null) {
				if (fileRowMapper.isEmptyRow(lineOfFile)) {
					continue;
				}

				if (!fileRowMapper.isValid(lineOfFile)) {
					return new UploadResult.Builder<E>().uploadError(true).errorMessage("Invalid file content").build();
				}

				importedEntities.add(fileRowMapper.mapToEntity(lineOfFile));
			}
		} finally {
			fileImportReader.close();
		}

		EntityHelper.setCreateAuditFields(importedEntities, fileImportCommand.getEntityAuditInfo());

		return new UploadResult.Builder<E>().uploadError(false).importedEntities(importedEntities).build();
	}

}
