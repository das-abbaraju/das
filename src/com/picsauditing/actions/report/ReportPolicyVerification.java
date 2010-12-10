package com.picsauditing.actions.report;

import java.io.IOException;
import org.apache.commons.beanutils.BasicDynaBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportPolicyVerification extends ReportContractorAuditOperator {
	private int auditID = 0;

	public ReportPolicyVerification(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		auditTypeClass = AuditTypeClass.Policy;
		orderByDefault = "MIN(cao.statusChangedDate) ASC, a.name";
	}

	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceVerification);
	}

	@Override
	protected void buildQuery() {
		getFilter().setShowAuditStatus(false);
		getFilter().setShowStatus(false);
		super.buildQuery();

		sql.addWhere("cao.status = 'Submitted'");

		sql.addField("MIN(cao.statusChangedDate) statusChangedDate");

		sql.addField("COUNT(cao.auditID) as operatorCount");
		sql.addGroupBy("ca.id");

		sql.addWhere("a.acceptsBids = 0");
		
		getFilter().setShowAuditFor(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowAMBest(true);
	}

	@Override
	protected String returnResult() throws IOException {
		if ("showNext".equals(button)) {
			if (data != null && data.size() > 0) {
				BasicDynaBean row = data.get(0);
				
				boolean next = false;
				for (BasicDynaBean d : data) {
					if (next) {
						row = d;
						break;
					}
					
					if ((Long) d.get("auditID") == auditID)
						next = true;
				}
				
				return redirect("Audit.action?auditID=" + row.get("auditID") + "&policy=true");
			}
		}
		if ("getFirst".equals(button)) {
			if (data != null && data.size() > 0) {
				BasicDynaBean firstRow = data.get(0);
				// TODO forward to the AuditCat page for that audit
				return redirect("Audit.action?auditID=" + firstRow.get("auditID"));
			}
		}
		return super.returnResult();
	}
	
	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}
}
