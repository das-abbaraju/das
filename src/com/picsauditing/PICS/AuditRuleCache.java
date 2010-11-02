package com.picsauditing.PICS;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditRuleCache {

	private Risks data = null;

	public void fill(Collection<AuditCategoryRule> rules) {
		data = new Risks();
		for (AuditCategoryRule rule : rules) {
			data.add(rule);
		}
	}

	public Set<AuditCategoryRule> getApplicable(ContractorAccount contractor) {
		if (data == null)
			return null;

		Set<LowMedHigh> risks = new HashSet<LowMedHigh>();
		risks.add(null);
		risks.add(contractor.getRiskLevel());
		Set<AuditType> auditTypes = new HashSet<AuditType>();
		auditTypes.add(null);

		Set<AuditCategoryRule> rules = new HashSet<AuditCategoryRule>();

		for (LowMedHigh risk : risks) {
			for (AuditType auditType : auditTypes) {
				for (OperatorAccount operator : contractor.getOperatorAccounts()) {
					rules.addAll(data.getData(risk).getData(auditType).getData(operator));
				}
			}
		}
		// public ContractorType contractorType;
		// public Boolean acceptsBids;

		return rules;
	}
	
	private class Params {
		
	}

	private class Risks {

		private Map<LowMedHigh, AuditTypes> data = new HashMap<LowMedHigh, AuditTypes>();

		public AuditTypes getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			AuditTypes map = data.get(rule.getRisk());
			if (map == null) {
				map = new AuditTypes();
				data.put(rule.getRisk(), map);
			}
			map.add(rule);
		}
	}

	private class AuditTypes {

		private Map<AuditType, Operators> data = new HashMap<AuditType, Operators>();

		public Operators getData(AuditType value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			Operators map = data.get(rule.getAuditType());
			if (map == null) {
				map = new Operators();
				data.put(rule.getAuditType(), map);
			}
			map.add(rule);
		}
	}

	private class Operators {

		private Map<OperatorAccount, Set<AuditCategoryRule>> data = new HashMap<OperatorAccount, Set<AuditCategoryRule>>();

		public Set<AuditCategoryRule> getData(OperatorAccount value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			Set<AuditCategoryRule> map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new HashSet<AuditCategoryRule>();
				data.put(rule.getOperatorAccount(), map);
			}
			map.add(rule);
		}
	}

	private interface RuleParam {

	}
}
