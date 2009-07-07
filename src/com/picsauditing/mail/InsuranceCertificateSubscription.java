package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

public class InsuranceCertificateSubscription extends SubscriptionBuilder {

	private ContractorAuditOperatorDAO caoDAO;
	private CaoStatus caoStatus;

	public InsuranceCertificateSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, ContractorAuditOperatorDAO caoDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.caoDAO = caoDAO;
	}

	@Override
	protected void setup(Account a) {
		if (subscription.equals(Subscription.PendingInsuranceCerts)) {
			caoStatus = CaoStatus.Pending;
		}
		if (subscription.equals(Subscription.VerifiedInsuranceCerts)) {
			caoStatus = CaoStatus.Verified;
		}
		templateID = 61;
		
		// We may need to use the inheritance for insurance
		List<ContractorAuditOperator> caos = caoDAO.find(a.getId(), caoStatus);
		
		tokens.put("caos", caos);
		tokens.put("caoStatus", caoStatus);
	}
}
