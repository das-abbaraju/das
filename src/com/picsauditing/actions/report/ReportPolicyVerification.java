package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;

@SuppressWarnings("serial")
public class ReportPolicyVerification extends ReportInsuranceSupport {

	public ReportPolicyVerification(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		orderByDefault = "MIN(cao.statusChangedDate) ASC, a.name";
	}

	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceVerification);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addWhere("cao.status = 'Submitted'");

		sql.addJoin("JOIN pqfcatdata pcd ON ca.id = pcd.auditID");
		sql.addField("pcd.id catdataID");
		sql.addField("MIN(cao.statusChangedDate) statusChangedDate");

		sql.addField("COUNT(cao.auditID) as operatorCount");
		sql.addGroupBy("ca.id");

		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addWhere("a.acceptsBids = 0");

		getFilter().setShowStatus(false);
		getFilter().setShowCaoStatus(false);
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
		if ("getFirst".equals(button)) {
			if (data != null && data.size() > 0) {
				BasicDynaBean firstRow = data.get(0);
				// TODO forward to the AuditCat page for that audit
				ServletActionContext.getResponse().sendRedirect(
						"AuditCat.action?auditID=" + firstRow.get("auditID") + "&catDataID="
								+ firstRow.get("catdataID"));
			}
		}
		return super.returnResult();
	}
}
