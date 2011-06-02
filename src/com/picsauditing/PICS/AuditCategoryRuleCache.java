package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.Trade;

public class AuditCategoryRuleCache {

	private SafetyRisks data;
	private AuditDecisionTableDAO auditRuleDAO;

	public AuditCategoryRuleCache(AuditDecisionTableDAO auditRuleDAO) {
		this.auditRuleDAO = auditRuleDAO;
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAccount contractor, AuditType auditType) {
		Set<AuditType> audit = new HashSet<AuditType>();
		audit.add(auditType);
		return getApplicableCategoryRules(contractor, audit);
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAccount contractor,
			Collection<AuditType> auditTypes) {
		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
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

		Set<AuditType> auditTypes2 = new HashSet<AuditType>();
		auditTypes2.add(null);
		if (auditTypes != null)
			auditTypes2.addAll(auditTypes);

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
							AuditTypes data3 = dataZ.getData(acceptsBid);
							if (data3 != null) {
								for (AuditType auditType : auditTypes2) {
									ContractorTypes data4 = data3.getData(auditType);
									if (data4 != null) {
										for (ContractorType conType : contractorType) {
											SoleProprietors dataX = data4.getData(conType);
											if (dataX != null) {
												for (Boolean soleProprietor : soleProprietors) {
													Trades dataY = dataX.getData(soleProprietor);
													if (dataY != null) {
														for (Trade t : trades) {
															Operators data5 = dataY.getData(t);
															if (data5 != null) {
																for (OperatorAccount o : operators) {
																	OperatorAccount operator = (o == null ? null : o);
																	Set<AuditCategoryRule> data6 = data5
																			.getData(operator);
																	if (data6 != null) {
																		for (AuditCategoryRule auditCategoryRule : data6) {
																			/*
																			 * boolean specificContractorRule = (conType
																			 * != null);
																			 */
																			if (auditCategoryRule.isInclude())
																				rules.add(auditCategoryRule);
																			else {
																				/*
																				 * Exclude rules can be tricky if they
																				 * are specific We could also add in
																				 * functionality to support dependent
																				 * question sets here are well 12 /2010
																				 * Please discuss with both Trevor and
																				 * Keerthi before changing this logic
																				 */
																				if (conType == null)
																					rules.add(auditCategoryRule);
																				else if (contractorType.size() == 2)
																					/*
																					 * This contractor has only one type
																					 * so include the "exclusion rule"
																					 */
																					rules.add(auditCategoryRule);
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
		}

		Collections.sort(rules);
		Collections.reverse(rules);

		return rules;
	}

	public SafetyRisks getData() {
		if (data == null) {
			data = new SafetyRisks();
			for (AuditCategoryRule rule : auditRuleDAO.findCategoryRules()) {
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

	private class Operators {

		private Map<OperatorAccount, Set<AuditCategoryRule>> data = new LinkedHashMap<OperatorAccount, Set<AuditCategoryRule>>();

		public Set<AuditCategoryRule> getData(OperatorAccount value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			Set<AuditCategoryRule> map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new LinkedHashSet<AuditCategoryRule>();
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
