package com.picsauditing.actions.cron;

import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.util.PicsDateFormat;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckRegistrationRequestsHoldDates implements CronTask {

    @Autowired
    PicsDAO dao;

    @Override
    public String getDescription() {
        return "Checking Registration Requests Hold Dates";
    }

    @Override
    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        List<ContractorRegistrationRequest> holdRequests = dao.findWhere(ContractorRegistrationRequest.class,
                "t.status = 'Hold'");
        Date now = new Date();
        for (ContractorRegistrationRequest crr : holdRequests) {
            if (now.after(crr.getHoldDate())) {
                crr.setStatus(ContractorRegistrationRequestStatus.Active);
                crr.setNotes(maskDateFormat(now) + " - System - hold date passed.  Request set to active \n\n"
                        + crr.getNotes());
            }
        }
        return new CronTaskResult(true, "success");
    }

    private String maskDateFormat(Date now) {
        DateFormat dateFormat = new SimpleDateFormat(PicsDateFormat.Iso);
        return dateFormat.format(now);
    }
}
