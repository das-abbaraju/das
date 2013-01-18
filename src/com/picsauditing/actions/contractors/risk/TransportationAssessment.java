package com.picsauditing.actions.contractors.risk;

import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

public enum TransportationAssessment implements RiskAssessment {
	DELIVER_TO_WAREHOUSE_OR_ADMINISTRATIVE_OFFICE(14924, LowMedHigh.Low, LowMedHigh.Med), //
	OVERSIZED_OR_PERMIT_REQUIRED_LOADS(14925, LowMedHigh.Med, LowMedHigh.Low), //
	LOAD_OFFLOAD_AT_CLIENT_FACILITY(14926, LowMedHigh.High, LowMedHigh.Low), //
	TRANSPORT_AND_OFFLOAD_HAZARDOUS_MATERIALS(14927, LowMedHigh.High, LowMedHigh.Low);

	private int questionID;
	private LowMedHigh riskRankingForAnswerYes;
	private LowMedHigh riskRankingForAnswerNo;

	private TransportationAssessment(int questionID, LowMedHigh riskRankingForAnswerYes, LowMedHigh riskRankingForAnswerNo) {
		this.questionID = questionID;
		this.riskRankingForAnswerYes = riskRankingForAnswerYes;
		this.riskRankingForAnswerNo = riskRankingForAnswerNo;
	}

	@Override
	public int getQuestionID() {
		return questionID;
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
	public boolean isQuestionSelfEvaluation() {
		return false;
	}

	@Override
	public LowMedHigh getRiskLevelBasedOn(String answer) {
		if (!Strings.isEmpty(answer.trim())) {
			if (YesNo.valueOf(answer) == YesNo.Yes) {
				return getRiskRankingForAnswerYes();
			} else if (YesNo.valueOf(answer) == YesNo.No) {
				return getRiskRankingForAnswerNo();
			}
		}

		return LowMedHigh.None;
	}

    public static TransportationAssessment findByQuestionID(int questionID) {
        for (TransportationAssessment assessment : values()) {
            if (assessment.getQuestionID() == questionID) {
                return assessment;
            }
        }

        return null;
    }
}
