package com.picsauditing.flags;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagCriteriaRule;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class FlagRuleCache {

	private SafetyRisks data;

	private final Logger logger = LoggerFactory.getLogger(FlagRuleCache.class);
	
	public List<FlagCriteriaRule> getRules(ContractorOperator co) {
		List<FlagCriteriaRule> rules = new ArrayList<FlagCriteriaRule>();
		if (getData() == null)
			return null;

		Sample sample = new Sample(co);

		for (LowMedHigh safetyRisk : sample.safetyRisks) {
			ProductRisks data2 = getData().getData(safetyRisk);
			if (data2 != null) {
				for (LowMedHigh productRisk : sample.productRisks) {
					AccountLevels data3 = data2.getData(productRisk);
					if (data3 != null) {
						for (AccountLevel accountLevel : sample.accountLevels) {
							ContractorTypes data7 = data3.getData(accountLevel);
							if (data7 != null) {
								for (ContractorType conType : sample.contractorType) {
									SoleProprietors dataX = data7.getData(conType);
									if (dataX != null) {
										for (Boolean soleProprietor : sample.soleProprietors) {
											Trades dataY = dataX.getData(soleProprietor);
											if (dataY != null) {
												for (Trade trade : sample.trades) {
													Operators data4 = dataY.getData(trade);
													if (data4 != null) {
														for (OperatorAccount operator : sample.operators) {
															Set<FlagCriteriaRule> data6 = data4.getData(operator);
															if (data6 != null) {
																for (FlagCriteriaRule rule : data6) {
																	rules.add((FlagCriteriaRule) rule);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	public void initialize(List<? extends BaseDecisionTreeRule> list) {
		data = new SafetyRisks();
		for (BaseDecisionTreeRule rule : list) {
			data.add((FlagCriteriaRule) rule);
		}
	}

	public void initialize(AuditDecisionTableDAO dao) {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(dao.findAllRules(FlagCriteriaRule.class));
			long endTime = System.currentTimeMillis();
			logger.info("Filled FlagRuleCache in {} ms", (endTime - startTime));
		}
	}

	public SafetyRisks getData() {
		if (data == null)
			throw new RuntimeException("No rules were found. Please initialize() before getting data.");
		return data;
	}

	private class SafetyRisks {

		private Map<LowMedHigh, ProductRisks> data = new LinkedHashMap<LowMedHigh, ProductRisks>();

		public ProductRisks getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(FlagCriteriaRule rule) {
			ProductRisks map = data.get(rule.getSafetyRisk());
			if (map == null) {
				map = new ProductRisks();
				data.put(rule.getSafetyRisk(), map);
			}
			map.add(rule);
		}

		public String toString() {
			// Experimental
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (LowMedHigh risk : data.keySet()) {
				builder.append("{");
				builder.append(risk);
				builder.append("}");
			}
			builder.append("]");
			return builder.toString();
		}
	}

	private class ProductRisks {

		private Map<LowMedHigh, AccountLevels> data = new LinkedHashMap<LowMedHigh, AccountLevels>();

		public AccountLevels getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(FlagCriteriaRule rule) {
			AccountLevels map = data.get(rule.getProductRisk());
			if (map == null) {
				map = new AccountLevels();
				data.put(rule.getProductRisk(), map);
			}
			map.add(rule);
		}
	}

	private class AccountLevels {

		private Map<AccountLevel, ContractorTypes> data = new LinkedHashMap<AccountLevel, ContractorTypes>();

		public ContractorTypes getData(AccountLevel value) {
			return data.get(value);
		}

		public void add(FlagCriteriaRule rule) {
			ContractorTypes map = data.get(rule.getAccountLevel());
			if (map == null) {
				map = new ContractorTypes();
				data.put(rule.getAccountLevel(), map);
			}
			map.add(rule);
		}
	}

	private class ContractorTypes {

		private Map<ContractorType, SoleProprietors> data = new LinkedHashMap<ContractorType, SoleProprietors>();

		public SoleProprietors getData(ContractorType value) {
			return data.get(value);
		}

		public void add(FlagCriteriaRule rule) {
			SoleProprietors map = data.get(rule.getContractorType());
			if (map == null) {
				map = new SoleProprietors();
				data.put(rule.getContractorType(), map);
			}
			map.add(rule);
		}
	}

	private class SoleProprietors {

		private Map<Boolean, Trades> data = new LinkedHashMap<Boolean, Trades>();

		public Trades getData(Boolean value) {
			return data.get(value);
		}

		public void add(FlagCriteriaRule rule) {
			Trades map = data.get(rule.getSoleProprietor());
			if (map == null) {
				map = new Trades();
				data.put(rule.getSoleProprietor(), map);
			}
			map.add(rule);
		}
	}

	private class Trades {

		private Map<Trade, Operators> data = new LinkedHashMap<Trade, Operators>();

		public Operators getData(Trade value) {
			Operators operator = new Operators();
			for (Trade trade : data.keySet()) {
				if (value != null && trade != null && (value.childOf(trade) || trade.childOf(value))) {
					logger.debug("         related to {}", trade);
					operator.add(data.get(trade));
				}
			}
			operator.add(data.get(value));
			return operator;
		}

		public void add(FlagCriteriaRule rule) {
			Operators map = data.get(rule.getTrade());
			if (map == null) {
				map = new Operators();
				data.put(rule.getTrade(), map);
			}
			map.add(rule);
		}
	}

	public void clear() {
		data = null;
	}

	private class Operators {

		private Map<OperatorAccount, Set<FlagCriteriaRule>> data = new LinkedHashMap<OperatorAccount, Set<FlagCriteriaRule>>();

		public Set<FlagCriteriaRule> getData(OperatorAccount value) {
			return data.get(value);
		}

		public void add(FlagCriteriaRule rule) {
			Set<FlagCriteriaRule> map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new LinkedHashSet<FlagCriteriaRule>();
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
			for (Set<FlagCriteriaRule> operatorRules : operators.data.values()) {
				for (FlagCriteriaRule rule : operatorRules) {
					add(rule);
				}
			}
		}
	}

	private class Sample {
		public Set<LowMedHigh> safetyRisks = new HashSet<LowMedHigh>();
		public Set<LowMedHigh> productRisks = new HashSet<LowMedHigh>();
		public Set<ContractorType> contractorType = new HashSet<ContractorType>();
		public Set<Boolean> soleProprietors = new HashSet<Boolean>();
		public Set<AccountLevel> accountLevels = new HashSet<AccountLevel>();
		public Set<Trade> trades = new HashSet<Trade>();
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();

		public Sample(ContractorOperator co) {
			ContractorAccount contractor = co.getContractorAccount();
			safetyRisks.add(null);
			safetyRisks.add(contractor.getSafetyRisk());

			productRisks.add(null);
			productRisks.add(contractor.getProductRisk());

			contractorType.add(null);
			contractorType.addAll(contractor.getAccountTypes());

			soleProprietors.add(null);
			soleProprietors.add(contractor.getSoleProprietor());

			accountLevels.add(null);
			accountLevels.add(contractor.getAccountLevel());

			trades.add(null);
			for (ContractorTrade ct : contractor.getTrades()) {
				trades.add(ct.getTrade());
			}

			operators.add(null);
			operators.add(co.getOperatorAccount());
			// adding parent facilities
			for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
				operators.add(f.getCorporate());
			}
		}
	}

}
