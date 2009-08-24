package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAccountQuick extends ReportAccount {
	public void buildQuery() {
		sql = new SelectAccount();
		sql.addField("dbaName");
		sql.addField("city");
		sql.addField("state");
		sql.addWhere("a.type IN ('Operator', 'Corporate', 'Contractor')");
		sql.setPermissions(permissions);
		orderByDefault = "a.type, a.name";

		ReportFilterContractor f = getFilter();
		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			int id = Strings.extractAccountID(accountName);
			if (id > 0) {
				sql.addWhere("a.id = " + id);
			} else {
				sql.addWhere("a.dbaName LIKE '" + Utilities.escapeQuotes(accountName) + "%'" + " OR a.nameIndex LIKE '%"
						+ Strings.indexName(accountName) + "%'");
			}
		}
	}

	@Override
	protected String returnResult() throws IOException {
		if (data.size() == 1) {
			// Forward the user to the Contractor Details page
			String id = this.data.get(0).get("id").toString();
			if (data.get(0).get("type").equals("Contractor"))
				ServletActionContext.getResponse().sendRedirect("ContractorView.action?id=" + id);
			else
				ServletActionContext.getResponse().sendRedirect("FacilitiesEdit.action?id=" + id);
		}
		return SUCCESS;
	}
}
