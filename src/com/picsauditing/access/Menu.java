package com.picsauditing.access;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ReportUser;

@SuppressWarnings("serial")
public class Menu extends PicsActionSupport {

	// Why is this part of Menu.java?
	private ContractorAccount contractor = null;

	public String execute() throws Exception {
		loadPermissions();

		String userFavoritesQuery =  "userID = " + permissions.getUserId() + " AND is_favorite = 1";
		List<ReportUser> favoriteReports = dao.findWhere(ReportUser.class, userFavoritesQuery);

		MenuComponent menu = MenuBuilder.buildMenubar(permissions, favoriteReports);

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
