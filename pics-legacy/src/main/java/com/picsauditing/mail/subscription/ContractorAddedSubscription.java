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

	@Override
	public Map<String, Object> process(EmailSubscription subscription) throws IOException {
        EmailSubscription emailSubscription = new EmailSubscription();
        int reportID;
        switch (subscription.getTimePeriod()) {
            case Daily:
                reportID = 1416;
                break;
            case Weekly:
                reportID = 1586;
                break;
            case Monthly:
                reportID = 1587;
                break;
            default: {
                reportID = 1416;
            }
        }

        emailSubscription.setUser(subscription.getUser());
        emailSubscription.setReport(reportDAO.findById(reportID));
        emailSubscription.setSubscription(Subscription.DynamicReports);

        return dynamicReports.process(emailSubscription);
    }

}