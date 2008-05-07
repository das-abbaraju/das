package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditQuestion;

public class ReportEMRRates extends ReportContractorAudits{
 
	public String execute() throws Exception {
		sql.addPQFQuestion(AuditQuestion.EMR07);
		sql.addPQFQuestion(AuditQuestion.EMR06);
		sql.addPQFQuestion(AuditQuestion.EMR05);
		sql.addField("q" + AuditQuestion.EMR07 + ".verified_answer AS verified1");
		sql.addField("q" + AuditQuestion.EMR06 + ".verified_answer AS verified2");
		sql.addField("q" + AuditQuestion.EMR05 + ".verified_answer AS verified2");
		return super.execute();
	}
}
