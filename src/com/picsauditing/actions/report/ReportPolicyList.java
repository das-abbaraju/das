package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.util.Strings;

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
		super.buildQuery();
		
		if (permissions.hasPermission(OpPerms.AllContractors)) {
			if (getFilter().getOperator() != null && getFilter().getOperator().length > 0) {
				sql.addField("cao.status as caoStatus");
				sql.addWhere("cao.opid IN (" + Strings.implode(getFilter().getOperator(), ",") + ")");
			} else {
				sql.addGroupBy("ca.id");
			}
		}
		

	}
}
