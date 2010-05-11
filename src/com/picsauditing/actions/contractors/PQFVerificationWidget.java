package com.picsauditing.actions.contractors;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class PQFVerificationWidget extends PicsActionSupport {
	public List<BasicDynaBean> getPqfVerifications() {
		SelectAccount sql = new SelectAccount();

		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN users contact ON contact.id = a.contactID");
		sql.addField("a.phone");
		sql.addField("a.fax");
		sql.addField("a.creationDate");
		sql.addField("c.riskLevel");
		sql.setType(SelectAccount.Type.Contractor);
		
		SelectSQL subSelect = new SelectSQL("contractor_audit ca");
		subSelect.addField("ca.conID");
		subSelect.addJoin("JOIN generalcontractors gc ON gc.subid = ca.conid");
		subSelect.addJoin("JOIN operators o ON o.id = gc.genid");
		subSelect.addJoin("JOIN audit_operator ao ON ao.opid = o.inheritAudits ");
		subSelect.addWhere("ao.auditTypeID in (1,11)");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ca.auditTypeID = ao.auditTypeID");
		subSelect.addWhere("ca.auditStatus IN ('Submitted','Resubmitted')");
		
		sql.addWhere("a.id IN (" + subSelect.toString() + ")");
		sql.addJoin("LEFT JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");
		sql.addJoin("JOIN contractor_audit ca1 on ca1.conID = a.id");
		sql.addWhere("ca1.auditTypeID = 1");
		sql.addField("ca1.completedDate");
		sql.addWhere("a.acceptsBids = 0");
		sql.addWhere("a.status = 'Active'");
		sql.addOrderBy("completedDate");
		sql.setLimit(10);

		try {
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), false);
			return pageData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
