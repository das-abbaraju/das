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

		if (permissions.isOperatorCorporate()) {
			sql.addField("d.answer certID");
			sql.addJoin("LEFT JOIN pqfdata d ON d.auditID = ca.id");
			sql.addWhere("d.questionID in " + "(SELECT aq.id FROM audit_question aq "
					+ "JOIN audit_category_rule acr ON acr.catID = aq.categoryID AND acr.opID = 1813 "
					+ "WHERE aq.questionType = 'FileCertificate' AND aq.columnHeader = 'Certificate') "
					+ "OR d.questionID IS NULL");
		}

		getFilter().setShowAuditFor(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowAMBest(true);

	}

}
