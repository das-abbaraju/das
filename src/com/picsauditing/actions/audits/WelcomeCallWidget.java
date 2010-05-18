package com.picsauditing.actions.audits;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class WelcomeCallWidget extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		return SUCCESS;
	}
	
	public List<BasicDynaBean> getPendingWelcomeCalls() {
		SelectAccount sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);
		sql.addField("ca.id AS auditID");
		sql.addField("ca.auditTypeID");
		sql.addField("ca.auditStatus");
		sql.addField("ca.percentComplete");
		sql.addField("ca.creationDate createdDate");
		sql.addField("ca.expiresDate");
		sql.addField("atype.auditName");
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");
		sql.addWhere("ca.auditTypeID = 9");
		sql.addWhere("ca.auditStatus = 'Pending'");
		sql.addWhere("c.welcomeAuditor_id = " + permissions.getUserId());
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
