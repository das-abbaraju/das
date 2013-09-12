package com.picsauditing.actions.contractors.risk;

import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public enum SafetyAssessment implements RiskAssessment {
	CONDUCTED_FROM_OFFICE(12341, LowMedHigh.Low, LowMedHigh.Med), //
	HAND_POWER_PNEUMATIC_TOOLS(12342, LowMedHigh.Med, LowMedHigh.Low), //
	PERSONAL_PROTECTIVE_EQUIPMENT(12343, LowMedHigh.Med, LowMedHigh.Low), //
	PERMIT_TO_WORK(12344, LowMedHigh.High, LowMedHigh.Med), //
	MOBILE_EQUIPMENT(12345, LowMedHigh.High, LowMedHigh.Med), //
	PERFORMS_HIGH_RISK(12346, LowMedHigh.High, LowMedHigh.Med), //
	LEVEL_OF_HAZARD_EXPOSURE(12347, true),
	// Old questions
	@Deprecated
	ALL_SERVICES_IN_OFFICE(2443, LowMedHigh.Low, LowMedHigh.Med), //
	@Deprecated
	MECHANICAL_SERVICES(3793, LowMedHigh.High, LowMedHigh.Low), //
	@Deprecated
	EQUIPMENT_MACHINERY(2442, LowMedHigh.High, LowMedHigh.Low), //
	@Deprecated
	FALL_PROTECTION(2445, LowMedHigh.High, LowMedHigh.Low), //
	@Deprecated
	RISK_PROFILE(2444, true);

	private int questionID;
	private boolean selfEvaluation = false;
	private LowMedHigh yes;
	private LowMedHigh no;

	private SafetyAssessment(int questionID, LowMedHigh yes, LowMedHigh no) {
		this.questionID = questionID;
		this.yes = yes;
		this.no = no;
	}

	private SafetyAssessment(int questionID, boolean selfEvaluation) {
		this.questionID = questionID;
		this.selfEvaluation = selfEvaluation;
	}

	@Override
	public int getQuestionID() {
		return questionID;
	}

	@Override
	public boolean isSelfEvaluation() {
		return selfEvaluation;
	}

	@Override
	public LowMedHigh getYes() {
		return yes;
	}

	@Override
	public LowMedHigh getNo() {
		return no;
	}

	@Override
	public LowMedHigh getRiskLevel(String answer) {
		if (!Strings.isEmpty(answer.trim())) {
			if (isSelfEvaluation()) {
				return LowMedHigh.parseLowMedHigh(answer);
			}

			if (YesNo.valueOf(answer) == YesNo.Yes) {
				return getYes();
			} else if (YesNo.valueOf(answer) == YesNo.No) {
				return getNo();
			}
		}

		return LowMedHigh.None;
	}
}