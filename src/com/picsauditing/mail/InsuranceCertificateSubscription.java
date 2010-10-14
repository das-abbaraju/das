package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class InsuranceCertificateSubscription extends SubscriptionBuilder {

	private ContractorAuditOperatorDAO caoDAO;
	private AuditStatus caoStatus;

	public InsuranceCertificateSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, ContractorAuditOperatorDAO caoDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.caoDAO = caoDAO;
		this.templateID = 61;
	}

	@Override
	protected void setup(Account a) {
		if (subscription.equals(Subscription.PendingInsuranceCerts)) {
			caoStatus = AuditStatus.Pending;
		}
		if (subscription.equals(Subscription.VerifiedInsuranceCerts)) {
			caoStatus = AuditStatus.Complete;
		}

		if (a instanceof OperatorAccount) {
			OperatorAccount o = (OperatorAccount) a;
			// We may need to use the inheritance for insurance
			List<ContractorAuditOperator> caos = caoDAO.find(o.getOperatorHeirarchy().get(0), caoStatus, timePeriod
					.getComparisonDate(), now);

			if (caos.size() > 0) {
				tokens.put("caos", caos);
				tokens.put("caoStatus", caoStatus);
			}
		}
	}
}
