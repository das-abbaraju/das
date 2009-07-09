package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.mail.ContractorRegistrationSubscription;
import com.picsauditing.mail.FlagChangesSubscription;
import com.picsauditing.mail.InsuranceCertificateSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionBuilder;
import com.picsauditing.mail.SubscriptionTimePeriod;

@SuppressWarnings("serial")
public class SubscriptionCron extends PicsActionSupport {

	private EmailSubscriptionDAO subscriptionDAO;
	private ContractorOperatorFlagDAO flagDAO;
	private ContractorAuditOperatorDAO caoDAO;
	private OperatorAccountDAO opDAO;
	private ContractorAccountDAO conDAO;

	public SubscriptionCron(EmailSubscriptionDAO subscriptionDAO, ContractorOperatorFlagDAO flagDAO,
			ContractorAuditOperatorDAO caoDAO, OperatorAccountDAO opDAO, ContractorAccountDAO conDAO) {
		this.subscriptionDAO = subscriptionDAO;
		this.flagDAO = flagDAO;
		this.caoDAO = caoDAO;
		this.opDAO = opDAO;
		this.conDAO = conDAO;
	}

	@Override
	public String execute() throws Exception {
		Calendar calendar = Calendar.getInstance();

		List<SubscriptionTimePeriod> timePeriods = new ArrayList<SubscriptionTimePeriod>();

		// Handle the daily subscriptions
		timePeriods.add(SubscriptionTimePeriod.Daily);

		// Handle the weekly subscriptions
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			timePeriods.add(SubscriptionTimePeriod.Weekly);

		// Handle the monthly subscriptions (1st of the month)
		if (calendar.get(Calendar.DAY_OF_MONTH) == 1)
			timePeriods.add(SubscriptionTimePeriod.Monthly);

		for (SubscriptionTimePeriod timePeriod : timePeriods) {
			SubscriptionBuilder builder;

			builder = new FlagChangesSubscription(timePeriod, subscriptionDAO, flagDAO);
			builder.process();

			builder = new InsuranceCertificateSubscription(Subscription.PendingInsuranceCerts, timePeriod,
					subscriptionDAO, caoDAO, opDAO);
			builder.process();

			builder = new InsuranceCertificateSubscription(Subscription.VerifiedInsuranceCerts, timePeriod,
					subscriptionDAO, caoDAO, opDAO);
			builder.process();

			builder = new ContractorRegistrationSubscription(timePeriod, subscriptionDAO, conDAO);
			builder.process();
		}

		return SUCCESS;
	}
}
