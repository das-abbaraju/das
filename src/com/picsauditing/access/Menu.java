package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportUser;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	// Why is this part of Menu.java?
	private ContractorAccount contractor = null;

	public String execute() throws Exception {
		loadPermissions();

		String userQuery = "userID = " + permissions.getUserId();
		List<ReportUser> savedReports = dao.findWhere(ReportUser.class, userQuery);

		String userFavoritesQuery =  userQuery + " and is_favorite = true";
		List<ReportUser> favoriteReports = dao.findWhere(ReportUser.class, userFavoritesQuery);

		MenuComponent menu = MenuBuilder.buildMenubar(permissions, favoriteReports, savedReports);

		// TODO make this return plain JSON, not ext-js specific stuff
		jsonArray = MenuWriter.exportMenuToExtJS(menu);

		return JSON_ARRAY;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
}
