package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportPolicyVerification extends ReportContractorAudits {
	private static final long serialVersionUID = 6697393552632136569L;
	
	public ReportPolicyVerification(){
		orderByDefault = "ca.completedDate ASC, a.name";
	}
	
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
		subSelect.addJoin("JOIN generalcontractors gc on gc.genID = ao.opID AND gc.subid = ca.conId");
		subSelect.addField("ca.id auditID");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ao.requiredForFlag in ('Amber','Red')");
		subSelect.addWhere("ao.requiredAuditStatus = 'Active'");
		subSelect.addWhere("cao.status = 'Awaiting'");
		sql.addWhere("ca.id IN (" + subSelect.toString() + ")");
		sql.addJoin("JOIN pqfcatdata pcd ON ca.id = pcd.auditID");
		sql.addField("pcd.id catdataID");
		
		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowAuditFor(false);

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
		if("getFirst".equals(button)){
			if (data != null && data.size() > 0) {
				BasicDynaBean firstRow = data.get(0);
				// TODO forward to the AuditCat page for that audit
				ServletActionContext.getResponse().sendRedirect("AuditCat.action?auditID=" + firstRow.get("auditID") + "&catDataID=" + firstRow.get("catdataID"));
			}
		}
		return super.returnResult();
	}
}
