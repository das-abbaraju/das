package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportPolicyList extends ReportContractorAuditOperator {

	public ReportPolicyList() {
		super();
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

		if (permissions.isOperatorCorporate()) {
			sql.addField("d.answer certID");

			sql.addJoin("LEFT JOIN (SELECT d.auditID, d.answer FROM pqfdata d "
					+ "JOIN audit_question aq ON aq.id = d.questionID AND aq.questionType = 'FileCertificate' "
					+ "JOIN audit_category_rule acr ON acr.catID = aq.categoryID AND acr.opID = "
					+ permissions.getAccountId()
					+ " JOIN audit_type atype ON atype.id = acr.auditTypeID AND atype.classType = 'Policy'"
					+ ") d ON d.auditID = ca.id");
		}

		getFilter().setShowAuditFor(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowAMBest(true);

	}
}
