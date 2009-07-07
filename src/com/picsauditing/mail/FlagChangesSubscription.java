package com.picsauditing.mail;

import java.util.Date;
import java.util.List;

import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;

public class FlagChangesSubscription extends SubscriptionBuilder {

	private ContractorOperatorFlagDAO flagDAO;

	public FlagChangesSubscription(SubscriptionTimePeriod timePeriod, EmailSubscriptionDAO subscriptionDAO,
			ContractorOperatorFlagDAO flagDAO) {
		super(Subscription.FlagChanges, timePeriod, subscriptionDAO);
		this.flagDAO = flagDAO;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setup() {
		templateID = 60;
	}

	@Override
	protected EmailQueue buildEmail(Account a) throws Exception {
		EmailQueue email = null;

		List<ContractorOperatorFlag> flags = flagDAO.findFlagChangedByOperatorAndRange(a.getId(), timePeriod
				.getComparisonDate(), new Date());

		if (flags.size() > 0) {

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(templateID);
			emailBuilder.addToken("flags", flags);
			emailBuilder.setUser(new User(2357));

			email = emailBuilder.build();
		}

		return email;
	}
}
