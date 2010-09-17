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
		
		sql.addJoin("JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");
		sql.addWhere("csr.id = " + permissions.getUserId());
		
		sql.addJoin("JOIN contractor_audit ca ON ca.conid = a.id");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		sql.addWhere("cao.status IN ('Submitted','Resubmitted')");
		sql.addWhere("ca.auditTypeID IN (1,11)");
		sql.addField("MIN(cao.statusChangedDate) as completedDate");
		sql.addWhere("a.acceptsBids = 0");
		sql.addWhere("a.status = 'Active'");
		sql.addGroupBy("ca.conid");
		sql.addOrderBy("cao.statusChangedDatee");
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
