package com.picsauditing.auditBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.SpringUtils;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditTypesBuilder extends AuditBuilderBase {

	public class AuditTypeDetail {

		public AuditTypeRule rule;
		/**
		 * Operator Accounts, not corporate, may be the same as the CAO
		 */
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	}

	protected void pruneRules(List<AuditTypeRule> rules) {
		// Prune Rules
		Set<OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, AuditData> answers = getAnswers(rules);
		Iterator<AuditTypeRule> iterator = rules.iterator();
		while (iterator.hasNext()) {
			AuditTypeRule rule = iterator.next();
			if (!isValid(rule, answers, tags))
				iterator.remove();
		}
	}

	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers, Set<OperatorTag> opTags) {
		AuditTypeRule auditTypeRule = (AuditTypeRule) rule;
		if (auditTypeRule.getAuditType() != null && auditTypeRule.getAuditType().getId() == AuditType.WELCOME) {
			if (DateBean.getDateDifference(contractor.getCreationDate()) < -90)
				return false;
		}
		if (auditTypeRule.isManuallyAdded() || (auditTypeRule.getDependentAuditType() != null)) {
			for (ContractorAudit audit : contractor.getAudits()) {
				if (auditTypeRule.isManuallyAdded()) {
					if (auditTypeRule.getAuditType().equals(audit.getAuditType())) {
						return true;
					}
				} else if (!audit.isExpired() && auditTypeRule.getDependentAuditType() != null
						&& audit.getAuditType().equals(auditTypeRule.getDependentAuditType())) {
					if (auditTypeRule.getDependentAuditStatus() != null
							&& (audit.hasCaoStatus(auditTypeRule.getDependentAuditStatus()) || audit
									.hasCaoStatusAfter(auditTypeRule.getDependentAuditStatus())))
						return true;
				}
			}
			return false;
		}
		return super.isValid(rule, contractorAnswers, opTags);
	}

	private Map<Integer, AuditData> getAnswers(List<? extends AuditRule> rules) {
		Set<Integer> contractorAnswersNeeded = new HashSet<Integer>();
		for (AuditRule rule : rules) {
			if (rule.getQuestion() != null) {
				contractorAnswersNeeded.add(rule.getQuestion().getId());
			}
		}

		Map<Integer, AuditData> answers = new HashMap<Integer, AuditData>();
		if (contractorAnswersNeeded.size() > 0) {
			if (testing) {
				System.out.println("Skipping call to AuditDataDAO with " + contractorAnswersNeeded.size()
						+ " answers needed");
				return answers;
			}
			AuditDataDAO dao = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
			answers = dao.findAnswersByContractor(contractor.getId(), contractorAnswersNeeded);
		}
		return answers;
	}
}
