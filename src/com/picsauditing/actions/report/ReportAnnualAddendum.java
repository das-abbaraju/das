package com.picsauditing.actions.report;

import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAnnualAddendum extends ReportAccount {
	private ReportFilterAudit filter = new ReportFilterAudit();

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.id auditID");
		sql.addField("ca.auditFor");
		sql.addWhere("ca.auditTypeID = 11");
		
		// TODO: Does this need CAO filters?
		String auditStatusList = Strings.implodeForDB(getFilter().getAuditStatus(), ",");
		if (!Strings.isEmpty(auditStatusList))	
			sql.addWhere("ca.auditStatus IN (" + auditStatusList + ")");
		
		String auditFor = Strings.implodeForDB(getFilter().getAuditFor(), ",");
		if (!Strings.isEmpty(auditFor))
			sql.addWhere("ca.auditFor IN ("+ auditFor + ")");
		
		if (filterOn(getFilter().getShaType())) {
			sql.addWhere("os.SHAType = '"+ getFilter().getShaType() +"'");
		}
		
		if(filterOn(getFilter().getShaLocation())) {
			sql.addWhere("os.location = '" + getFilter().getShaLocation() + "'");
		}
		
		getFilter().setShowAuditFor(true);
		getFilter().setShowVerifiedAnnualUpdates(true);

		getFilter().setShowAddress(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditStatus(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowCreatedDate(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setPendingPqfAnnualUpdate(false);
	}

	@Override
	public ReportFilterAudit getFilter() {
		return filter;
	}
	
	public void setVerifiedAnnualUpdateFilter(String columnName) {
		if(getFilter().getVerifiedAnnualUpdate() > 0) {
			if(getFilter().getVerifiedAnnualUpdate() == 1) {
				sql.addWhere(columnName + " IS NOT NULL");
			}
			else
				sql.addWhere(columnName +" IS NULL");
		}
	}
}
