package com.picsauditing.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.comparators.ContractorAuditComparator;
import com.picsauditing.util.log.PicsLogger;

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

	/**
	 * Given a set of answers to an audit, figure out if the operator can see
	 * the audit and the answers in it
	 */
	public AnswerMapByAudits(AnswerMapByAudits toCopy, OperatorAccount operator) {

		PicsLogger.start("AnswerMapByAudits", "Pruning the contractor data for operator " + operator.getName());

		if (toCopy != null && toCopy.data != null) {
			for (ContractorAudit audit : toCopy.data.keySet()) {
				AnswerMap mapCopy = new AnswerMap(toCopy.get(audit), operator);
				put(audit, mapCopy);
			}
		}

		Map<Integer, Map<String, List<ContractorAudit>>> byContractorIdAndAuditTypeName = new HashMap<Integer, Map<String, List<ContractorAudit>>>();

		// wrote this as a second set to avoid any
		// concurrentmodificationexceptions
		Set<ContractorAudit> auditSet = new HashSet<ContractorAudit>();
		auditSet.addAll(data.keySet());

		// check that they can see it

		for (AuditOperator auditOperator : operator.getVisibleAudits())
			PicsLogger.log(" operator can see " + auditOperator.getAuditType().getAuditName());

		for (ContractorAudit audit : auditSet) {
			PicsLogger.log("- conAudit " + audit.getId() + " " + audit.getAuditType().getAuditName());
			boolean canSeeAudit = false;

			// check that they can see it
			for (AuditOperator auditOperator : operator.getVisibleAudits()) {
				if (auditOperator.getAuditType().equals(audit.getAuditType())) {
					canSeeAudit = true;
					break;
				}
			}

			if (canSeeAudit) {
				PicsLogger.log("- is visible");

				Map<String, List<ContractorAudit>> byAuditTypeName = byContractorIdAndAuditTypeName.get(audit
						.getContractorAccount().getId());

				if (byAuditTypeName == null) {
					byAuditTypeName = new HashMap<String, List<ContractorAudit>>();
					byContractorIdAndAuditTypeName.put(audit.getContractorAccount().getId(), byAuditTypeName);
				}

				List<ContractorAudit> audits = byAuditTypeName.get(audit.getAuditType().getAuditName());

				if (audits == null) {
					audits = new Vector<ContractorAudit>();
					byAuditTypeName.put(audit.getAuditType().getAuditName(), audits);
				}

				audits.add(audit);
			} else {
				PicsLogger.log("- is NOT visible ... removing from map");
				remove(audit);
			}
		}

		for (Integer contractorId : byContractorIdAndAuditTypeName.keySet()) {
			Map<String, List<ContractorAudit>> byAuditTypeName = byContractorIdAndAuditTypeName.get(contractorId);

			for (String auditTypeName : byAuditTypeName.keySet()) {
				if (!auditTypeName.equals("Annual Update")) {
					List<ContractorAudit> audits = byAuditTypeName.get(auditTypeName);

					int biggest = 0;
					ContractorAudit bestAudit = null;

					for (ContractorAudit thisAudit : audits) {

						int thisScore = scoreAudit(thisAudit, operator);

						if (thisScore > biggest) {
							PicsLogger.log("-- " + thisAudit.getAuditType().getAuditName() + "-" + thisAudit.getId()
									+ " scored " + thisScore);
							if (bestAudit != null) {
								PicsLogger.log("--- but was less than best, so IGNORING");
								remove(bestAudit);
							}

							bestAudit = thisAudit;
							biggest = thisScore;
						}
					}
				}
			}
		}
		PicsLogger.stop();
	}

	private int scoreAudit(ContractorAudit audit, OperatorAccount operator) {
		if (audit.getAuditStatus().isExpired())
			return 0;

		AuditOperator auditOperator = null;

		int score = 0;

		for (AuditOperator ao : operator.getVisibleAudits()) {
			if (ao.getAuditType() == audit.getAuditType()) {
				auditOperator = ao;
			}
		}

		boolean requiresActiveStatus = true;
		if (auditOperator != null) {
			for(FlagCriteriaOperator flagCriteriaOperator : operator.getFlagAuditCriteriaInherited()) {
				if(flagCriteriaOperator.getCriteria().getAuditType().equals(auditOperator.getAuditType())) {
					if(!flagCriteriaOperator.getCriteria().isValidationRequired())
						requiresActiveStatus = false;
				}
			}
		} else {
			PicsLogger.log("Warning: this Operator doesn't require " + audit.getAuditType().getAuditName());
		}

		if (audit.getAuditType().getClassType().isPolicy()) {
			// Add 1000 to make sure the number is always positive since
			// creation dates
			// are always in the past and this DateDifference will be negative
			return DateBean.getDateDifference(audit.getCreationDate()) + 1000;
		} else {
			if (!requiresActiveStatus
					&& audit.getAuditStatus().isSubmitted()) {
				score = 100;
			} else if (audit.getAuditStatus() == AuditStatus.Active)
				score = 90;
			else if (audit.getAuditStatus() == AuditStatus.Resubmitted)
				score = 80;
			else if (audit.getAuditStatus() == AuditStatus.Exempt)
				score = 70;
			else if (!requiresActiveStatus
					&& audit.getAuditStatus() != AuditStatus.Submitted) {
				score = 60;
			} else if (audit.getAuditStatus() == AuditStatus.Pending)
				score = 50;

		}
		if (audit.getRequestingOpAccount() != null && audit.getRequestingOpAccount().equals(operator)) {
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

	public Map<AuditQuestion, AuditData> getAuditQuestionAnswerMap() {
		Map<AuditQuestion, AuditData> map = new TreeMap<AuditQuestion, AuditData>();

		return map;
	}

	public Set<ContractorAudit> getAuditSet() {
		return data.keySet();
	}

	public List<ContractorAudit> getAuditSet(AuditType matchingAuditType) {
		List<ContractorAudit> matchingConAudits = new ArrayList<ContractorAudit>();
		if (data.size() > 0)
			for (ContractorAudit conAudit : data.keySet())
				if (conAudit.getAuditType().equals(matchingAuditType) && !conAudit.getAuditStatus().isExpired())
					matchingConAudits.add(conAudit);

		Collections.sort(matchingConAudits, new ContractorAuditComparator("auditFor -1"));
		return matchingConAudits;
	}

}
