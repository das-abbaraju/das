package com.picsauditing.actions.contractors;

import java.util.Date;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorAgreement extends ContractorActionSupport {

	public ContractorAgreement(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		if ("I Agree".equals(button)) {
			if (!permissions.isAdmin()
					&& (permissions.hasPermission(OpPerms.ContractorAdmin)
							|| permissions.hasPermission(OpPerms.ContractorBilling) || permissions
							.hasPermission(OpPerms.ContractorSafety))) {
				contractor.setAgreementDate(new Date());
				contractor.setAgreedBy(getUser());
				accountDao.save(contractor);
				this.redirect("ContractorAgreement.action");
			} else {
				addActionError("Only account Administrators, Billing, and Safety can accept this Contractor Agreement");
			}
		}

		return SUCCESS;
	}
}
