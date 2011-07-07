package com.picsauditing.actions.flags;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class ContractorFlagChangesAddDetails extends ContractorActionSupport {

	private int id = 0;
	private int priority;
	private String eta;
	private ContractorOperatorDAO contractorOperatorDao;
	private UserDAO userDao;
	private ContractorAccountDAO contractorAccountDao;
	private ContractorOperator co;

	public ContractorFlagChangesAddDetails(UserDAO userDao, ContractorOperatorDAO contractorOperatorDao,
			ContractorAccountDAO contractorAccountDao) {
		this.contractorOperatorDao = contractorOperatorDao;
		this.userDao = userDao;
		this.contractorAccountDao = contractorAccountDao;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		co = contractorOperatorDao.find(id);
		priority = co.getContractorAccount().getNeedsRecalculation();

		if (priority == 0) {
			ContractorAccount lastRun = (ContractorAccount) contractorAccountDao.findWhere(ContractorAccount.class,
					"lastRecalculation != '(null)'", 1, "lastRecalculation DESC").get(0);

			ContractorAccount oldestRun = (ContractorAccount) contractorAccountDao.findWhere(ContractorAccount.class,
					"lastRecalculation != '(null)' AND status = 'Active'", 1, "lastRecalculation ASC").get(0);

			long lastRunTime = lastRun.getLastRecalculation().getTime();
			long oldestRunTime = oldestRun.getLastRecalculation().getTime();

			double time = (lastRunTime - oldestRunTime) / 3600000;

			eta = time + " hours";
		} else
			eta = "Prioritized";

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
		return eta;
	}

	public String getBaseLineApproved() {
		return co.getBaselineApproved().toString();
	}

	public String getBaseLineApprover() {
		return userDao.find(co.getBaselineApprover()).getName();
	}
}
