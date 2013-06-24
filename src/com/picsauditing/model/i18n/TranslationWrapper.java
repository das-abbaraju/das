package com.picsauditing.model.i18n;

public class TranslationWrapper {

	private String key;
	private String translation;
	private String locale;
	private int createdBy;
	private int updatedBy;

	public String getKey() {
		return key;
	}

	public String getTranslation() {
		return translation;
	}

	public String getLocale() {
		return locale;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public static class Builder {

		private String key;
		private String translation;
		private String locale;
		private int createdBy;
		private int updatedBy;

		public Builder key(String key) {
			this.key = key;
			return this;
		}

		public Builder translation(String translation) {
			this.translation = translation;
			return this;
		}

		public Builder locale(String locale) {
			this.locale = locale;
			return this;
		}

		public Builder createdBy(int createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		public Builder updatedBy(int updatedBy) {
			this.updatedBy = updatedBy;
			return this;
		}

		public TranslationWrapper build() {
			TranslationWrapper translationWrapper = new TranslationWrapper();
			translationWrapper.key = key;
			translationWrapper.translation = translation;
			translationWrapper.locale = locale;
			translationWrapper.createdBy = createdBy;
			translationWrapper.updatedBy = updatedBy;
			return translationWrapper;
		}
	}

}
