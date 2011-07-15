package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class ReportImportPQFs extends ReportActionSupport {
	protected SelectAccount sql = new SelectAccount();

	@Override
	@RequiredPermission(value = OpPerms.ImportPQF)
	public String execute() throws Exception {
		buildQuery();
		run(sql);

		return SUCCESS;
	}

	protected void buildQuery() {
		sql.setType(SelectAccount.Type.Contractor);

		sql.addJoin("JOIN contractor_audit pqf ON pqf.conID = a.id AND pqf.auditTypeID = 1");
		sql.addJoin(String.format("JOIN contractor_audit importPQF ON importPQF.conID = a.id "
				+ "AND importPQF.auditTypeID = %d AND (importPQF.expiresDate > NOW() "
				+ "OR importPQF.expiresDate IS NULL)", AuditType.IMPORT_PQF));
		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = importPQF.id AND cao.visible = 1 "
				+ "AND cao.status IN ('Submitted', 'Resubmitted')");

		sql.addField("pqf.id pqfID");
		sql.addField("importPQF.id importPqfID");

		sql.addWhere("a.status = 'Active'");
	}
}
