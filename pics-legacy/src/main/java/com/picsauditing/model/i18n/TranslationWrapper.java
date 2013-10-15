package com.picsauditing.model.i18n;

import com.picsauditing.jpa.entities.TranslationQualityRating;

public class TranslationWrapper {

	private int keyID;
	private String key;
	private String translation;
	private String locale;
	private String requestedLocale;
    private TranslationQualityRating qualityRating;
    private boolean retrievedByWildcard = false;
	private int createdBy;
	private int updatedBy;

    public int getKeyID() {
        return keyID;
    }

    public String getKey() {
		return key;
	}

	public String getTranslation() {
		return translation;
	}

	public String getLocale() {
		return locale;
	}

    public String getRequestedLocale() {
        return requestedLocale;
    }

    public TranslationQualityRating getQualityRating() {
        return qualityRating;
    }

    public boolean isRetrievedByWildcard() {
        return retrievedByWildcard;
    }

    public int getCreatedBy() {
		return createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public static class Builder {
        private int keyID;
		private String key;
		private String translation;
		private String locale;
        private String requestedLocale;
        private TranslationQualityRating qualityRating;
        private boolean retrievedByWildcard = false;
		private int createdBy;
		private int updatedBy;

        public Builder() {

        }

        public Builder(TranslationWrapper seed) {
            keyID = seed.getKeyID();
            key = seed.getKey();
            translation = seed.getTranslation();
            locale = seed.getLocale();
            requestedLocale = seed.getRequestedLocale();
            qualityRating = seed.getQualityRating();
            retrievedByWildcard = seed.isRetrievedByWildcard();
            createdBy = seed.getCreatedBy();
            updatedBy = seed.getUpdatedBy();
        }

        public Builder keyID(int keyID) {
            this.keyID = keyID;
            return this;
        }

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

        public Builder requestedLocale(String requestedLocale) {
            this.requestedLocale = requestedLocale;
            return this;
        }

        public Builder qualityRating(TranslationQualityRating qualityRating) {
            this.qualityRating = qualityRating;
            return this;
        }

        public Builder retrievedByWildcard(boolean retrievedByWildcard) {
            this.retrievedByWildcard = retrievedByWildcard;
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
			translationWrapper.keyID = keyID;
			translationWrapper.key = key;
			translationWrapper.translation = translation;
			translationWrapper.locale = locale;
			translationWrapper.requestedLocale = requestedLocale;
            translationWrapper.qualityRating = qualityRating;
            translationWrapper.retrievedByWildcard = retrievedByWildcard;
			translationWrapper.createdBy = createdBy;
			translationWrapper.updatedBy = updatedBy;
			return translationWrapper;
		}
	}

}
