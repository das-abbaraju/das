package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportPolicyList extends ReportContractorAuditOperator {

	public ReportPolicyList(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		auditTypeClass = AuditTypeClass.Policy;
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceCerts);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		// TODO: Inheritance changes
		if (permissions.hasPermission(OpPerms.AllContractors)) {
			if (getFilter().getOperatorSingle() > 0) {
				sql.addField("cao.status as caoStatus");
				sql.addJoin("JOIN operators o ON o.inheritInsuranceCriteria = cao.opID AND o.id = "
						+ getFilter().getOperatorSingle());
			} else {
				sql.addGroupBy("ca.id");
			}
			getFilter().setShowOperator(false);
			getFilter().setShowOperatorSingle(false);
		}

		getFilter().setShowAuditFor(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowAMBest(true);

	}
	

}
