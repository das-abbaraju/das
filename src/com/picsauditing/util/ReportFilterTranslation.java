package com.picsauditing.util;

import com.picsauditing.jpa.entities.TranslationQualityRating;

@SuppressWarnings("serial")
public class ReportFilterTranslation extends ReportFilter {
	private boolean showQualityRating = true;
	private boolean showKey = true;
	private boolean showRestrictToCurrentUser = true;

	private TranslationQualityRating[] qualityRating = new TranslationQualityRating[] { TranslationQualityRating.Good };
	private String key;
	private boolean restrictToCurrentUser = true;

	public boolean isShowQualityRating() {
		return showQualityRating;
	}

	public void setShowQualityRating(boolean showQualityRating) {
		this.showQualityRating = showQualityRating;
	}

	public boolean isShowKey() {
		return showKey;
	}

	public void setShowKey(boolean showKey) {
		this.showKey = showKey;
	}

	public boolean isShowRestrictToCurrentUser() {
		return showRestrictToCurrentUser;
	}

	public void setShowRestrictToCurrentUser(boolean showRestrictToCurrentUser) {
		this.showRestrictToCurrentUser = showRestrictToCurrentUser;
	}

	public TranslationQualityRating[] getQualityRating() {
		return qualityRating;
	}

	public void setQualityRating(TranslationQualityRating[] qualityRating) {
		this.qualityRating = qualityRating;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isRestrictToCurrentUser() {
		return restrictToCurrentUser;
	}

	public void setRestrictToCurrentUser(boolean restrictToCurrentUser) {
		this.restrictToCurrentUser = restrictToCurrentUser;
	}

	public TranslationQualityRating[] getQualityRatingList() {
		return TranslationQualityRating.values();
	}
}
