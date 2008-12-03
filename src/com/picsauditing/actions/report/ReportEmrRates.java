package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ReportEmrRates extends ReportAccount {
	protected int year = 2007;
	protected float minRate = 0;
	protected float maxRate = 100;

	public String execute() throws Exception {
		if(!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.EMRReport);
		sql.addJoin("JOIN contractor_audit pqf ON pqf.conID = a.id");
		sql.addJoin("JOIN pqfdata d ON d.auditID = pqf.auditID");
		sql.addField("d.answer");
		sql.addField("d.verifiedAnswer");
		sql.addWhere("pqf.auditStatus = 'Active' AND pqf.auditTypeID = 1");
		
		int questionID = 0;
		switch (year) {
		// This should probably go somewere else, so we can reuse it.
		case 2007:
			questionID = AuditQuestion.EMR07;
			break;
		case 2006:
			questionID = AuditQuestion.EMR06;
			break;
		case 2005:
			questionID = AuditQuestion.EMR05;
			break;
		}

		if (questionID == 0)
			return SUCCESS;

		sql.addWhere("d.questionID = " + questionID);
		sql.addWhere("d.verifiedAnswer >= " + minRate);
		sql.addWhere("d.verifiedAnswer < " + maxRate);
		sql.addWhere("d.verifiedAnswer > ''");

		forwardSingleResults = false;
		return super.execute();
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public float getMinRate() {
		return minRate;
	}

	public void setMinRate(float minRate) {
		this.minRate = minRate;
	}

	public float getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(float maxRate) {
		this.maxRate = maxRate;
	}
}
