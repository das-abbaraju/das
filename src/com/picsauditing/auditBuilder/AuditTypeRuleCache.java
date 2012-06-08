package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;

public class AuditTypeRuleCache extends AuditRuleCache<AuditTypeRule> {

	private SafetyRisks data;

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

	public void initialize(List<AuditTypeRule> rules) {
		data = new SafetyRisks();
		for (AuditTypeRule rule : rules) {
			data.add(rule);
		}
	}

	public void initialize(AuditDecisionTableDAO dao) {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(dao.findAllRules(AuditTypeRule.class));
			long endTime = System.currentTimeMillis();
			System.out.println("Filled AuditTypeRuleCache in " + (endTime - startTime) + "ms");
		}
	}

	public void clear() {
		data = null;
	}

	private SafetyRisks getData() {
		if (data == null)
			throw new RuntimeException("No rules were found. Please initialize() before getting data.");
		return data;
	}
}
