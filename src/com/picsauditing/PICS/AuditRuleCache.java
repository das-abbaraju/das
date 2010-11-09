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
import com.picsauditing.jpa.entities.ContractorOperator;
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

	public Set<AuditCategoryRule> getApplicable(ContractorAccount contractor, Set<AuditType> auditTypes) {
		// get the operators so I can get an accurate timing
		contractor.getOperatorAccounts().size();
		long startTime = Calendar.getInstance().getTimeInMillis();

		Set<AuditCategoryRule> rules = new HashSet<AuditCategoryRule>();
		if (data == null)
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

		Set<ContractorOperator> operators = new HashSet<ContractorOperator>();
		operators.add(null);
		operators.addAll(contractor.getOperators());

		for (LowMedHigh risk : risks) {
			AcceptsBids data2 = data.getData(risk);
			if (data2 != null) {
				System.out.println("found matching risk " + risk);
				for (Boolean acceptsBid : acceptsBids) {
					AuditTypes data3 = data2.getData(acceptsBid);
					if (data3 != null) {
						System.out.println(" found matching acceptsBid " + acceptsBid);
						for (AuditType auditType : auditTypes2) {
							ContractorTypes data4 = data3.getData(auditType);
							if (data4 != null) {
								System.out.println("  found matching auditType " + auditType);
								for (ContractorOperator co : operators) {
									Set<ContractorType> contractorType = new HashSet<ContractorType>();
									contractorType.add(null);
									OperatorAccount operator = null;
									if (co != null) {
										contractorType.add(co.getContractorType());
										operator = co.getOperatorAccount();
									}
									for (ContractorType conType : contractorType) {
										Operators data5 = data4.getData(conType);
										if (data5 != null) {
											System.out.println("   found matching conType " + conType);
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

	private class Risks {

		private Map<LowMedHigh, AcceptsBids> data = new HashMap<LowMedHigh, AcceptsBids>();

		public AcceptsBids getData(LowMedHigh value) {
			return data.get(value);
		}

		public void add(AuditCategoryRule rule) {
			System.out.println("Add rule to cache: " + rule);
			AcceptsBids map = data.get(rule.getRisk());
			if (map == null) {
				map = new AcceptsBids();
				data.put(rule.getRisk(), map);
			}
			map.add(rule);
			System.out.println(" + Risk = " + rule.getRisk());
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

		private Map<AuditType, ContractorTypes> data = new HashMap<AuditType, ContractorTypes>();

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
			System.out.println(" + AuditType = " + rule.getAuditType());
		}
	}

	private class ContractorTypes {

		private Map<ContractorType, Operators> data = new HashMap<ContractorType, Operators>();

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
			System.out.println(" + ContractorType = " + rule.getContractorType());
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
