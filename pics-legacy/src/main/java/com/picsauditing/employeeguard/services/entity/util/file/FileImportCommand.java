package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;
import com.picsauditing.employeeguard.models.EntityAuditInfo;

import java.io.File;

public class FileImportCommand<E extends BaseEntity> {

	private final File file;
	private final EntityAuditInfo entityAuditInfo;
	private final FileRowMapper<E> fileRowMapper;
	private final FileImportReader fileImportReader;

	public FileImportCommand(Builder<E> builder) {
		this.file = builder.file;
		this.entityAuditInfo = builder.entityAuditInfo;
		this.fileRowMapper = builder.fileRowMapper;
		this.fileImportReader = builder.fileImportReader;
	}

	public File getFile() {
		return file;
	}

	public EntityAuditInfo getEntityAuditInfo() {
		return entityAuditInfo;
	}

	public FileRowMapper<E> getFileRowMapper() {
		return fileRowMapper;
	}

	public FileImportReader getFileImportReader() {
		return fileImportReader;
	}

	public static class Builder<E extends BaseEntity> {

		private File file;
		private EntityAuditInfo entityAuditInfo;
		private FileRowMapper<E> fileRowMapper;
		private FileImportReader fileImportReader;

		public Builder setFile(File file) {
			this.file = file;
			return this;
		}

		public Builder entityAuditInfo(EntityAuditInfo entityAuditInfo) {
			this.entityAuditInfo = entityAuditInfo;
			return this;
		}

		public Builder fileRowMapper(FileRowMapper<E> fileRowMapper) {
			this.fileRowMapper = fileRowMapper;
			return this;
		}

		public Builder fileImportReader(FileImportReader fileImportReader) {
			this.fileImportReader = fileImportReader;
			return this;
		}

		public FileImportCommand<E> build() {
			return new FileImportCommand<>(this);
		}
	}
}
