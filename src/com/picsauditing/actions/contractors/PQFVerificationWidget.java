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
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		return SUCCESS;
	}
	
	public List<BasicDynaBean> getPqfVerifications() {
		SelectAccount sql = new SelectAccount();

		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN users contact ON contact.id = a.contactID");
		sql.setType(SelectAccount.Type.Contractor);
		
		SelectSQL subSelect = new SelectSQL("generalcontractors gc");
		subSelect.addField("gc.subid");
		subSelect.addJoin("JOIN operators o ON o.id = gc.genid");
		subSelect.addJoin("JOIN audit_operator ao ON ao.opid = o.inheritAudits");
		subSelect.addWhere("ao.auditTypeID in (1,11)");
		subSelect.addWhere("ao.canSee = 1");
		
		sql.addWhere("a.id IN (" + subSelect.toString() + ")");
		sql.addJoin("JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");
		sql.addWhere("csr.id = " + permissions.getUserId());
		
		sql.addJoin("JOIN contractor_audit ca ON ca.conid = a.id");
		sql.addWhere("ca.auditStatus IN ('Submitted','Resubmitted')");
		sql.addWhere("ca.auditTypeID IN (1,11)");
		sql.addField("MIN(ca.completedDate) as completedDate");
		sql.addWhere("a.acceptsBids = 0");
		sql.addWhere("a.status = 'Active'");
		sql.addGroupBy("ca.conid");
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
