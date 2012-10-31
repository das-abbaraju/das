package com.picsauditing.actions.contractors;

import com.picsauditing.jpa.entities.LowMedHigh;

public enum ProductAssessment {
	FAILURE_WORK_STOPPAGE(7660, LowMedHigh.High, LowMedHigh.Low), 
	DELIVERY_WORK_STOPPAGE(7661, LowMedHigh.High, LowMedHigh.Low), 
	RISK_ON_HEALTH_SAFETY(7679, true),
	// Old questions
	UTILIZED_IN_CRITICAL_PROCESSES(9798, LowMedHigh.High, LowMedHigh.Low), 
	FAILURE_BODILY_INJURY_ILLNESS(7662, LowMedHigh.High, LowMedHigh.Low), 
	LIABILITY_INSURANCE(7663, LowMedHigh.High, LowMedHigh.Low), 
	SELF_RATING(7678, true);

	private int questionID;
	private boolean selfEvaluation = false;
	private LowMedHigh yes;
	private LowMedHigh no;

	ProductAssessment(int questionID, LowMedHigh yes, LowMedHigh no) {
		this.questionID = questionID;
		this.yes = yes;
		this.no = no;
	}

	ProductAssessment(int questionID, boolean selfEvaluation) {
		this.questionID = questionID;
		this.selfEvaluation = selfEvaluation;
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public boolean isSelfEvaluation() {
		return selfEvaluation;
	}

	public void setSelfEvaluation(boolean selfEvaluation) {
		this.selfEvaluation = selfEvaluation;
	}

	public LowMedHigh getYes() {
		return yes;
	}

	public void setYes(LowMedHigh yes) {
		this.yes = yes;
	}

	public LowMedHigh getNo() {
		return no;
	}

	public void setNo(LowMedHigh no) {
		this.no = no;
	}

	public static ProductAssessment getProductAssessment(int questionID) {
		for (ProductAssessment productAssessment : values()) {
			if (productAssessment.getQuestionID() == questionID) {
				return productAssessment;
			}
		}

		return null;
	}

	public static boolean isSelfAssessment(int questionID) {
		for (ProductAssessment productAssessment : values()) {
			if (productAssessment.getQuestionID() == questionID) {
				return productAssessment.isSelfEvaluation();
			}
		}

		return false;
	}
}
