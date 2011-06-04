package com.picsauditing.auditBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.SpringUtils;

public abstract class AuditBuilderBase {
	/**
	 * Are we currently junit testing this class? If so, then we must ignore the DAO calls
	 */
	public boolean testing = false;
	protected ContractorAccount contractor;

	protected Set<OperatorTag> getRequiredTags(List<? extends AuditRule> rules) {
		Set<Integer> tagsNeeded = new HashSet<Integer>();
		for (AuditRule rule : rules) {
			if (rule.getTag() != null)
				tagsNeeded.add(rule.getTag().getId());
		}
		Set<OperatorTag> tags = new HashSet<OperatorTag>();
		if (tagsNeeded.size() > 0) {
			if (testing) {
				System.out.println("Skipping call to ContractorTagDAO with " + tagsNeeded.size()
						+ " tags needed");
				return tags;
			}
			ContractorTagDAO dao = (ContractorTagDAO) SpringUtils.getBean("ContractorTagDAO");
			List<ContractorTag> contractorTags = dao.getContractorTags(contractor.getId(), tagsNeeded);
			for (ContractorTag contractorTag : contractorTags) {
				tags.add(contractorTag.getTag());
			}
		}
		return tags;
	}
	
	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers, Set<OperatorTag> opTags) {
		if (rule.getQuestion() != null && !rule.isMatchingAnswer(contractorAnswers.get(rule.getQuestion().getId()))) {
			return false;
		}

		if (rule.getTag() != null && !opTags.contains(rule.getTag())) {
			return false;
		}
		
		return true;
	}

	protected void reSortRules(List<? extends BaseDecisionTreeRule> rules) {
		// This is a sanity check to make double sure that the rules are sorted correctly.
		for (BaseDecisionTreeRule rule : rules) {
			rule.calculatePriority();
		}
		Collections.sort(rules);
		Collections.reverse(rules);
	}

}
