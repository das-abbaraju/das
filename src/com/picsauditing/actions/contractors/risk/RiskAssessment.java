package com.picsauditing.actions.contractors.risk;

import com.picsauditing.jpa.entities.LowMedHigh;

public interface RiskAssessment {
	public abstract int getQuestionID();

	public abstract boolean isQuestionSelfEvaluation();

	public abstract LowMedHigh getRiskRankingForAnswerYes();

	public abstract LowMedHigh getRiskRankingForAnswerNo();

	public abstract LowMedHigh getRiskLevelBasedOn(String answer);
}