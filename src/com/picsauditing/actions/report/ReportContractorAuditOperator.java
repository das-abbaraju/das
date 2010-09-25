package com.picsauditing.actions.report;

import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.ReportFilterCAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorAuditOperator extends ReportContractorAudits {

	public ReportContractorAuditOperator() {
		super();
		orderByDefault = "cao.statusChangedDate DESC";
		filter = new ReportFilterCAO();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		sql.addJoin("LEFT JOIN contractor_audit_operator_workflow caow ON cao.id = caow.id");
		sql.addJoin("JOIN accounts caoAccount ON cao.opID = caoAccount.id");
		sql.addField("cao.id caoID");
		sql.addField("cao.status auditStatus");
		sql.addField("cao.statusChangedDate");
		sql.addField("caoAccount.name caoAccountName");

		if (permissions.isOperatorCorporate()) {
			String opIDs = permissions.getAccountIdString();
			if (permissions.isCorporate())
				opIDs = Strings.implode(permissions.getOperatorChildren());

			sql.addWhere("cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs
					+ "))");
		}

		getFilter().setShowOperator(false);
		getFilter().setShowTrade(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowWaitingOn(true);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowAddress(false);
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterCAO f = getFilter();

		if (f.getAuditStatus().length > 0) {
			String auditStatusList = Strings.implodeForDB(f.getAuditStatus(), ",");
			sql.addWhere("cao.status IN (" + auditStatusList + ")");
		}

		if (filterOn(f.getPercentComplete1())) {
			report
					.addFilter(new SelectFilter("percentComplete1", "cao.percentComplete >= '?'", f
							.getPercentComplete1()));
		}

		if (filterOn(f.getPercentComplete2())) {
			report
					.addFilter(new SelectFilter("percentComplete2", "cao.percentComplete < '?'", f
							.getPercentComplete2()));
		}

		if (getFilter().getAmBestRating() > 0 || getFilter().getAmBestClass() > 0) {
			sql.addJoin("JOIN pqfdata am ON am.auditid = ca.id");
			sql.addJoin("JOIN audit_question aq ON aq.id = am.questionid");
			sql.addJoin("JOIN ambest ambest ON ambest.naic = am.comment and ambest.companyName = am.answer");
			sql.addWhere("aq.questionType = 'AMBest'");
			if (getFilter().getAmBestRating() > 0)
				sql.addWhere("ambest.ratingcode = " + getFilter().getAmBestRating());
			if (getFilter().getAmBestClass() > 0)
				sql.addWhere("ambest.financialCode =" + getFilter().getAmBestClass());
		}
	}

	@Override
	public ReportFilterCAO getFilter() {
		return (ReportFilterCAO) filter;
	}

}
