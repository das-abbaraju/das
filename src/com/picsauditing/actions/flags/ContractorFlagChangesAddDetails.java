package com.picsauditing.actions.flags;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.UserDAO;

@SuppressWarnings("serial")
public class ContractorFlagChangesAddDetails extends ContractorActionSupport {

	protected int id = 0;
	protected int priority;
	protected ContractorOperatorDAO contractorOperatorDao;
	protected UserDAO userDao;
	protected com.picsauditing.jpa.entities.ContractorOperator co;

	public ContractorFlagChangesAddDetails(UserDAO userDao, ContractorOperatorDAO contractorOperatorDao,
			ContractorAccountDAO contractorAccountDao) {
		this.contractorOperatorDao = contractorOperatorDao;
		this.userDao = userDao;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		co = contractorOperatorDao.find(id);
		priority = co.getContractorAccount().getNeedsRecalculation();
		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastRecalc() {
		return co.getContractorAccount().getLastRecalculation().toString();
	}

	public int getPriority() {
		return priority;
	}

	public String getEta() {
		String eta;
		if (priority > 95)
			eta = "Soon";
		if (priority > 63)
			eta = "In a bit";
		if (priority > 31)
			eta = "In a while";
		else
			eta = "Don't hold your breath";
		return eta;
	}

	public String getBaseLineApproved() {
		return co.getBaselineApproved().toString();
	}

	public String getBaseLineApprover() {
		return userDao.find(co.getBaselineApprover()).getName();
	}
}
