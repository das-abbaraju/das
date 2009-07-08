package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class InsuranceCertificateSubscription extends SubscriptionBuilder {

	private ContractorAuditOperatorDAO caoDAO;
	private OperatorAccountDAO opDAO;
	private CaoStatus caoStatus;

	public InsuranceCertificateSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, ContractorAuditOperatorDAO caoDAO, OperatorAccountDAO opDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.caoDAO = caoDAO;
		this.opDAO = opDAO;
		this.templateID = 61;
	}

	@Override
	protected void setup(Account a) {
		if (subscription.equals(Subscription.PendingInsuranceCerts)) {
			caoStatus = CaoStatus.Pending;
		}
		if (subscription.equals(Subscription.VerifiedInsuranceCerts)) {
			caoStatus = CaoStatus.Verified;
		}

		OperatorAccount o = opDAO.find(a.getId());

		if (o != null) {
			// We may need to use the inheritance for insurance
			List<ContractorAuditOperator> caos = caoDAO.find(o.getInheritInsurance().getId(), caoStatus, timePeriod
					.getComparisonDate(), now);

			if (caos.size() > 0) {
				tokens.put("caos", caos);
				tokens.put("caoStatus", caoStatus);
			}
		}
	}
}
