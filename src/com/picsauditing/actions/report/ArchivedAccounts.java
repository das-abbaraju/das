package com.picsauditing.actions.report;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.OpPerms;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.SpringUtils;

public class ArchivedAccounts extends ReportAccount {
	protected int conID;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addField("a.contact");
		sql.addField("a.phone");
		sql.addField("a.phone2");
		sql.addField("a.email");
		sql.addField("c.lastInvoiceDate");
		sql.addWhere("DATEDIFF(NOW(),c.lastInvoiceDate) > 120");
		sql.addWhere("(c.lastPayment IS NULL OR c.lastPayment < c.lastInvoiceDate)");
		sql.addWhere("a.active = 'N'");

		setOrderBy("c.lastInvoiceDate ASC");

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		qb.setActiveContractorsOnly(false);
		qb.setWorkingFacilities(false);
		sql.addWhere("1 " + qb.toString());
		skipPermissions = true;
		permissions.tryPermission(OpPerms.RemoveContractors);
		if ("Remove".equals(button)) {
			FacilityChanger facilityChanger = (FacilityChanger) SpringUtils.getBean("FacilityChanger");
			facilityChanger.setOperator(permissions.getAccountId());
			facilityChanger.setContractor(conID);
			facilityChanger.setPermissions(permissions);
			facilityChanger.remove();
		}
		return super.execute();
	}

	@Override
	protected void toggleFilters() {
		super.toggleFilters();
		filterVisible = false;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
}
