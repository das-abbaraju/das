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
	private LowMedHigh yes;
	private LowMedHigh no;

	private TransportationAssessment(int questionID, LowMedHigh yes, LowMedHigh no) {
		this.questionID = questionID;
		this.yes = yes;
		this.no = no;
	}

	@Override
	public int getQuestionID() {
		return questionID;
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
	public boolean isSelfEvaluation() {
		return false;
	}

	@Override
	public LowMedHigh getRiskLevel(String answer) {
		if (!Strings.isEmpty(answer.trim())) {
			if (YesNo.valueOf(answer) == YesNo.Yes) {
				return getYes();
			} else if (YesNo.valueOf(answer) == YesNo.No) {
				return getNo();
			}
		}

		return LowMedHigh.None;
	}
}
