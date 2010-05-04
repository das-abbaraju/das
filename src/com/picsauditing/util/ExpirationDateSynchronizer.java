package com.picsauditing.util;

import java.util.List;

import com.picsauditing.PICS.BrainTreeService.CreditCard;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.log.PicsLogger;

/**
 * For collecting contractors with credit cards with no expiration date and
 * synchronizing them with their corresponding expiration date on BrainTree.
 * 
 * @author Thomas Baker
 * @version 4.3, 05/03/2010
 */

public class ExpirationDateSynchronizer extends PicsActionSupport {
	private static final long serialVersionUID = 2238845577233880953L;

	private List<ContractorAccount> contractors;

	@Override
	public String execute() throws Exception {
		if ("Synchronize Expiration Dates".equals(button)) {
			// get list of contractors with credit card and no expiration
			ContractorAccountDAO contractorAccountDAO = (ContractorAccountDAO) SpringUtils
					.getBean("ContractorAccountDAO");
			if (contractorAccountDAO == null) {
				addActionError("Could not get ContractorAccountDAO Bean");
				return SUCCESS;
			}

			PicsLogger.addRuntimeRule("ExpirationDateSynchronizer");
			PicsLogger.start("ExpirationDateSynchronizer");

			List<ContractorAccount> contractors = contractorAccountDAO
					.findWhere("a.ccExpiration IS NULL AND a.ccOnFile = 1");

			if (contractors == null || contractors.isEmpty()) {
				PicsLogger.log("No contractors with null expiration and ccOnFile found");
				addActionError("No contractors with null expiration and ccOnFile found");
				PicsLogger.stop();
				return SUCCESS;
			}

			// for each contractor
			for (ContractorAccount contractor : contractors) {
				// query braintree
				CreditCard cc = contractor.getCreditCard();
				// get expiration
				if (cc != null) {
					contractor.setCcExpiration(cc.getExpirationDate2());

					// save expiration to contractor account
					contractorAccountDAO.save(contractor);
				} else {
					PicsLogger.log("Contractor " + contractor.getId() + "(" + contractor.getName()
							+ ") failed to update expiration date");
					addActionError("Contractor " + contractor.getId() + "(" + contractor.getName()
							+ ") failed to update expiration date");
				}
			}
			
			PicsLogger.stop();
		}

		return SUCCESS;
	}

	public void setContractors(List<ContractorAccount> contractors) {
		this.contractors = contractors;
	}

	public List<ContractorAccount> getContractors() {
		return contractors;
	}
}
