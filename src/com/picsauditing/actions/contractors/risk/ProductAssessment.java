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
	private LowMedHigh riskRankingForAnswerYes;
	private LowMedHigh riskRankingForAnswerNo;

	private ProductAssessment(int questionID, LowMedHigh riskRankingForAnswerYes, LowMedHigh riskRankingForAnswerNo) {
		this.questionID = questionID;
		this.riskRankingForAnswerYes = riskRankingForAnswerYes;
		this.riskRankingForAnswerNo = riskRankingForAnswerNo;
	}

	private ProductAssessment(int questionID, boolean selfEvaluation) {
		this.questionID = questionID;
		this.selfEvaluation = selfEvaluation;
	}

    @Override
	public int getQuestionID() {
		return questionID;
	}

    @Override
	public boolean isQuestionSelfEvaluation() {
		return selfEvaluation;
	}

    @Override
	public LowMedHigh getRiskRankingForAnswerYes() {
		return riskRankingForAnswerYes;
	}

    @Override
	public LowMedHigh getRiskRankingForAnswerNo() {
		return riskRankingForAnswerNo;
	}

	@Override
	public LowMedHigh getRiskLevelBasedOn(String answer) {
		if (!Strings.isEmpty(answer.trim())) {
			if (isQuestionSelfEvaluation()) {
				return LowMedHigh.parseLowMedHigh(answer);
			}

			if (YesNo.valueOf(answer) == YesNo.Yes) {
				return getRiskRankingForAnswerYes();
			} else if (YesNo.valueOf(answer) == YesNo.No) {
				return getRiskRankingForAnswerNo();
			}
		}

		return LowMedHigh.None;
	}

    public static ProductAssessment findByQuestionID(int questionID) {
        for (ProductAssessment assessment : values()) {
            if (assessment.getQuestionID() == questionID) {
                return assessment;
            }
        }

        return null;
    }
}
