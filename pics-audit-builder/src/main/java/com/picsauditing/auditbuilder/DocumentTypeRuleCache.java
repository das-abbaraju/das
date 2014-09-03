package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.DocumentTypeRule;
import com.picsauditing.auditbuilder.entities.ContractorAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class DocumentTypeRuleCache extends DocumentRuleCache<DocumentTypeRule> {
	private SafetyRisks data;

	private final Logger logger = LoggerFactory.getLogger(DocumentTypeRuleCache.class);

	public List<DocumentTypeRule> getRules(ContractorAccount contractor) {
		List<DocumentTypeRule> rules;
		if (getData() == null)
			return null;

		RuleFilter contractorFilter = new RuleFilter(contractor);
		rules = getData().next(contractorFilter);
		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	private void initialize(List<DocumentTypeRule> rules) {
		data = new SafetyRisks();
		for (DocumentTypeRule rule : rules) {
			data.add(rule);
		}
	}

	synchronized void initialize() {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(auditDecisionTableDAO.findAllRules(DocumentTypeRule.class));
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