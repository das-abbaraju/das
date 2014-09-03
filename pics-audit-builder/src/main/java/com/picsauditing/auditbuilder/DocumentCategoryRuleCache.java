package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.DocumentCategoryRule;
import com.picsauditing.auditbuilder.entities.AuditType;
import com.picsauditing.auditbuilder.entities.ContractorAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentCategoryRuleCache extends DocumentRuleCache<DocumentCategoryRule> {
	private AuditTypes data;
	private final Logger logger = LoggerFactory.getLogger(DocumentCategoryRuleCache.class);

	public List<DocumentCategoryRule> getRules(ContractorAccount contractor, AuditType auditType) {
		if (getData() == null)
			return null;

		RuleFilter contractorFilter = new RuleFilter(contractor);
		contractorFilter.addAuditType(auditType);

		List<DocumentCategoryRule> rules = getData().next(contractorFilter);

		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	private void initialize(List<DocumentCategoryRule> rules) {
		data = new AuditTypes();
		for (DocumentCategoryRule rule : rules) {
			if (rule.getId() > 30470)
				logger.info("rule# {}: {}", rule.getId(), rule);
			data.add(rule);
		}
	}

	synchronized void initialize() {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(auditDecisionTableDAO.findAllRules(DocumentCategoryRule.class));
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

	private class AuditTypes extends RuleCacheLevel<AuditType, SafetyRisks, DocumentCategoryRule> {

		public void add(DocumentCategoryRule rule) {
			SafetyRisks map = data.get(rule.getAuditType());
			if (map == null) {
				map = new SafetyRisks();
				data.put(rule.getAuditType(), map);
			}
			map.add(rule);
		}

		@Override
		public List<DocumentCategoryRule> next(RuleFilter contractor) {
			List<DocumentCategoryRule> rules = new ArrayList<>();
			for (AuditType auditType : contractor.auditTypes) {
				SafetyRisks safetyRisks = data.get(auditType);
				if (safetyRisks != null)
					rules.addAll(safetyRisks.next(contractor));
			}
			return rules;
		}
	}
}