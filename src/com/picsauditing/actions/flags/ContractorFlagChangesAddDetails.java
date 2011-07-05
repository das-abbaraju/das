package com.picsauditing.actions.flags;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.dao.UserDAO;

@SuppressWarnings("serial")
public class ContractorFlagChangesAddDetails extends ContractorActionSupport {

	private int id = 0;
	private int priority;
	private ContractorOperatorDAO contractorOperatorDao;
	private UserDAO userDao;
	private com.picsauditing.jpa.entities.ContractorOperator co;
	private PicsDAO picsDao;

	public ContractorFlagChangesAddDetails(UserDAO userDao, ContractorOperatorDAO contractorOperatorDao,
			ContractorAccountDAO contractorAccountDao, PicsDAO picsDao) {
		this.contractorOperatorDao = contractorOperatorDao;
		this.userDao = userDao;
		this.picsDao = picsDao;
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
		if (priority > 0)
			eta = "Prioritized";
		else {
			eta = "";
		}

		return eta;
	}

	public String getBaseLineApproved() {
		return co.getBaselineApproved().toString();
	}

	public String getBaseLineApprover() {
		return userDao.find(co.getBaselineApprover()).getName();
	}
}
