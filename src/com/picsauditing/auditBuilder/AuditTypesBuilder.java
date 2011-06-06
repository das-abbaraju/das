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
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.SpringUtils;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditTypesBuilder extends AuditBuilderBase {
	private AuditTypeRuleCache ruleCache;
	private List<AuditTypeRule> rules;

	public class AuditTypeDetail {
		/**
		 * The AuditTypeRule that is responsible for including this auditType for this contractor
		 */
		public AuditTypeRule rule;
		/**
		 * Operator Accounts that require this audit (CAOPs)
		 */
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	}

	public AuditTypesBuilder(AuditTypeRuleCache ruleCache, ContractorAccount contractor) {
		super(contractor);
		this.ruleCache = ruleCache;
	}

	public Set<AuditTypeDetail> calculate() {
		Set<AuditTypeDetail> types = new HashSet<AuditTypeDetail>();

		rules = ruleCache.getRules(contractor);

		// Prune Rules
		Set<OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, AuditData> answers = getAnswers(rules);
		if (tags.size() > 0 || answers.size() > 0) {
			Iterator<AuditTypeRule> iterator = rules.iterator();
			while (iterator.hasNext()) {
				AuditTypeRule rule = iterator.next();
				if (!isValid(rule, answers, tags))
					iterator.remove();
			}
		}

		/**
		 * We will never have a rule that says to include all audit types. So assuming that rule.getAuditType is never
		 * NULL is fine. This fact also allows us to only evaluate the auditTypes for the rules we have rather than
		 * using all auditTypes.
		 */
		Set<AuditType> allCandidateAuditTypes = new HashSet<AuditType>();
		for (AuditTypeRule rule : rules) {
			if (rule.isInclude()) {
				allCandidateAuditTypes.add(rule.getAuditType());
			}
		}

		// Get the operator list once
		List<OperatorAccount> operatorAccounts = contractor.getOperatorAccounts();
		for (AuditType auditType : allCandidateAuditTypes) {
			AuditTypeDetail detail = new AuditTypeDetail();
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : operatorAccounts) {
						AuditTypeRule rule = getApplicable(rules, auditType, trade, type, operator);
						if (rule != null && rule.isInclude()) {
							// We need to add this category to the audit
							detail.operators.add(operator);
							if (rule.isMoreSpecific(detail.rule))
								detail.rule = rule;
							types.add(detail);
						}
					}
				}
			}
		}

		return types;
	}

	private AuditTypeRule getApplicable(List<AuditTypeRule> rules, AuditType auditType, Trade trade,
			ContractorType type, OperatorAccount operator) {
		for (AuditTypeRule rule : rules) {
			if (auditType.equals(rule.getAuditType()))
				if (rule.isApplies(trade))
					if (rule.isApplies(type))
						if (rule.isApplies(operator))
							return rule;
		}
		return null;
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
			// Don't load the DAO if not needed. This is especially helpful for unit testing
			AuditDataDAO dao = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
			answers = dao.findAnswersByContractor(contractor.getId(), contractorAnswersNeeded);
		}
		return answers;
	}
	
	public List<AuditTypeRule> getRules() {
		return rules;
	}
}
