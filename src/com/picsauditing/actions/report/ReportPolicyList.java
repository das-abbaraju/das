package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

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
			sql.addGroupBy("ca.id");

			sql.addJoin("LEFT JOIN pqfdata d ON d.auditID = ca.id AND d.questionID IN "
					+ "(SELECT aq.id FROM audit_question aq "
					+ "JOIN audit_category_rule acr ON acr.catID = aq.categoryID AND acr.opID IN (" + Strings.implode(permissions.getVisibleAccounts()) + ")"
					+ "WHERE aq.questionType = 'FileCertificate')");
		}

		getFilter().setShowAuditFor(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowAMBest(true);
	}
	
	protected void addExcelColumns() {
		super.addExcelColumns();
		excelSheet.addColumn(new ExcelColumn("auditID", "AmBest"));
		excelSheet.setReportCAO(this);
	}
}
