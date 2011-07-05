package com.picsauditing.actions.report;


import java.util.ArrayList;
import java.util.List;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportCsrContractorCount extends ReportActionSupport {
	protected List<User> csrs = null;
	protected UserDAO userDAO = null;
	protected int[] csrIds;

	public ReportCsrContractorCount(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if(!filterOn(csrIds)) {
			if(permissions.hasGroup(User.GROUP_MANAGER)) {
				csrIds = new int[getCsrs().size()];
				int i = 0;
				for(User u : getCsrs()) {
					csrIds[i] = u.getId();
					i++;
				}
			}
			else {
				csrIds = new int[1];
				csrIds[0] = permissions.getUserId();
			}
		}
		
		SelectSQL sql = new SelectSQL("contractor_info ci");
		sql.addField("u.name as name");
		sql.addField("a.state as state");
		sql.addField("count(a.name) as cnt");
		sql.addJoin("JOIN accounts a ON a.status = 'Active'");
		sql.addJoin("JOIN users u ON ci.welcomeAuditor_id = u.id");
		String opIds = " ci.welcomeAuditor_id IN (" + csrIds[0];
		for (int i = 1; i < csrIds.length; i++) {
			opIds += "," + csrIds[i];
		}
		opIds += ")";
		sql.addWhere(opIds);
		sql.addWhere("a.id = ci.id");
		sql.addGroupBy("ci.welcomeAuditor_id, a.state");
		
		orderByDefault = "u.name,a.state DESC";
		filteredDefault = true;
		run(sql);

		return SUCCESS;
	}

	public List<User> getCsrs() {
		if(csrs == null) {
			csrs = new ArrayList<User>();
			csrs = userDAO.findByGroup(User.GROUP_CSR);
		}
		return csrs;
	}
	public int[] getCsrIds() {
		return csrIds;
	}
	public void setCsrIds(int[] csrIds) {
		this.csrIds = csrIds;
	}
}