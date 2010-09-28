package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;

@SuppressWarnings("serial")
public class ReportAuditDataUpdate extends ReportContractorAuditOperator {
	
	public ReportAuditDataUpdate(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO){
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AuditVerification);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addField("pq.updateDate");
		sql.addField("pq.answer");
		sql.addField("pt.question");
		sql.addJoin("JOIN pqfdata pq on pq.auditID = ca.id");
		sql.addJoin("JOIN pqfquestion_text pt on pt.questionID = pq.questionID");
		sql.addWhere("pq.updateDate > cao.statusChangedDate");
		sql.addWhere("a.status = 'Active'");
		if(getFilter().getAuditTypeID() == null) {
			sql.addWhere("atype.id = 1");
		}
		orderByDefault = "pq.updateDate DESC";
		
		getFilter().setShowPolicyType(true);
	}
}
