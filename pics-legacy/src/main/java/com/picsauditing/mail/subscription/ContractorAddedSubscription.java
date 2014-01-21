package com.picsauditing.mail.subscription;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.service.mail.MailCronService;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractorAddedSubscription extends SubscriptionBuilder {
    @Autowired
    DynamicReportsSubscription dynamicReports;

    private static final int REPORT_ID_DAILY = 1416;
    private static final int REPORT_ID_WEEKLY = 1586;
    private static final int REPORT_ID_MONTHLY = 1587;

    @Override
	public Map<String, Object> process(EmailSubscription subscription) throws IOException {
        EmailSubscription emailSubscription = new EmailSubscription();
        int reportID = getReportID(subscription);

        emailSubscription.setUser(subscription.getUser());
        emailSubscription.setReport(reportDAO.findById(reportID));
        emailSubscription.setSubscription(Subscription.DynamicReports);

        return dynamicReports.process(emailSubscription);
    }

    private int getReportID(EmailSubscription subscription) {
        int reportID;
        switch (subscription.getTimePeriod()) {
            case Daily:
                reportID = REPORT_ID_DAILY;
                break;
            case Weekly:
                reportID = REPORT_ID_WEEKLY;
                break;
            case Monthly:
                reportID = REPORT_ID_MONTHLY;
                break;
            default: {
                reportID = REPORT_ID_DAILY;
            }
        }
        return reportID;
    }

}