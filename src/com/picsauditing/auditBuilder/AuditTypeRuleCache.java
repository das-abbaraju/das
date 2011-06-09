package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class AuditTypeRuleCache extends AuditRuleCache {

	private SafetyRisks data;

	protected class Contractor extends AuditRuleCache.Contractor {
		public Contractor(ContractorAccount contractor) {
			super(contractor);
		}
	}

	public List<AuditTypeRule> getRules(ContractorAccount contractor) {
		List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
		if (getData() == null)
			return null;

		Contractor contractor2 = new Contractor(contractor);

		for (LowMedHigh safetyRisk : contractor2.safetyRisks) {
			// System.out.println("safetyRisk = " + safetyRisk);
			ProductRisks data2 = getData().getData(safetyRisk);
			if (data2 != null) {
				for (LowMedHigh productRisk : contractor2.productRisks) {
					// System.out.println("  productRisk = " + safetyRisk);
					AccountLevels data3 = data2.getData(productRisk);
					if (data3 != null) {
						for (AccountLevel accountLevel : contractor2.accountLevels) {
							// System.out.println("    accountLevel = " + accountLevel);
							ContractorTypes data7 = data3.getData(accountLevel);
							if (data7 != null) {
								for (ContractorType conType : contractor2.contractorType) {
									// System.out.println("      conType = " + conType);
									SoleProprietors dataX = data7.getData(conType);
									if (dataX != null) {
										for (Boolean soleProprietor : contractor2.soleProprietors) {
											// System.out.println("        soleProprietor = " + soleProprietor);
											Trades dataY = dataX.getData(soleProprietor);
											if (dataY != null) {
												for (Trade trade : contractor2.trades) {
													// System.out.println("          trade = " + trade);
													Operators data4 = dataY.getData(trade);
													if (data4 != null) {
														for (OperatorAccount operator : contractor2.operators) {
															// System.out.println("            operator = " + operator);
															Set<AuditRule> data6 = data4.getData(operator);
															if (data6 != null) {
																for (AuditRule rule : data6) {
																	// System.out.println("              rule = " +
																	// rule);
																	rules.add((AuditTypeRule) rule);
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

	public void initialize(List<AuditTypeRule> rules) {
		data = new SafetyRisks();
		for (AuditTypeRule rule : rules) {
			data.add(rule);
		}
	}

	public void initialize(AuditDecisionTableDAO auditRuleDAO) {
		if (data == null) {
			long startTime = System.currentTimeMillis();
			initialize(auditRuleDAO.findAuditTypeRules());
			long endTime = System.currentTimeMillis();
			System.out.println("Filled AuditTypeRuleCache in " + (endTime - startTime) + "ms");
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

		public void add(AuditTypeRule rule) {
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

		public void add(AuditTypeRule rule) {
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

		public void add(AuditTypeRule rule) {
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

		public void add(AuditTypeRule rule) {
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

		public void add(AuditTypeRule rule) {
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
					System.out.println("         related to " + trade);
					operator.add(data.get(trade));
				}
			}
			operator.add(data.get(value));
			return operator;
		}
		
		public void add(AuditTypeRule rule) {
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
}
