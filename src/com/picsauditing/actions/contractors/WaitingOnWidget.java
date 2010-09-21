package com.picsauditing.actions.contractors;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class WaitingOnWidget extends PicsActionSupport {
	private List<BasicDynaBean> data;
	
	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		try {
			SelectSQL sql = new SelectSQL("contractor_audit a");
			sql.addField("a.id");
			sql.addField("a.conID");
			sql.addField("con.name");
			sql.addField("t.auditName");
			sql.addField("cao.percentVerified");
			sql.addJoin("JOIN audit_type t ON t.id = a.auditTypeID");
			sql.addJoin("JOIN accounts con ON con.id = a.conID");
			sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = a.id");
			sql.addWhere("cao.visible = 1");
			sql.addWhere("a.percentComplete = 100");
			sql.addWhere("a.percentVerified < 100");
			sql.addWhere("a.auditStatus = 'Pending'");
			sql.addWhere("t.workflowID IN (SELECT workflowID from workflow_step ws WHERE ws.newStatus = 'Submitted')");
			sql.addWhere("a.auditorID = " + permissions.getUserId());
			sql.addOrderBy("a.id DESC");
			sql.setLimit(10);
			
			Database db = new Database();
			data = db.select(sql.toString(), false);
		} catch (Exception e) {
			e.printStackTrace();
			addActionError("Could not query database");
		}
		
		return SUCCESS;
	}
	
	public List<BasicDynaBean> getData() {
		return data;
	}
}
