package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditTypeRuleCache extends AuditRuleCache<AuditTypeRule> {
	private SafetyRisks data;

	private final Logger logger = LoggerFactory.getLogger(AuditTypeRuleCache.class);
	
	public List<AuditTypeRule> getRules(ContractorAccount contractor) {
		List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
		if (getData() == null)
			return null;

		RuleFilter contractorFilter = new RuleFilter(contractor);
		rules = getData().next(contractorFilter);
		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	private void initialize(List<AuditTypeRule> rules) {
		data = new SafetyRisks();
		for (AuditTypeRule rule : rules) {
			data.add(rule);
		}
	}

	synchronized void initialize() {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(auditDecisionTableDAO.findAllRules(AuditTypeRule.class));
			long endTime = System.currentTimeMillis();
			logger.info("Filled AuditTypeRuleCache in {} ms", (endTime - startTime));
		}
	}

	public synchronized void clear() {
		data = null;
        initialize();
	}

	private SafetyRisks getData() {
		if (data == null) {
            initialize();
        }
		return data;
	}
}
