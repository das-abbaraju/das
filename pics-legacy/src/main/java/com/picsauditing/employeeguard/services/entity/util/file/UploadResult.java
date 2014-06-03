package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.employeeguard.entities.BaseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadResult<E extends BaseEntity> {

	private final boolean uploadError;
	private final String errorMessage;
	private final List<E> importedEntities;

	public UploadResult(final Builder builder) {
		this.uploadError = builder.uploadError;
		this.errorMessage = builder.errorMessage;
		this.importedEntities = builder.importedEntities;
	}

	public boolean isUploadError() {
		return uploadError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static class Builder<E> {

		private boolean uploadError;
		private String errorMessage;
		private List<E> importedEntities;

		public Builder uploadError(boolean uploadError) {
			this.uploadError = uploadError;
			return this;
		}

		public Builder errorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}

		public Builder importedEntities(final List<E> importedEntities) {
			this.importedEntities = Collections.unmodifiableList(new ArrayList<>(importedEntities));
			return this;
		}

		public UploadResult build() {
			return new UploadResult(this);
		}
	}
}
