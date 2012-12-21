package com.picsauditing.actions.contractors.risk;

import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public enum ProductAssessment implements RiskAssessment {
	FAILURE_WORK_STOPPAGE(7660, LowMedHigh.High, LowMedHigh.Low), //
	DELIVERY_WORK_STOPPAGE(7661, LowMedHigh.High, LowMedHigh.Low), //
	RISK_ON_HEALTH_SAFETY(7679, true),
	// Old questions
	@Deprecated
	UTILIZED_IN_CRITICAL_PROCESSES(9798, LowMedHigh.High, LowMedHigh.Low), //
	@Deprecated
	FAILURE_BODILY_INJURY_ILLNESS(7662, LowMedHigh.High, LowMedHigh.Low), //
	@Deprecated
	LIABILITY_INSURANCE(7663, LowMedHigh.High, LowMedHigh.Low), //
	@Deprecated
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

	public boolean isSelfEvaluation() {
		return selfEvaluation;
	}

	public LowMedHigh getYes() {
		return yes;
	}

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
