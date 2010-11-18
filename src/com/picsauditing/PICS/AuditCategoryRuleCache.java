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
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.log.PicsLogger;

public class AuditCategoryRuleCache {

	private Risks data;
	private AuditDecisionTableDAO auditRuleDAO;

	public AuditCategoryRuleCache(AuditDecisionTableDAO auditRuleDAO) {
		this.auditRuleDAO = auditRuleDAO;
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAccount contractor, AuditType auditType) {
		Set<AuditType> audit = new HashSet<AuditType>();
		audit.add(auditType);
		return getApplicableCategoryRules(contractor, audit);
	}

	public List<AuditCategoryRule> getApplicableCategoryRules(ContractorAccount contractor, Set<AuditType> auditTypes) {
		PicsLogger.start("AuditCategoryRuleCache", "Searching AuditCategoryRuleCache for contractor "
				+ contractor.getId());
		List<AuditCategoryRule> rules = new ArrayList<AuditCategoryRule>();
		if (getData() == null)
			return null;

		Set<LowMedHigh> risks = new HashSet<LowMedHigh>();
		risks.add(null);
		risks.add(contractor.getRiskLevel());

		Set<Boolean> acceptsBids = new HashSet<Boolean>();
		acceptsBids.add(null);
		acceptsBids.add(contractor.isAcceptsBids());

		Set<AuditType> auditTypes2 = new HashSet<AuditType>();
		auditTypes2.add(null);
		if (auditTypes != null)
			auditTypes2.addAll(auditTypes);

		Set<ContractorType> contractorType = new HashSet<ContractorType>();
		contractorType.add(null);
		if (contractor.isOnsiteServices())
			contractorType.add(ContractorType.Onsite);
		if (contractor.isOffsiteServices())
			contractorType.add(ContractorType.Offsite);
		if (contractor.isMaterialSupplier())
			contractorType.add(ContractorType.Supplier);

		Set<ContractorOperator> operators = new HashSet<ContractorOperator>();
		operators.add(null);
		operators.addAll(contractor.getOperators());

		for (LowMedHigh risk : risks) {
			AcceptsBids data2 = getData().getData(risk);
			if (data2 != null) {
				PicsLogger.log("found matching risk " + risk);
				for (Boolean acceptsBid : acceptsBids) {
					AuditTypes data3 = data2.getData(acceptsBid);
					if (data3 != null) {
						PicsLogger.log(" found matching acceptsBid " + acceptsBid);
						for (AuditType auditType : auditTypes2) {
							ContractorTypes data4 = data3.getData(auditType);
							if (data4 != null) {
								PicsLogger.log("  found matching auditType " + auditType);
								for (ContractorType conType : contractorType) {
									Operators data5 = data4.getData(conType);
									if (data5 != null) {
										PicsLogger.log("   found matching conType " + conType);
										for (ContractorOperator co : operators) {
											OperatorAccount operator = (co == null ? null : co.getOperatorAccount());
											Set<AuditCategoryRule> data6 = data5.getData(operator);
											if (data6 != null) {
												PicsLogger.log("    found matching operator " + operator);
												rules.addAll(data6);
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

		PicsLogger.log("found " + rules.size() + " rules for contractor " + contractor.getId());

		return rules;
	}

	public Risks getData() {
		if (data == null) {
			data = new Risks();
			for (AuditCategoryRule rule : auditRuleDAO.findCategoryRules()) {
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

		public void add(AuditCategoryRule rule) {
			PicsLogger.log("Add rule to cache: " + rule);
			AcceptsBids map = data.get(rule.getRisk());
			if (map == null) {
				map = new AcceptsBids();
				data.put(rule.getRisk(), map);
			}
			map.add(rule);
			PicsLogger.log(" + Risk = " + rule.getRisk());
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
			PicsLogger.log(" + AcceptsBids = " + rule.getAcceptsBids());
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
			PicsLogger.log(" + AuditType = " + rule.getAuditType());
		}
	}

	private class ContractorTypes {

		private Map<ContractorType, Operators> data = new LinkedHashMap<ContractorType, Operators>();

		public Operators getData(ContractorType value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			Operators map = data.get(rule.getContractorType());
			if (map == null) {
				map = new Operators();
				data.put(rule.getContractorType(), map);
			}
			map.add(rule);
			PicsLogger.log(" + ContractorType = " + rule.getContractorType());
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
			PicsLogger.log(" + OperatorAccount = " + rule.getOperatorAccount());
		}
	}

	public String print() {
		return "";
	}
}
