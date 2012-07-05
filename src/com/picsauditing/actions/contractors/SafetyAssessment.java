package com.picsauditing.actions.contractors;

import com.picsauditing.jpa.entities.LowMedHigh;

public enum SafetyAssessment {
	CONDUCTED_FROM_OFFICE(12341, LowMedHigh.Low, LowMedHigh.Med),
	HAND_POWER_PNEUMATIC_TOOLS(12342, LowMedHigh.Med, LowMedHigh.Low),
	PERSONAL_PROTECTIVE_EQUIPMENT(12343, LowMedHigh.Med, LowMedHigh.Low),
	PERMIT_TO_WORK(12344, LowMedHigh.High, LowMedHigh.Med),
	MOBILE_EQUIPMENT(12345, LowMedHigh.High, LowMedHigh.Med),
	PERFORMS_HIGH_RISK(12346, LowMedHigh.High, LowMedHigh.Med),
	LEVEL_OF_HAZARD_EXPOSURE(12347, true),
	// Old questions
	ALL_SERVICES_IN_OFFICE(2443, LowMedHigh.Low, LowMedHigh.Med),
	MECHANICAL_SERVICES(3793, LowMedHigh.High, LowMedHigh.Low),
	EQUIPMENT_MACHINERY(2442, LowMedHigh.High, LowMedHigh.Low),
	FALL_PROTECTION(2445, LowMedHigh.High, LowMedHigh.Low),
	RISK_PROFILE(2444, true);
	
	private int questionID;
	private boolean selfEvaluation = false;
	private LowMedHigh yes;
	private LowMedHigh no;
	
	SafetyAssessment(int questionID, LowMedHigh yes, LowMedHigh no) {
		this.questionID = questionID;
		this.yes = yes;
		this.no = no;
	}
	
	SafetyAssessment(int questionID, boolean selfEvaluation) {
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
	
	public static SafetyAssessment getSafetyAssessment(int questionID) {
		for (SafetyAssessment safetyAssessment : values()) {
			if (safetyAssessment.getQuestionID() == questionID) {
				return safetyAssessment;
			}
		}
		
		return null;
	}
	
	public static boolean isSelfAssessment(int questionID) {
		for (SafetyAssessment safetyAssessment : values()) {
			if (safetyAssessment.getQuestionID() == questionID) {
				return safetyAssessment.isSelfEvaluation();
			}
		}
		
		return false;
	}
}