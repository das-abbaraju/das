package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;

public class InsuranceCertificateSubscription extends SubscriptionBuilder {

	private ContractorAuditOperatorDAO caoDAO;
	private CaoStatus caoStatus;
	private int templateID;

	public InsuranceCertificateSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, ContractorAuditOperatorDAO caoDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.caoDAO = caoDAO;
		if (subscription.equals(Subscription.PendingInsuranceCerts)) {
			this.caoStatus = CaoStatus.Pending;
			this.templateID = 62;
		}
		if (subscription.equals(Subscription.VerifiedInsuranceCerts)) {
			this.caoStatus = CaoStatus.Verified;
			this.templateID = 61;
		}
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	protected EmailQueue buildEmail(Account a) throws Exception {
		EmailQueue email = null;

		// We may need to use the inheritance for insurance
		List<ContractorAuditOperator> caos = caoDAO.find(a.getId(), caoStatus);

		if (caos.size() > 0) {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(templateID);
			emailBuilder.addToken("caos", caos);
			emailBuilder.setUser(new User(2357));

			email = emailBuilder.build();
			System.out.println(email.getBody());
		}

		return email;
	}
}
