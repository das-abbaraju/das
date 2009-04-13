package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

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
		
		sql.addJoin("JOIN contractor_audit_operator cao on ca.id = cao.auditID");
		sql.addWhere("cao.status = 'Awaiting'");
		sql.addWhere("ca.auditStatus != 'Expired'");
		sql.addJoin("JOIN pqfcatdata pcd ON ca.id = pcd.auditID");
		sql.addField("COUNT(cao.auditID) as operatorCount");
		sql.addField("pcd.id catdataID");
		sql.addField("cao.status as caoStatus");
		sql.addGroupBy("ca.id");
		
		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowAuditStatus(false);

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
