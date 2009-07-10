package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.mail.ContractorRegistrationSubscription;
import com.picsauditing.mail.FlagChangesSubscription;
import com.picsauditing.mail.InsuranceCertificateSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionBuilder;
import com.picsauditing.mail.SubscriptionTimePeriod;

@SuppressWarnings("serial")
public class SubscriptionCron extends PicsActionSupport implements ServletRequestAware {

	private EmailSubscriptionDAO subscriptionDAO;
	private ContractorOperatorFlagDAO flagDAO;
	private ContractorAuditOperatorDAO caoDAO;
	private ContractorAccountDAO conDAO;
	protected HttpServletRequest request;

	public SubscriptionCron(EmailSubscriptionDAO subscriptionDAO, ContractorOperatorFlagDAO flagDAO,
			ContractorAuditOperatorDAO caoDAO, ContractorAccountDAO conDAO) {
		this.subscriptionDAO = subscriptionDAO;
		this.flagDAO = flagDAO;
		this.caoDAO = caoDAO;
		this.conDAO = conDAO;
	}

	@Override
	public String execute() throws Exception {
		Calendar calendar = Calendar.getInstance();
		String name = request.getRequestURL().toString();
		String serverName = name.replace(ActionContext.getContext().getName() + ".action", "");

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
			builder.setServerName(serverName);
			builder.process();

			builder = new InsuranceCertificateSubscription(Subscription.PendingInsuranceCerts, timePeriod,
					subscriptionDAO, caoDAO);
			builder.setServerName(serverName);
			builder.process();

			builder = new InsuranceCertificateSubscription(Subscription.VerifiedInsuranceCerts, timePeriod,
					subscriptionDAO, caoDAO);
			builder.setServerName(serverName);
			builder.process();

			builder = new ContractorRegistrationSubscription(timePeriod, subscriptionDAO, conDAO);
			builder.process();
		}

		return SUCCESS;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
