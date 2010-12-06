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
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditTypeRuleCache {

	private Risks data;
	private AuditDecisionTableDAO auditRuleDAO;

	public AuditTypeRuleCache(AuditDecisionTableDAO auditRuleDAO) {
		this.auditRuleDAO = auditRuleDAO;
	}

	public List<AuditTypeRule> getApplicableAuditRules(ContractorAccount contractor) {
//		PicsLogger.start("AuditTypeRuleCache", "Searching AuditTypeRuleCache for contractor " + contractor.getId());
		List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
		if (getData() == null)
			return null;

		Set<LowMedHigh> risks = new HashSet<LowMedHigh>();
		risks.add(null);
		risks.add(contractor.getRiskLevel());

		Set<Boolean> acceptsBids = new HashSet<Boolean>();
		acceptsBids.add(null);
		acceptsBids.add(contractor.isAcceptsBids());

		Set<ContractorType> contractorType = new HashSet<ContractorType>();
		contractorType.add(null);
		contractorType.addAll(contractor.getAccountTypes());

		Set<ContractorOperator> operators = new HashSet<ContractorOperator>();
		operators.add(null);
		operators.addAll(contractor.getOperators());

		for (LowMedHigh risk : risks) {
			AcceptsBids data2 = getData().getData(risk);
			if (data2 != null) {
//				PicsLogger.log("found matching risk " + risk);
				for (Boolean acceptsBid : acceptsBids) {
					ContractorTypes data3 = data2.getData(acceptsBid);
					if (data3 != null) {
//						PicsLogger.log(" found matching acceptsBid " + acceptsBid);
						for (ContractorType conType : contractorType) {
							Operators data4 = data3.getData(conType);
							if (data4 != null) {
//								PicsLogger.log("   found matching conType " + conType);
								for (ContractorOperator co : operators) {
									OperatorAccount operator = (co == null ? null : co.getOperatorAccount());
									Set<AuditTypeRule> data6 = data4.getData(operator);
									if (data6 != null) {
//										PicsLogger.log("    found matching operator " + operator);
										for (AuditTypeRule auditTypeRule : data6) {
											//boolean specificContractorRule = (conType != null && );
											if (auditTypeRule.isInclude())
												rules.add(auditTypeRule);
											else {
												// Exclude rules can be tricky if they are specific
												// We could also add in functionality to support dependent question sets here are well
												// 12/2010 Please discuss with both Trevor and Keerthi before changing this logic
												if (conType == null)
													rules.add(auditTypeRule);
												else if (contractorType.size() == 2)
													// This contractor has only one type so include the "exclusion rule"
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

		Collections.sort(rules);
		Collections.reverse(rules);

//		PicsLogger.log("found " + rules.size() + " rules for contractor " + contractor.getId());
//		PicsLogger.stop();

		return rules;
	}

	public Risks getData() {
		if (data == null) {
			data = new Risks();
			for (AuditTypeRule rule : auditRuleDAO.findAuditTypeRules()) {
				data.add(rule);
			}
		}
		return data;
	}

	public void clear() {
		data = null;
	}

	private class Risks {

		private Map<LowMedHigh, AcceptsBids> data = new LinkedHashMap<LowMedHigh, AcceptsBids>();

		public AcceptsBids getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(AuditTypeRule rule) {
//			PicsLogger.log("Add rule to cache: " + rule);
			AcceptsBids map = data.get(rule.getRisk());
			if (map == null) {
				map = new AcceptsBids();
				data.put(rule.getRisk(), map);
			}
			map.add(rule);
//			PicsLogger.log(" + Risk = " + rule.getRisk());
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
//			PicsLogger.log(" + AcceptsBids = " + rule.getAcceptsBids());
		}
	}

	private class ContractorTypes {

		private Map<ContractorType, Operators> data = new LinkedHashMap<ContractorType, Operators>();

		public Operators getData(ContractorType value) {
			return data.get(value);
		}

		public void add(AuditTypeRule rule) {
			Operators map = data.get(rule.getContractorType());
			if (map == null) {
				map = new Operators();
				data.put(rule.getContractorType(), map);
			}
			map.add(rule);
//			PicsLogger.log(" + ContractorType = " + rule.getContractorType());
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
//			PicsLogger.log(" + OperatorAccount = " + rule.getOperatorAccount());
		}
	}

	public String print() {
		return "";
	}
}
