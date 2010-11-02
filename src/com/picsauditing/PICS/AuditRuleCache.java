package com.picsauditing.PICS;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditRuleCache {

	private Risks data = null;

	public void fill(Collection<AuditCategoryRule> rules) {
		data = new Risks();
		for (AuditCategoryRule rule : rules) {
			data.add(rule);
		}
	}

	public Set<AuditCategoryRule> getApplicable(ContractorAccount contractor) {
		for (OperatorAccount foo : contractor.getOperatorAccounts());
		
		long startTime = Calendar.getInstance().getTimeInMillis();
		Set<AuditCategoryRule> rules = new HashSet<AuditCategoryRule>();
		if (data == null)
			return null;

		Params params = new Params();
		params.risks.add(contractor.getRiskLevel());
		params.operators.addAll(contractor.getOperatorAccounts());

		for (LowMedHigh risk : params.risks) {
			ContractorTypes data2 = data.getData(risk);
			if (data2 != null) {
				System.out.println("found matching risk " + risk);
				for (ContractorType conType : params.contractorType) {
					AcceptsBids data3 = data2.getData(conType);
					if (data3 != null) {
						System.out.println(" found matching conType " + conType);
						for (Boolean acceptsBid : params.acceptsBids) {
							AuditTypes data4 = data3.getData(acceptsBid);
							if (data4 != null) {
								System.out.println("  found matching acceptsBid " + acceptsBid);
								for (AuditType auditType : params.auditTypes) {
									Operators data5 = data4.getData(auditType);
									if (data5 != null) {
										System.out.println("   found matching auditType " + auditType);
										for (OperatorAccount operator : params.operators) {
											Set<AuditCategoryRule> data6 = data5.getData(operator);
											if (data6 != null) {
												System.out.println("    found matching operator " + operator);
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

		long finishTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("Found " + rules.size() + " rules in " + (finishTime - startTime) + "ms");
		return rules;
	}

	private class Params {

		public Set<LowMedHigh> risks = new HashSet<LowMedHigh>();
		public Set<ContractorType> contractorType = new HashSet<ContractorType>();
		public Set<Boolean> acceptsBids = new HashSet<Boolean>();
		public Set<AuditType> auditTypes = new HashSet<AuditType>();
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();

		public Params() {
			risks.add(null);
			contractorType.add(null);
			acceptsBids.add(null);
			auditTypes.add(null);
			operators.add(null);
		}
	}

	private class Risks {

		private Map<LowMedHigh, ContractorTypes> data = new HashMap<LowMedHigh, ContractorTypes>();

		public ContractorTypes getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			System.out.println("Add rule to cache: " + rule);
			ContractorTypes map = data.get(rule.getRisk());
			if (map == null) {
				map = new ContractorTypes();
				data.put(rule.getRisk(), map);
			}
			map.add(rule);
			System.out.println(" + Risk = " + rule.getRisk());
		}
	}

	private class ContractorTypes {

		private Map<ContractorType, AcceptsBids> data = new HashMap<ContractorType, AcceptsBids>();

		public AcceptsBids getData(ContractorType value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			AcceptsBids map = data.get(rule.getContractorType());
			if (map == null) {
				map = new AcceptsBids();
				data.put(rule.getContractorType(), map);
			}
			map.add(rule);
			System.out.println(" + ContractorType = " + rule.getContractorType());
		}
	}

	private class AcceptsBids {

		private Map<Boolean, AuditTypes> data = new HashMap<Boolean, AuditTypes>();

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
			System.out.println(" + AcceptsBids = " + rule.getAcceptsBids());
		}
	}

	private class AuditTypes {

		private Map<AuditType, Operators> data = new HashMap<AuditType, Operators>();

		public Operators getData(AuditType value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			Operators map = data.get(rule.getAuditType());
			if (map == null) {
				map = new Operators();
				data.put(rule.getAuditType(), map);
			}
			map.add(rule);
			System.out.println(" + AuditType = " + rule.getAuditType());
		}
	}

	private class Operators {

		private Map<OperatorAccount, Set<AuditCategoryRule>> data = new HashMap<OperatorAccount, Set<AuditCategoryRule>>();

		public Set<AuditCategoryRule> getData(OperatorAccount value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			Set<AuditCategoryRule> map = data.get(rule.getOperatorAccount());
			if (map == null) {
				map = new HashSet<AuditCategoryRule>();
				data.put(rule.getOperatorAccount(), map);
			}
			map.add(rule);
			System.out.println(" + OperatorAccount = " + rule.getOperatorAccount());
		}
	}

	public String print() {
		return "";
	}
}
