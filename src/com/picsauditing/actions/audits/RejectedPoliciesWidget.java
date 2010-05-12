package com.picsauditing.actions.audits;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class RejectedPoliciesWidget extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		return SUCCESS;
	}
	
	public List<BasicDynaBean> getRejectedPolicies() {
		SelectAccount sql = new SelectAccount();
		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		sql.addJoin("JOIN audit_type atype ON atype.id = ca.auditTypeID");
		sql.addField("ca.id AS auditID");
		sql.addField("atype.auditName");
		sql.addField("cao.statusChangedDate");
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("atype.classType = 'Policy'");
		sql.addWhere("ca.auditStatus != 'Expired'");
		sql.addWhere("cao.status = 'Rejected'");
		sql.addWhere("cao.statusChangedDate < DATE_SUB(NOW(), INTERVAL 30 DAY)");
		sql.addWhere("ca.auditorID = " + permissions.getUserId());
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
