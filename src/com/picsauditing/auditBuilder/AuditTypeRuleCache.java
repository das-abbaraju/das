package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
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
			ProductRisks data2 = getData().getData(safetyRisk);
			if (data2 != null) {
				for (LowMedHigh productRisk : contractor2.productRisks) {
					AcceptsBids dataZ = data2.getData(productRisk);
					if (dataZ != null) {
						for (Boolean acceptsBid : contractor2.acceptsBids) {
							ContractorTypes data3 = dataZ.getData(acceptsBid);
							if (data3 != null) {
								for (ContractorType conType : contractor2.contractorType) {
									SoleProprietors dataX = data3.getData(conType);
									if (dataX != null) {
										for (Boolean soleProprietor : contractor2.soleProprietors) {
											Trades dataY = dataX.getData(soleProprietor);
											if (dataY != null) {
												for (Trade t : contractor2.trades) {
													Operators data4 = dataY.getData(t);
													if (data4 != null) {
														for (OperatorAccount o : contractor2.operators) {
															OperatorAccount operator = o;
															Set<AuditRule> data6 = data4.getData(operator);
															if (data6 != null) {
																for (AuditRule rule : data6) {
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
	}

	private class ProductRisks {

		private Map<LowMedHigh, AcceptsBids> data = new LinkedHashMap<LowMedHigh, AcceptsBids>();

		public AcceptsBids getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(AuditTypeRule rule) {
			AcceptsBids map = data.get(rule.getProductRisk());
			if (map == null) {
				map = new AcceptsBids();
				data.put(rule.getProductRisk(), map);
			}
			map.add(rule);
		}
	}

	private class AcceptsBids {

		private Map<Boolean, ContractorTypes> data = new LinkedHashMap<Boolean, ContractorTypes>();

		public ContractorTypes getData(Boolean value) {
			return data.get(value);
		}

		public void add(AuditTypeRule rule) {
			ContractorTypes map = data.get(rule.getAcceptsBids());
			if (map == null) {
				map = new ContractorTypes();
				data.put(rule.getAcceptsBids(), map);
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
			return data.get(value);
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
