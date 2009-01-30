package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportPolicyVerification extends ReportContractorAudits {
	private static final long serialVersionUID = 6697393552632136569L;
	
	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceVerification);
	}

	@Override
	protected void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();
		
		// Only show policies that are required by operators ( that haven't decided yet
		SelectSQL subSelect = new SelectSQL("contractor_audit_operator cao");
		subSelect.addJoin("JOIN contractor_audit ca ON cao.auditID = ca.id");
		subSelect.addJoin("JOIN audit_operator ao ON ca.auditTypeID = ao.auditTypeID AND cao.opID = ao.opID");
		subSelect.addField("ca.id auditID");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ao.requiredForFlag in ('Amber','Red')");
		subSelect.addWhere("ao.requiredAuditStatus = 'Active'");
		subSelect.addWhere("cao.status = 'Awaiting'");
		sql.addWhere("ca.id IN (" + subSelect.toString() + ")");
	}
	
	@Override
	protected String returnResult() throws IOException {
		if ("showNext".equals(button)) {
			if (data != null && data.size() > 0) {
				BasicDynaBean firstRow = data.get(0);
				// TODO forward to the AuditCat page for that audit
				return SUCCESS;
			}
		}
		return super.returnResult();
	}
}
