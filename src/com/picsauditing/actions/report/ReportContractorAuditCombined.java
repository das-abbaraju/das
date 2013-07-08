package com.picsauditing.actions.report;

import java.io.IOException;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.util.ReportFilterCAOW;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorAuditCombined extends ReportContractorAudits {
	protected ReportFilterCAOW filter = new ReportFilterCAOW();
	
	public ReportContractorAuditCombined () {
		filter.setShowCaowDetail(true);
	}
	
	@Override
	protected void buildQuery() {
		super.buildQuery();
		
		Boolean detailLevel = filter.getCaowDetailLevel();
		
		sql = new SelectContractorAudit();
		sql.setType(SelectAccount.Type.Contractor);
		if (!skipPermissions)
			sql.setPermissions(permissions);
		
		addFilterToSQL();
		
		sql.addField("ca.creationDate createdDate");
		sql.addField("ca.expiresDate");
		sql.addField("ca.scheduledDate");
		sql.addField("ca.assignedDate");
		sql.addField("ca.auditLocation");
		sql.addField("ca.auditorID");
		sql.addField("ca.auditFor");

		sql.addField("atype.isScheduled");
		sql.addField("atype.hasAuditor");
		sql.addField("atype.scoreable");
		sql.addField("ca.score as auditScore");

		sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorID");
		sql.addField("auditor.name auditor_name");

		sql.addJoin("JOIN users contact ON contact.id = a.contactID");
		sql.addField("contact.name AS contactname");
		sql.addField("contact.phone AS contactphone");
		sql.addField("contact.email AS contactemail");

		if (auditTypeClass != null) {
			if (auditTypeClass == AuditTypeClass.Audit) {
				sql.addWhere("atype.classType in ( 'Audit', 'Review', 'IM', 'PQF' ) ");
			} else {
				sql.addWhere("atype.classType = '" + auditTypeClass.toString() + "'");
			}
		}

		if (!permissions.isPicsEmployee())
			getFilter().setShowAuditor(true);

		if (permissions.isPicsEmployee())
			getFilter().setShowClosingAuditor(true);

		getFilter().setShowAuditFor(true);
		getFilter().setShowExpiredDate(true);
		
		if (detailLevel == null) {
			//similar to ReportContractorAudits / ReportAuditList / AuditListCompressed

		} else {
			//similar to ReportContractorAuditOperator / ReportCAOList / Audit List
			
			sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
			sql.addWhere("cao.visible = 1");
			
			sql.addJoin("JOIN accounts caoAccount ON cao.opID = caoAccount.id");
			sql.addField("cao.id caoID");
			sql.addField("cao.status auditStatus");
			sql.addField("cao.statusChangedDate");
			sql.addField("caoAccount.name caoAccountName");
			sql.addWhere("a.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo'" : "")
					+ ")");

			if (permissions.isOperatorCorporate()) {
				String opIDs = permissions.getAccountIdString();
				if (permissions.isCorporate())
					opIDs = Strings.implode(permissions.getOperatorChildren());

				sql.addWhere("cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs
						+ "))");
			}

			if (getFilter().isShowAuditStatus() && getFilter().getAuditStatus() == null)
				getFilter().setAuditStatus(AuditStatus.activeStatusesBeyondPending());

			getFilter().setShowOperator(false);
			getFilter().setShowTrade(false);
			getFilter().setShowLocation(false);
			getFilter().setShowTaxID(false);
			getFilter().setShowWaitingOn(true);

			if (detailLevel == true) {
				//similar to ReportContractorAuditOperatorWorkflow / ReportCAOByStatusList / Audit List by Status
				
				sql.addField("caow.status caowStatus");
				sql.addJoin("JOIN contractor_audit_operator_workflow caow ON cao.id = caow.caoID");
				
				getFilter().setShowAuditStatus(false);
				getFilter().setShowCaoStatusChangedDate(false);
			}
		} 
	}
	
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterCAOW f = getFilter();
		Boolean detailLevel = filter.getCaowDetailLevel();
		
		if (detailLevel == null) {
			
		} else {
			String auditStatusList = Strings.implodeForDB(f.getAuditStatus(), ",");
			if (filterOn(auditStatusList)) {
				sql.addWhere("cao.status IN (" + auditStatusList + ")");
			}
	
			if (filterOn(f.getPercentComplete1())) {
				report.addFilter(new SelectFilter("percentComplete1", "cao.percentComplete >= '?'", f.getPercentComplete1()));
			}
	
			if (filterOn(f.getPercentComplete2())) {
				report.addFilter(new SelectFilter("percentComplete2", "cao.percentComplete < '?'", f.getPercentComplete2()));
			}
	
			if (filterOn(f.getPercentVerified1())) {
				report.addFilter(new SelectFilter("percentVerified1", "cao.percentVerified >= '?'", f.getPercentVerified1()));
			}
	
			if (filterOn(f.getPercentVerified2())) {
				report.addFilter(new SelectFilter("percentVerified2", "cao.percentVerified < '?'", f.getPercentVerified2()));
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
	
			if (filterOn(f.getCaoOperator())) {
				sql.addWhere("cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN ("
						+ Strings.implode(f.getCaoOperator()) + "))");
			}
			
			if (detailLevel == true) {
				
				String caowStatusList = Strings.implodeForDB(f.getCaowStatus(), ",");
				if (filterOn(caowStatusList)) {
					sql.addWhere("caow.status IN (" + caowStatusList + ")");
				}
				
				if (filterOn(f.getCaowUpdateDate1())) {
					report.addFilter(new SelectFilterDate("caowUpdateDate1", "caow.updateDate >= '?'", DateBean
							.format(f.getCaowUpdateDate1(), "M/d/yy")));
				}

				if (filterOn(f.getCaowUpdateDate2())) {
					report.addFilter(new SelectFilterDate("caowUpdateDate2", "caow.updateDate < '?'", DateBean.format(
							f.getCaowUpdateDate2(), "M/d/yy")));
				}
			}
		} 
	}
	
	@Override
	protected String returnResult() throws IOException {
		if (download) {
			return null;
		}

		return SUCCESS;
	}

	@Override
	public ReportFilterCAOW getFilter() {
		return filter;
	}

	

}
