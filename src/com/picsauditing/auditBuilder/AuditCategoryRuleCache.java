package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;

public class AuditCategoryRuleCache extends AuditRuleCache<AuditCategoryRule> {

	private AuditTypes data;
	private final Logger logger = LoggerFactory.getLogger(AuditCategoryRuleCache.class);
	
	public List<AuditCategoryRule> getRules(ContractorAccount contractor, AuditType auditType) {
		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
		if (getData() == null)
			return null;

		RuleFilter contractorFilter = new RuleFilter(contractor);
		contractorFilter.addAuditType(auditType);

		rules = getData().next(contractorFilter);

		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	public void initialize(List<AuditCategoryRule> rules) {
		data = new AuditTypes();
		for (AuditCategoryRule rule : rules) {
			if (rule.getId() > 30470)
				System.out.println("rule#" + rule.getId() + ": " + rule);
			data.add(rule);
		}
	}

	public void initialize(AuditDecisionTableDAO dao) {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(dao.findAllRules(AuditCategoryRule.class));
			long endTime = System.currentTimeMillis();
			logger.info("Filled AuditCategoryRuleCache in {} ms", (endTime - startTime));
		}
	}

	public void clear() {
		data = null;
	}

	private AuditTypes getData() {
		if (data == null)
			throw new RuntimeException("No rules were found. Please initialize() before getting data.");
		return data;
	}

	private class AuditTypes extends RuleCacheLevel<AuditType, SafetyRisks, AuditCategoryRule> {

		public void add(AuditCategoryRule rule) {
			SafetyRisks map = data.get(rule.getAuditType());
			if (map == null) {
				map = new SafetyRisks();
				data.put(rule.getAuditType(), map);
			}
			map.add(rule);
		}

		@Override
		public List<AuditCategoryRule> next(RuleFilter contractor) {
			List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
			for (AuditType auditType : contractor.auditTypes) {
				SafetyRisks safetyRisks = data.get(auditType);
				if (safetyRisks != null)
					rules.addAll(safetyRisks.next(contractor));
			}
			return rules;
		}
	}

}
