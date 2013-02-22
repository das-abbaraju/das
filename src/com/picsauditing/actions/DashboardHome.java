package com.picsauditing.actions;

import java.util.List;
import java.util.Set;

import com.picsauditing.jpa.entities.Dashboard;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class DashboardHome extends PicsActionSupport {

	private int id = 0;
	private Dashboard dashboard = null;

	public String execute() throws Exception {
		findDashboard();
		buildJson();
		return SUCCESS;
	}

	private void findDashboard() {
		Set<Integer> validUserGroups = permissions.getDirectlyRelatedGroupIds();
		validUserGroups.add(permissions.getUserId());

		if (id > 0) {
			dashboard = dao.find(Dashboard.class, id);
			if (dashboard != null && validUserGroups.contains(dashboard.getUser().getId())) {
				return;
			}
		}

		String myDashboardWhereClause = "user.id IN (" + Strings.implode(validUserGroups) + ")";
		List<Dashboard> dashboards = dao.findWhere(Dashboard.class, myDashboardWhereClause);
		for (Dashboard candidateDashboard : dashboards) {
			int dashboardUserID = candidateDashboard.getUser().getId();
			if (dashboardUserID == permissions.getUserId()) {
				dashboard = candidateDashboard;
				return;
			} else if (dashboard == null) {
				dashboard = candidateDashboard;
			} else {
				// TODO compare the level of dashboard groups and try to return
				// the one closest to this user
			}
		}

		if (dashboard == null) {
			dashboard = dao.find(Dashboard.class, Dashboard.DEFAULT);
		}
	}

	private void buildJson() {
		this.jsonArray.add(null);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
