package com.picsauditing.actions.report;

import java.util.List;
import java.util.Map;


import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportInsuranceSupport extends ReportContractorAuditOperator {

	protected AuditDataDAO auditDataDao = null;
	protected AuditQuestionDAO auditQuestionDao = null;
	protected OperatorAccountDAO operatorAccountDAO = null;
	protected AmBestDAO amBestDAO = null;

	/**
	 * Map of Purpose, AuditID, then List of Answers
	 */
	protected Map<String, Map<Integer, List<AuditData>>> questionData = null;

	public ReportInsuranceSupport(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		this.auditQuestionDao = auditQuestionDao;
		this.operatorAccountDAO = operatorAccountDAO;
		this.amBestDAO = amBestDAO;
	}

	@Override
	protected void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();

		if (!permissions.hasPermission(OpPerms.AllContractors)) {
			sql.addField("caow.notes as caoNotes");
			sql.addField("cao.flag as caoRecommendedFlag");
			sql.addField("cao.certificateID");
			sql.addField("valid");
		}

		sql.addWhere("ca.expiresDate > NOW()");

		if (getFilter().getAmBestRating() > 0 || getFilter().getAmBestClass() > 0) {
			sql.addJoin("JOIN pqfdata am ON am.auditid = ca.id");
			sql.addJoin("JOIN pqfquestions pq ON pq.id = am.questionid");
			sql.addJoin("JOIN ambest ambest ON ambest.naic = am.comment and ambest.companyName = am.answer");
			sql.addWhere("pq.questionType = 'AMBest'");
			if (getFilter().getAmBestRating() > 0)
				sql.addWhere("ambest.ratingcode = " + getFilter().getAmBestRating());
			if (getFilter().getAmBestClass() > 0)
				sql.addWhere("ambest.financialCode =" + getFilter().getAmBestClass());
		}

		getFilter().setShowPrimaryInformation(true);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowFlagStatus(false);

		getFilter().setShowCreatedDate(true);
		getFilter().setShowPolicyType(true);
	}
}
