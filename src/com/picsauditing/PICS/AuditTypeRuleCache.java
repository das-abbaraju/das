package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class AuditTypeRuleCache {

	private SafetyRisks data;
	private AuditDecisionTableDAO auditRuleDAO;

	public AuditTypeRuleCache(AuditDecisionTableDAO auditRuleDAO) {
		this.auditRuleDAO = auditRuleDAO;
	}

	public List<AuditTypeRule> getApplicableAuditRules(ContractorAccount contractor) {
		List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
		if (getData() == null)
			return null;

		Set<LowMedHigh> safetyRisks = new HashSet<LowMedHigh>();
		safetyRisks.add(null);
		safetyRisks.add(contractor.getSafetyRisk());

		Set<LowMedHigh> productRisks = new HashSet<LowMedHigh>();
		productRisks.add(null);
		productRisks.add(contractor.getProductRisk());

		Set<Boolean> acceptsBids = new HashSet<Boolean>();
		acceptsBids.add(null);
		acceptsBids.add(contractor.isAcceptsBids());

		Set<ContractorType> contractorType = new HashSet<ContractorType>();
		contractorType.add(null);
		contractorType.addAll(contractor.getAccountTypes());

		Set<Boolean> soleProprietors = new HashSet<Boolean>();
		soleProprietors.add(null);
		soleProprietors.add(contractor.getSoleProprietor());

		Set<Trade> trades = new HashSet<Trade>();
		trades.add(null);
		for (ContractorTrade ct : contractor.getTrades()) {
			trades.add(ct.getTrade());
		}

		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
		operators.add(null);
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			operators.add(co.getOperatorAccount());
			// adding parent facilities
			for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
				operators.add(f.getCorporate());
			}
		}

		for (LowMedHigh safetyRisk : safetyRisks) {
			ProductRisks data2 = getData().getData(safetyRisk);
			if (data2 != null) {
				for (LowMedHigh productRisk : productRisks) {
					AcceptsBids dataZ = data2.getData(productRisk);
					if (dataZ != null) {
						for (Boolean acceptsBid : acceptsBids) {
							ContractorTypes data3 = dataZ.getData(acceptsBid);
							if (data3 != null) {
								for (ContractorType conType : contractorType) {
									SoleProprietors dataX = data3.getData(conType);
									if (dataX != null) {
										for (Boolean soleProprietor : soleProprietors) {
											Trades dataY = dataX.getData(soleProprietor);
											if (dataY != null) {
												for (Trade t : trades) {
													Operators data4 = dataY.getData(t);
													if (data4 != null) {
														for (OperatorAccount o : operators) {
															OperatorAccount operator = o;
															Set<AuditTypeRule> data6 = data4.getData(operator);
															if (data6 != null) {
																for (AuditTypeRule auditTypeRule : data6) {
																	/*
																	 * boolean specificContractorRule = (conType != null
																	 * && );
																	 */
																	if (auditTypeRule.isInclude())
																		rules.add(auditTypeRule);
																	else {
																		/*
																		 * Exclude rules can be tricky if they are
																		 * specific We could also add in functionality
																		 * to support dependent question sets here are
																		 * well 12/2010 Please discuss with both Trevor
																		 * and Keerthi before changing this logic
																		 */
																		if (conType == null)
																			rules.add(auditTypeRule);
																		else if (contractorType.size() == 2)
																			/*
																			 * This contractor has only one type so
																			 * include the "exclusion rule"
																			 */
																			rules.add(auditTypeRule);
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

	public SafetyRisks getData() {
		if (data == null) {
			data = new SafetyRisks();
			for (AuditTypeRule rule : auditRuleDAO.findAuditTypeRules()) {
				data.add(rule);
			}
		}
		return data;
	}

	public void clear() {
		data = null;
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

	private class Operators {

		private Map<OperatorAccount, Set<AuditTypeRule>> data = new LinkedHashMap<OperatorAccount, Set<AuditTypeRule>>();

		public Set<AuditTypeRule> getData(OperatorAccount value) {
			return data.get(value);
		}

		public void add(AuditTypeRule rule) {
			Set<AuditTypeRule> map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new LinkedHashSet<AuditTypeRule>();
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

	public String print() {
		return "";
	}
}
