package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportPolicyList extends ReportInsuranceSupport {

	
	public ReportPolicyList(AuditDataDAO auditDataDao,
			AuditQuestionDAO auditQuestionDao, OperatorAccountDAO operatorAccountDAO) {
		super( auditDataDao, auditQuestionDao, operatorAccountDAO );
	}

	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceCerts);
	}

	@Override
	public void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;

		super.buildQuery();
		
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");

		if(permissions.isOperator()) {
			sql.addField("cao.status AS CaoStatus");
			sql.addWhere("cao.opID = "+permissions.getAccountId());
		} else {
			sql.addField("COUNT(cao.status) as operatorCount");
			sql.addGroupBy("ca.id");
		}
		
		getFilter().setShowPolicyType(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowConAuditor(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowCaoStatus(true);
		getFilter().setShowAuditStatus(false);
	}
}
