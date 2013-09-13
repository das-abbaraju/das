package com.picsauditing.mail.subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailSubscription;

public class CancelledScheduledAuditsSubscription extends SubscriptionBuilder {
	@Autowired
	private ContractorAuditDAO auditDAO;

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		List<ContractorAudit> data = new ArrayList<ContractorAudit>();
		data = auditDAO.findCancelledScheduledAudits();

		if (data.size() > 0) {
			tokens.put("audits", data);
		}

		return tokens;
	}
}
