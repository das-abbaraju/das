package com.picsauditing.actions.report;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.ReportFilterContractor;

@SuppressWarnings("serial")
public class ArchivedAccounts extends ReportAccount {
	@Autowired
	protected FacilityChanger facilityChanger;
	
	protected ContractorAccount contractor;
	protected OperatorAccount operator;

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
		qb.addVisibleStatus(AccountStatus.Deactivated);
		qb.addVisibleStatus(AccountStatus.Pending);
		qb.setWorkingFacilities(false);
		sql.addWhere("1 " + qb.toString());

		getFilter().setShowStatus(false);
		getFilter().setShowFlagStatus(false);
		getFilter().setShowWaitingOn(false);
		if (permissions.seesAllContractors())
			getFilter().setShowDeactivationReason(true);
	}

	@RequiredPermission(value=OpPerms.RemoveContractors)
	public String remove() throws Exception {
		facilityChanger.setOperator(operator);
		facilityChanger.setContractor(contractor);
		facilityChanger.setPermissions(permissions);
		facilityChanger.remove();
		
		return super.execute();
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
	
	public Map<String, String> getReasons() {
		return ReportFilterContractor.getDeactivationReasons();
	}
}
