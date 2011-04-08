package com.picsauditing.actions.report;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAnnualAddendum extends ReportContractorAuditOperator {
	public ReportAnnualAddendum(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql.addWhere("ca.auditTypeID = 11");
		
		sql.addGroupBy("c.id");
		
		String auditFor = Strings.implodeForDB(getFilter().getAuditFor(), ",");
		if (!Strings.isEmpty(auditFor))
			sql.addWhere("ca.auditFor IN ("+ auditFor + ")");
		
		if (filterOn(getFilter().getShaType())) {
			sql.addWhere("os.SHAType = '"+ getFilter().getShaType() +"'");
			if(getFilter().getShaType().equals(OshaType.MSHA) || getFilter().getShaType().equals(OshaType.COHS)) {
				getFilter().setVerifiedAnnualUpdate(0);
			}
		}
		
		if(filterOn(getFilter().getShaLocation())) {
			sql.addWhere("os.location = '" + getFilter().getShaLocation() + "'");
		}
		
		getFilter().setShowVerifiedAnnualUpdates(true);

		getFilter().setShowAddress(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowCreatedDate(false);
		getFilter().setPendingPqfAnnualUpdate(false);
		
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
