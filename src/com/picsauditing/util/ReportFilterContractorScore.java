package com.picsauditing.util;

@SuppressWarnings("serial")
public class ReportFilterContractorScore extends ReportFilterCAO {
	// Show
	protected boolean showScoreRange = true;

	// Fields
	protected float scoreMin = 0;
	protected float scoreMax = 100;

	// Getters and setters - Show
	public boolean isShowScoreRange() {
		return showScoreRange;
	}

	public void setShowScoreRange(boolean showScoreRange) {
		this.showScoreRange = showScoreRange;
	}

	// Getters and setters - Fields
	public float getScoreMin() {
		return scoreMin;
	}

	public void setScoreMin(float scoreMin) {
		this.scoreMin = scoreMin;
	}

	public float getScoreMax() {
		return scoreMax;
	}

	public void setScoreMax(float scoreMax) {
		this.scoreMax = scoreMax;
	}
}
