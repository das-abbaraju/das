package com.picsauditing.actions.report;

import java.io.IOException;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectFilterInteger;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAccountQuick extends ReportAccount {
	public void buildQuery() {
		sql = new SelectAccount();
		sql.addWhere("a.type IN ('Operator', 'Corporate', 'Contractor')");
		sql.setPermissions(permissions);
		orderByDefault = "a.type, a.name";
		
		ReportFilterContractor f = getFilter();
		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			int id = Strings.extractAccountID(accountName);
			if (id > 0)
				report.addFilter(new SelectFilterInteger("id", "a.id = ?", id));
			else
				report.addFilter(new SelectFilter("accountName", "a.name LIKE '%?%'", accountName));
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
				ServletActionContext.getResponse().sendRedirect("FacilitiesEdit.action?opID=" + id);
		}
		return SUCCESS;
	}
}
