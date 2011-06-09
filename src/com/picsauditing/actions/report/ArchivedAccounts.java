package com.picsauditing.actions.report;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.OpPerms;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ArchivedAccounts extends ReportAccount {
	protected int conID;

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.DelinquentAccounts);
	}

	public void buildQuery() {
		skipPermissions = true;
		super.buildQuery();

		if (permissions.isOperatorCorporate()) {
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id");
			sql.addField("gc.flag");

			if (permissions.isCorporate()) {
				sql.addJoin("JOIN facilities f ON f.opID = gc.genID AND f.corporateID = " + permissions.getAccountId());
			} else if (permissions.isOperator()) {
				sql.addWhere("gc.genID = " + permissions.getAccountId());
			}
		}

		sql.addWhere("a.status IN ('Pending','Deactivated')");

		if (permissions.seesAllContractors()) {
			sql.addField("a.reason");
			sql.addField("c.paymentExpires");
			sql.addAuditQuestion(69, 1, false);
		}

		PermissionQueryBuilder qb = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		qb.setShowPendingDeactivated(true);
		qb.setWorkingFacilities(false);
		sql.addWhere("1 " + qb.toString());

		getFilter().setShowStatus(false);
		getFilter().setShowFlagStatus(false);
		getFilter().setShowWaitingOn(false);
		if (permissions.seesAllContractors())
			getFilter().setShowDeactivationReason(true);
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if ("Remove".equals(button)) {
			permissions.tryPermission(OpPerms.RemoveContractors);
			FacilityChanger facilityChanger = (FacilityChanger) SpringUtils.getBean("FacilityChanger");
			facilityChanger.setOperator(permissions.getAccountId());
			facilityChanger.setContractor(conID);
			facilityChanger.setPermissions(permissions);
			facilityChanger.remove();
		}

		return super.execute();
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
}
