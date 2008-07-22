package com.picsauditing.actions.contractors;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

public class DelinquentAccountsWidget extends PicsActionSupport {
	ContractorAccountDAO accountDao;

	public DelinquentAccountsWidget(ContractorAccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	public String execute() throws Exception {
		loadPermissions();
		return SUCCESS;
	}

	public List<ContractorAccount> getDelinquentContractors() {
		return accountDao.findDelinquentContractors(permissions, 10);
	}

	public int getDaysLeft(Date invoiceDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(invoiceDate);
		cal.add(cal.DAY_OF_YEAR, 120);
		return DateBean.getDateDifference(cal.getTime());
	}

}
