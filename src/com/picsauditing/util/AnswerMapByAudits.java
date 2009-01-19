package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AnswerMapByAudits {

	private Map<ContractorAudit, AnswerMap> data = new HashMap<ContractorAudit, AnswerMap>();

	public AnswerMapByAudits() {
	}

	public AnswerMapByAudits(AnswerMapByAudits toCopy) {
		for (ContractorAudit audit : toCopy.data.keySet()) {
			AnswerMap mapCopy = new AnswerMap(toCopy.get(audit));
			put(audit, mapCopy);
		}
	}

	public AnswerMapByAudits(AnswerMapByAudits toCopy, OperatorAccount operator) {

		if( toCopy != null && toCopy.data != null ) {
			for (ContractorAudit audit : toCopy.data.keySet()) {
				AnswerMap mapCopy = new AnswerMap(toCopy.get(audit), operator);
				put(audit, mapCopy);
			}
		}

		Map<Integer, Map<String, List<ContractorAudit>>> byContractorIdAndAuditTypeName = new HashMap<Integer, Map<String, List<ContractorAudit>>>();

		// wrote this as a second set to avoid any
		// concurrentmodificationexceptions
		Set<ContractorAudit> auditSet = new HashSet<ContractorAudit>(data
				.keySet());

		for (ContractorAudit audit : auditSet) {
			for (ContractorOperator contractorOperator : operator
					.getContractorOperators()) {
				if ("Y".equals(contractorOperator.getWorkStatus())) {

					boolean canSee = false;

					// check that they can see it
					for (AuditOperator auditOperator : operator.getAudits()) {
						if (auditOperator.getAuditType().equals(
								audit.getAuditType())) {
							if (auditOperator.isCanSee()) {
								canSee = true;
							}
							break;
						}
					}

					if (canSee) {

						Map<String, List<ContractorAudit>> byAuditTypeName = byContractorIdAndAuditTypeName
								.get(audit.getContractorAccount().getId());

						if (byAuditTypeName == null) {
							byAuditTypeName = new HashMap<String, List<ContractorAudit>>();
							byContractorIdAndAuditTypeName.put(audit
									.getContractorAccount().getId(),
									byAuditTypeName);
						}

						List<ContractorAudit> audits = byAuditTypeName
								.get(audit.getAuditType().getAuditName());

						if (audits == null) {
							audits = new Vector<ContractorAudit>();
							byAuditTypeName.put(audit.getAuditType()
									.getAuditName(), audits);
						}

						audits.add(audit);
					} else {
						remove(audit);
					}
				} else {
					remove(audit);
				}
			}
		}

		for (Integer contractorId : byContractorIdAndAuditTypeName.keySet()) {
			Map<String, List<ContractorAudit>> byAuditTypeName = byContractorIdAndAuditTypeName
					.get(contractorId);

			for (String auditTypeName : byAuditTypeName.keySet()) {
				List<ContractorAudit> audits = byAuditTypeName
						.get(auditTypeName);

				int biggest = 0;
				ContractorAudit bestAudit = null;

				for (ContractorAudit thisAudit : audits) {

					int thisScore = scoreAudit(thisAudit, operator);

					if (thisScore > biggest) {
						if (bestAudit != null) {
							remove(bestAudit);
						}

						bestAudit = thisAudit;
						biggest = thisScore;
					}
				}
			}
		}

	}

	private int scoreAudit(ContractorAudit audit, OperatorAccount operator) {

		AuditOperator auditOperator = null;

		int score = 0;

		for (AuditOperator ao : operator.getAudits()) {
			if (ao.getAuditType() == audit.getAuditType()) {
				auditOperator = ao;
			}
		}

		if (auditOperator.getRequiredAuditStatus() != null
				&& auditOperator.getRequiredAuditStatus() == AuditStatus.Submitted
				&& audit.getAuditStatus() == AuditStatus.Submitted) {
			score = 100;
		} else if (audit.getAuditStatus() == AuditStatus.Active)
			score = 90;
		else if (audit.getAuditStatus() == AuditStatus.Resubmitted)
			score = 80;
		else if (audit.getAuditStatus() == AuditStatus.Exempt)
			score = 70;
		else if (auditOperator.getRequiredAuditStatus() != null
				&& auditOperator.getRequiredAuditStatus() == AuditStatus.Submitted
				&& audit.getAuditStatus() != AuditStatus.Submitted) {
			score = 60;
		} else if (audit.getAuditStatus() == AuditStatus.Pending)
			score = 50;

		if (audit.getRequestingOpAccount() != null
				&& audit.getRequestingOpAccount().equals(operator)) {
			score += 101;
		}

		return score;
	}

	public AnswerMapByAudits copy() {
		AnswerMapByAudits response = new AnswerMapByAudits(this);
		return response;
	}

	public AnswerMapByAudits copy(OperatorAccount operator) {
		AnswerMapByAudits response = new AnswerMapByAudits(this, operator);
		return response;
	}

	public AnswerMap get(ContractorAudit audit) {
		return data.get(audit);
	}

	public void put(ContractorAudit audit, AnswerMap answers) {
		data.put(audit, answers);
	}

	public void remove(ContractorAudit audit) {
		if (audit != null)
			data.remove(audit);
	}

	public void resetFlagColors() {
		for (ContractorAudit audit : data.keySet()) {
			data.get(audit).resetFlagColors();
		}
	}

	public Set<ContractorAudit> getAuditSet() {
		return data.keySet();
	}

	public List<ContractorAudit> getAuditSet(AuditType matchingAuditType) {
		List<ContractorAudit> matchingConAudits = new ArrayList<ContractorAudit>();
		if (data.size() > 0)
			for (ContractorAudit conAudit : data.keySet())
				if (conAudit.getAuditType().equals(matchingAuditType))
					matchingConAudits.add(conAudit);

		return matchingConAudits;
	}

}
