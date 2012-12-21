package com.picsauditing.actions.contractors.risk;

import com.picsauditing.jpa.entities.LowMedHigh;

public interface RiskAssessment {
	public abstract int getQuestionID();

	public abstract boolean isSelfEvaluation();

	public abstract LowMedHigh getYes();

	public abstract LowMedHigh getNo();

	public abstract LowMedHigh getRiskLevel(String answer);
}