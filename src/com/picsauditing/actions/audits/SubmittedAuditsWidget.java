package com.picsauditing.actions.audits;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class SubmittedAuditsWidget extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		return SUCCESS;
	}
	
	public List<BasicDynaBean> getSubmittedAudits() {
		SelectAccount sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		sql.addField("atype.auditName");
		sql.addField("ca.id AS auditID");
		sql.addField("ca.statusChangedDate");
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("ca.auditTypeID IN (2,3)"); // Manual and Implementation Audits
		sql.addWhere("cao.status = 'Submitted'");
		sql.addWhere("cao.statusChangedDate < DATE_SUB(NOW(), INTERVAL 45 DAY)"); // 45 days after
		sql.addWhere("c.welcomeAuditor_id = "+ permissions.getUserId());
		sql.addOrderBy("cao.statusChangedDate DESC");
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
