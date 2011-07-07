package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class AuditRuleCache<R extends AuditRule> {

	private class FilterRule implements RuleFilterable<R> {
		List<R> rules;

		public void add(R rule) {
			if (rules == null) {
				rules = new ArrayList<R>();
			}
			rules.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			return rules;
		}
	}

	protected class SafetyRisks extends RuleCacheLevel<LowMedHigh, ProductRisks, R> {

		public void add(R rule) {
			ProductRisks map = data.get(rule.getSafetyRisk());
			if (map == null) {
				map = new ProductRisks();
				data.put(rule.getSafetyRisk(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (LowMedHigh risk : contractor.safetyRisks) {
				rules.addAll(data.get(risk).next(contractor));
			}
			return rules;
		}
	}

	protected class ProductRisks extends RuleCacheLevel<LowMedHigh, AccountLevels, R> {

		public void add(R rule) {
			AccountLevels map = data.get(rule.getProductRisk());
			if (map == null) {
				map = new AccountLevels();
				data.put(rule.getProductRisk(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (LowMedHigh risk : contractor.productRisks) {
				rules.addAll(data.get(risk).next(contractor));
			}
			return rules;
		}

	}

	protected class AccountLevels extends RuleCacheLevel<AccountLevel, ContractorTypes, R> {

		public void add(R rule) {
			ContractorTypes map = data.get(rule.getAccountLevel());
			if (map == null) {
				map = new ContractorTypes();
				data.put(rule.getAccountLevel(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (AccountLevel level : contractor.accountLevels) {
				rules.addAll(data.get(level).next(contractor));
			}
			return rules;
		}
	}

	protected class ContractorTypes extends RuleCacheLevel<ContractorType, SoleProprietors, R> {

		public void add(R rule) {
			SoleProprietors map = data.get(rule.getContractorType());
			if (map == null) {
				map = new SoleProprietors();
				data.put(rule.getContractorType(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (ContractorType type : contractor.contractorType) {
				rules.addAll(data.get(type).next(contractor));
			}
			return rules;
		}
	}

	protected class SoleProprietors extends RuleCacheLevel<Boolean, Trades, R> {

		public void add(R rule) {
			Trades map = data.get(rule.getSoleProprietor());
			if (map == null) {
				map = new Trades();
				data.put(rule.getSoleProprietor(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (LowMedHigh risk : contractor.productRisks) {
				rules.addAll(data.get(risk).next(contractor));
			}
			return rules;
		}
	}

	protected class Trades extends RuleCacheLevel<Trade, Operators, R> {

		public Operators getData(Trade value) {
			Operators operator = new Operators();
			for (Trade trade : data.keySet()) {
				if (value != null && trade != null && (value.childOf(trade) || trade.childOf(value))) {
					System.out.println("         related to " + trade);
					operator.add(data.get(trade));
				}
			}
			operator.add(data.get(value));
			return operator;
		}

		public void add(R rule) {
			Operators map = data.get(rule.getTrade());
			if (map == null) {
				map = new Operators();
				data.put(rule.getTrade(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (Trade trade : contractor.trades) {
				rules.addAll(data.get(trade).next(contractor));
			}
			return rules;
		}
	}

	protected class Operators extends RuleCacheLevel<OperatorAccount, FilterRule, R> {

		public void add(R rule) {
			FilterRule map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new FilterRule();
				data.put(rule.getOperatorAccount(), map);
			}
			{
				/*
				 * Trying to ensure that needed objects are loaded in memory before they are cached so that when they
				 * are referenced later, lazy initializations do not occur
				 */
				if (rule.getOperatorAccount() != null)
					rule.getOperatorAccount().getCorporateFacilities();
				if (rule.getQuestion() != null)
					rule.getQuestion().getAuditType();
			}
			map.add(rule);
		}

		public void add(Operators operators) {
			if (operators == null)
				return;
			for (FilterRule operatorRules : operators.data.values()) {
				for (R rule : operatorRules.rules) {
					add(rule);
				}
			}
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<R>();
			for (OperatorAccount operator : contractor.operators) {
				rules.addAll(data.get(operator).next(contractor));
			}
			return rules;
		}
	}

}
