package com.picsauditing.actions.audits;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class WelcomeCallWidget extends PicsActionSupport {
	private int csrID;
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
		sql.addField("cao.status");
		sql.addField("cao.percentComplete");
		sql.addField("cao.creationDate createdDate");
		sql.addField("ca.expiresDate");
		sql.addField("atype.auditName");
		sql.addField("c.welcomeAuditor_id AS csrID");
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		sql.addWhere("ca.auditTypeID = 9");
		sql.addWhere("cao.status = 'Pending'");
		sql.addWhere("c.welcomeAuditor_id = " + permissions.getUserId());

		try {
			Database db = new Database();
			List<BasicDynaBean> pageData = db.select(sql.toString(), false);
			
			if (pageData.size() > 0)
				csrID = Integer.parseInt(pageData.get(0).get("csrID").toString());
			
			return pageData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getCsrID() {
		return csrID;
	}
}
