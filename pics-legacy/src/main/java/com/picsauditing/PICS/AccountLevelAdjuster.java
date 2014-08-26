package com.picsauditing.PICS;

import com.picsauditing.audits.AuditBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.audits.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;

public class AccountLevelAdjuster {
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	protected AuditBuilderFactory auditBuilderFactory;

	public void upgradeToFullAccount(ContractorAccount contractor, Permissions permissions) {
		contractor.setAccountLevel(AccountLevel.Full);
		contractor.setRenew(true);
		resetPqfToPending(contractor, permissions);
		auditBuilderFactory.buildAudits(contractor);
	}

	private void resetPqfToPending(ContractorAccount contractor, Permissions permissions) {
		for (ContractorAudit cAudit : contractor.getAudits()) {
			if (cAudit.getAuditType().isPicsPqf()) {
				boolean atLeastOneCaoAffected = false;
				for (ContractorAuditOperator cao : cAudit.getOperators()) {
					if (cao.getStatus().after(AuditStatus.Pending)) {
						atLeastOneCaoAffected = true;
						ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Pending, permissions);
						auditDataDAO.save(cao);
						if (caow != null) {
							caow.setNotes("PQF set to pending for " + cao.getOperator().getName() + " because contractor moving to FULL account level.");
							auditDataDAO.save(caow);
						}
					}
				}

				if (atLeastOneCaoAffected) {
                    auditBuilderFactory.recalculateCategories(cAudit);
                    auditBuilderFactory.recalcAllAuditCatDatas(cAudit);
                    auditBuilderFactory.percentCalculateComplete(cAudit);
					auditDataDAO.save(cAudit);
				}
			}
		}
	}
	public void setListOnlyIfPossible(ContractorAccount contractor) {
		if (contractor.isListOnlyEligible() && contractor.getStatus().isPending()
				&& contractor.getAccountLevel().isFull()) {
			boolean canBeListed = true;
			for (ContractorOperator conOp : contractor.getNonCorporateOperators()) {
				if (!conOp.getOperatorAccount().isAcceptsList())
					canBeListed = false;
			}
			if (canBeListed)
				contractor.setAccountLevel(AccountLevel.ListOnly);
		}
	}

}
