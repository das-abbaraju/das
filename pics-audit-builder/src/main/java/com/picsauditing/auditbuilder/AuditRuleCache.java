package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.AuditDecisionTableDAO2;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.AuditService;
import com.picsauditing.auditbuilder.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class AuditRuleCache<R extends AuditRule> {
    @Autowired
    protected AuditDecisionTableDAO2 auditDecisionTableDAO;

    private class FilterRule implements RuleFilterable<R> {
		List<R> rules;

		public void add(R rule) {
			if (rules == null) {
				rules = new ArrayList<>();
			}
			rules.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			return rules;
		}
	}

    protected class SafetySensitives extends RuleCacheLevel<Boolean, ProductRisks, R> {

        public void add(R rule) {
            ProductRisks map = data.get(rule.getSafetySensitive());
            if (map == null) {
                map = new ProductRisks();
                data.put(rule.getSafetySensitive(), map);
            }
            map.add(rule);
        }

        @Override
        public List<R> next(RuleFilter contractor) {
            List<R> rules = new ArrayList<>();
            for (Boolean sensitive : contractor.safetySensitives) {
                ProductRisks productRisks = data.get(sensitive);
                if (productRisks != null)
                    rules.addAll(productRisks.next(contractor));
            }
            return rules;
        }
    }

    protected class SafetyRisks extends RuleCacheLevel<LowMedHigh, SafetySensitives, R> {

		public void add(R rule) {
            SafetySensitives map = data.get(rule.getSafetyRisk());
			if (map == null) {
				map = new SafetySensitives();
				data.put(rule.getSafetyRisk(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<>();
			for (LowMedHigh risk : contractor.safetyRisks) {
                SafetySensitives sensitive = data.get(risk);
				if (sensitive != null)
					rules.addAll(sensitive.next(contractor));
			}
			return rules;
		}
	}

	protected class ProductRisks extends RuleCacheLevel<LowMedHigh, TradeSafetyRisks, R> {

		public void add(R rule) {
			TradeSafetyRisks map = data.get(rule.getProductRisk());
			if (map == null) {
				map = new TradeSafetyRisks();
				data.put(rule.getProductRisk(), map);
			}
			map.add(rule);
		}

		@Override
		public List<R> next(RuleFilter contractor) {
			List<R> rules = new ArrayList<>();
			for (LowMedHigh risk : contractor.productRisks) {
				TradeSafetyRisks TradeSafetyRisks = data.get(risk);
				if (TradeSafetyRisks != null)
					rules.addAll(TradeSafetyRisks.next(contractor));
			}
			return rules;
		}
	}

    protected class TradeSafetyRisks extends RuleCacheLevel<LowMedHigh, AccountLevels, R> {

        public void add(R rule) {
            AccountLevels map = data.get(rule.getTradeSafetyRisk());
            if (map == null) {
                map = new AccountLevels();
                data.put(rule.getTradeSafetyRisk(), map);
            }
            map.add(rule);
        }

        @Override
        public List<R> next(RuleFilter contractor) {
            List<R> rules = new ArrayList<>();
            for (LowMedHigh risk : contractor.tradeSafetyRisks) {
                AccountLevels accountLevels = data.get(risk);
                if (accountLevels != null)
                    rules.addAll(accountLevels.next(contractor));
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
			List<R> rules = new ArrayList<>();
			for (AccountLevel level : contractor.accountLevels) {
				ContractorTypes contractorTypes = data.get(level);
				if (contractorTypes != null)
					rules.addAll(contractorTypes.next(contractor));
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
			List<R> rules = new ArrayList<>();
			for (ContractorType type : contractor.contractorType) {
				SoleProprietors soleProprietors = data.get(type);
				if (soleProprietors != null)
					rules.addAll(soleProprietors.next(contractor));
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
			List<R> rules = new ArrayList<>();
			for (Boolean sole : contractor.soleProprietors) {
				Trades trades = data.get(sole);
				if (trades != null)
					rules.addAll(trades.next(contractor));
			}
			return rules;
		}
	}

	protected class Trades extends RuleCacheLevel<Trade, Operators, R> {

		public Operators getData(Trade value) {
			Operators operator = new Operators();
			for (Trade trade : data.keySet()) {
				if (value != null && trade != null && (TradeService.childOf(value,trade))) {
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
				Operators operators = getData(trade);
				if (operators != null)
					rules.addAll(operators.next(contractor));
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
				if (rule.getOperatorAccount() != null)
					rule.getOperatorAccount().getCorporateFacilities();
				if (rule.getQuestion() != null)
					AuditService.getAuditType(rule.getQuestion());
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
			List<R> rules = new ArrayList<>();
			for (OperatorAccount operator : contractor.operators) {
				FilterRule filterRule = data.get(operator);
				if (filterRule != null)
					rules.addAll(filterRule.next(contractor));
			}
			return rules;
		}
	}

    abstract void initialize();
}