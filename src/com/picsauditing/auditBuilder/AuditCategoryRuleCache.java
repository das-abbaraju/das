package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class AuditCategoryRuleCache extends AuditRuleCache {

	private SafetyRisks data;

	protected class Contractor extends AuditRuleCache.Contractor {
		public Contractor(ContractorAccount contractor) {
			super(contractor);
		}
	}

	public List<AuditCategoryRule> getRules(ContractorAccount contractor, AuditType auditType) {
		Contractor contractor2 = new Contractor(contractor);

		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
		for (AuditRule rule : super.getRules(contractor2)) {
			rules.add((AuditCategoryRule) rule);
		}

		Set<AuditType> auditTypes = new HashSet<AuditType>();
		auditTypes.add(null);
		if (auditType != null)
			auditTypes.add(auditType);

		for (LowMedHigh safetyRisk : contractor2.safetyRisks) {
			ProductRisks data2 = getData().getData(safetyRisk);
			if (data2 != null) {
				for (LowMedHigh productRisk : contractor2.productRisks) {
					AcceptsBids dataZ = data2.getData(productRisk);
					if (dataZ != null) {
						for (Boolean acceptsBid : contractor2.acceptsBids) {
							AuditTypes data3 = dataZ.getData(acceptsBid);
							if (data3 != null) {
								for (AuditType auditType2 : auditTypes) {
									ContractorTypes data4 = data3.getData(auditType2);
									if (data4 != null) {
										for (ContractorType conType : contractor2.contractorType) {
											SoleProprietors dataX = data4.getData(conType);
											if (dataX != null) {
												for (Boolean soleProprietor : contractor2.soleProprietors) {
													Trades dataY = dataX.getData(soleProprietor);
													if (dataY != null) {
														for (Trade t : contractor2.trades) {
															Operators data5 = dataY.getData(t);
															if (data5 != null) {
																for (OperatorAccount o : contractor2.operators) {
																	OperatorAccount operator = (o == null ? null : o);
																	Set<AuditRule> data6 = data5.getData(operator);
																	if (data6 != null) {
																		for (AuditRule auditRule : data6) {
																			rules.add((AuditCategoryRule) auditRule);
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
			}
		}

		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	public void initialize(List<AuditCategoryRule> rules) {
		data = new SafetyRisks();
		for (AuditCategoryRule rule : rules) {
			rule.calculatePriority();
			data.add(rule);
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

		public void add(AuditCategoryRule rule) {
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

		public void add(AuditCategoryRule rule) {
			AcceptsBids map = data.get(rule.getProductRisk());
			if (map == null) {
				map = new AcceptsBids();
				data.put(rule.getProductRisk(), map);
			}
			map.add(rule);
		}
	}

	private class AcceptsBids {

		private Map<Boolean, AuditTypes> data = new LinkedHashMap<Boolean, AuditTypes>();

		public AuditTypes getData(Boolean value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			AuditTypes map = data.get(rule.getAcceptsBids());
			if (map == null) {
				map = new AuditTypes();
				data.put(rule.getAcceptsBids(), map);
			}
			map.add(rule);
		}
	}

	private class AuditTypes {

		private Map<AuditType, ContractorTypes> data = new LinkedHashMap<AuditType, ContractorTypes>();

		public ContractorTypes getData(AuditType value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			ContractorTypes map = data.get(rule.getAuditType());
			if (map == null) {
				map = new ContractorTypes();
				data.put(rule.getAuditType(), map);
			}
			map.add(rule);
		}
	}

	private class ContractorTypes {

		private Map<ContractorType, SoleProprietors> data = new LinkedHashMap<ContractorType, SoleProprietors>();

		public SoleProprietors getData(ContractorType value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
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

		public void add(AuditCategoryRule rule) {
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

		public void add(AuditCategoryRule rule) {
			Operators map = data.get(rule.getTrade());
			if (map == null) {
				map = new Operators();
				data.put(rule.getTrade(), map);
			}
			map.add(rule);
		}
	}

	public String print() {
		return "";
	}
}
