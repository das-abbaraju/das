package com.picsauditing.actions;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.CronDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class CronDashboard extends ContractorActionSupport {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
    private CronDAO cronDao;

    public double totalHours;

    public String execute() {

    	totalHours = Math.ceil(cronDao.timeToRunAllContractors() / 3600);

    	return SUCCESS;
    }

    public CronDAO getCronDao() {
        return cronDao;
    }

}
