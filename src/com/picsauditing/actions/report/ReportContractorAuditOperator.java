package com.picsauditing.actions.report;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.ReportFilterCAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorAuditOperator extends ReportContractorAudits {

	protected AuditDataDAO auditDataDao;
	protected AuditQuestionDAO auditQuestionDao;
	protected OperatorAccountDAO operatorAccountDAO;
	protected AmBestDAO amBestDAO;

	public ReportContractorAuditOperator(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		// sql = new SelectContractorAudit();
		this.auditDataDao = auditDataDao;
		this.auditQuestionDao = auditQuestionDao;
		this.operatorAccountDAO = operatorAccountDAO;
		this.amBestDAO = amBestDAO;

		filter = new ReportFilterCAO();
	}

	@Override
	protected void checkPermissions() throws Exception {
		// TODO Auto-generated method stub
		super.checkPermissions();
	}

	@Override
	protected void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		// TODO: permissions?

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

		getFilter().setShowPrimaryInformation(true);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowTrade(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowFlagStatus(false);

		getFilter().setShowCreatedDate(true);
		getFilter().setShowPolicyType(true);
		getFilter().setShowCaoStatusChangedDate(true);
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterCAO f = getFilter();

		String auditStatusList = Strings.implodeForDB(f.getAuditStatus(), ",");
		if (filterOn(auditStatusList)) {
			sql.addWhere("cao.status IN (" + auditStatusList + ")");
			setFiltered(true);
		}

		if (filterOn(f.getPercentComplete1())) {
			report
					.addFilter(new SelectFilter("percentComplete1", "ca.percentComplete >= '?'", f
							.getPercentComplete1()));
		}

		if (filterOn(f.getPercentComplete2())) {
			report.addFilter(new SelectFilter("percentComplete2", "ca.percentComplete < '?'", f.getPercentComplete2()));
		}

	}

	@Override
	public ReportFilterCAO getFilter() {
		return (ReportFilterCAO) filter;
	}

}
