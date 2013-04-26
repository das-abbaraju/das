package com.picsauditing.actions;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.CronDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class CronDashboard extends ContractorActionSupport {
    @Autowired
    private CronDAO cronDao;

    public String execute() {
        return SUCCESS;
    }

    public CronDAO getCronDao() {
        return cronDao;
    }

}
