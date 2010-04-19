package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportCompetencyDetail extends ReportCompetencyByEmployee {

	public ReportCompetencyDetail() {
		orderByDefault = "name";
	}

	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN operator_competency oc ON jc.competencyID = oc.id");

		sql.addField("oc.label");
		sql.addField("IFNULL(ec.skilled, 0) skilledValue");
	}
}
