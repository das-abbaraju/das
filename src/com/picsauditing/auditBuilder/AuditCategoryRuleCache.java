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
import org.springframework.beans.factory.annotation.Autowired;

public class AuditCategoryRuleCache extends AuditRuleCache<AuditCategoryRule> {
	private AuditTypes data;
	private final Logger logger = LoggerFactory.getLogger(AuditCategoryRuleCache.class);
	
	public List<AuditCategoryRule> getRules(ContractorAccount contractor, AuditType auditType) {
		if (getData() == null)
			return null;

		RuleFilter contractorFilter = new RuleFilter(contractor);
		contractorFilter.addAuditType(auditType);

		List<AuditCategoryRule> rules = getData().next(contractorFilter);

		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	private void initialize(List<AuditCategoryRule> rules) {
		data = new AuditTypes();
		for (AuditCategoryRule rule : rules) {
			if (rule.getId() > 30470)
				logger.info("rule# {}: {}", rule.getId(), rule);
			data.add(rule);
		}
	}

	synchronized void initialize() {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(auditDecisionTableDAO.findAllRules(AuditCategoryRule.class));
			long endTime = System.currentTimeMillis();
			logger.info("Filled AuditCategoryRuleCache in {} ms", (endTime - startTime));
		}
	}

	public synchronized void clear() {
		data = null;
        initialize();
	}

	private AuditTypes getData() {
		if (data == null) {
            initialize();
        }
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
