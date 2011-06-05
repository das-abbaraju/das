package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class AuditRuleCache {

	protected class Contractor {
		public Set<LowMedHigh> safetyRisks = new HashSet<LowMedHigh>();
		public Set<LowMedHigh> productRisks = new HashSet<LowMedHigh>();
		public Set<Boolean> acceptsBids = new HashSet<Boolean>();
		public Set<ContractorType> contractorType = new HashSet<ContractorType>();
		public Set<Boolean> soleProprietors = new HashSet<Boolean>();
		public Set<Trade> trades = new HashSet<Trade>();
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();

		public Contractor(ContractorAccount contractor) {
			safetyRisks.add(null);
			safetyRisks.add(contractor.getSafetyRisk());

			productRisks.add(null);
			productRisks.add(contractor.getProductRisk());

			acceptsBids.add(null);
			acceptsBids.add(contractor.isAcceptsBids());

			contractorType.add(null);
			contractorType.addAll(contractor.getAccountTypes());

			soleProprietors.add(null);
			soleProprietors.add(contractor.getSoleProprietor());

			trades.add(null);
			for (ContractorTrade ct : contractor.getTrades()) {
				trades.add(ct.getTrade());
			}

			operators.add(null);
			for (ContractorOperator co : contractor.getNonCorporateOperators()) {
				operators.add(co.getOperatorAccount());
				// adding parent facilities
				for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
					operators.add(f.getCorporate());
				}
			}
		}
	}

	protected List<AuditRule> getRules(Contractor contractor) {
		List<AuditRule> rules = new ArrayList<AuditRule>();
		return rules;
	}

	protected class SafetyRisks {

		private Map<LowMedHigh, Operators> data = new LinkedHashMap<LowMedHigh, Operators>();

		public Operators getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(AuditRule rule) {
			Operators map = data.get(rule.getSafetyRisk());
			if (map == null) {
				map = new Operators();
				data.put(rule.getSafetyRisk(), map);
			}
			map.add(rule);
		}
	}

	protected class Operators {

		private Map<OperatorAccount, Set<AuditRule>> data = new LinkedHashMap<OperatorAccount, Set<AuditRule>>();

		public Set<AuditRule> getData(OperatorAccount value) {
			return data.get(value);
		}

		public void add(AuditRule rule) {
			Set<AuditRule> map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new LinkedHashSet<AuditRule>();
				data.put(rule.getOperatorAccount(), map);
			}
			// Trying to ensure that needed objects are loaded in memory before
			// they are cached so that when they are referenced later, lazy
			// initializations do not occur
			if (rule.getOperatorAccount() != null)
				rule.getOperatorAccount().getCorporateFacilities();
			if (rule.getQuestion() != null)
				rule.getQuestion().getAuditType();
			map.add(rule);
		}
	}

}
