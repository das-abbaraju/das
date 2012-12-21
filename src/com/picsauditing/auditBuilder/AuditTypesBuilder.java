package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Calendar;
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
		Map<Integer, OperatorTag> tags = getRequiredTags(rules);
		Map<Integer, AuditData> answers = getAnswers(rules);
		Iterator<AuditTypeRule> iterator = rules.iterator();
		while (iterator.hasNext()) {
			AuditTypeRule rule = iterator.next();
			if (!isValid(rule, answers, tags))
				iterator.remove();
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
		List<OperatorAccount> operatorAccounts = new ArrayList<OperatorAccount>();
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			if (operator.isOperator() && !operator.getStatus().isDeactivated() && !operator.getStatus().isDeleted()) {
				operatorAccounts.add(operator);
			}
		}

		for (AuditType auditType : allCandidateAuditTypes) {
			List<AuditTypeRule> rulesForThisAuditType = new ArrayList<AuditTypeRule>();
			for (AuditTypeRule rule : rules) {
				if (auditType.equals(rule.getAuditType())) {
					rulesForThisAuditType.add(rule);
				}
			}
			
			// Welcome Audits do not csre about trades or contractor types
			if (auditType.getId() == AuditType.WELCOME) {
				Trade blank = new Trade();
				blank.setId(-1);
				
				AuditTypeDetail detail = new AuditTypeDetail();
				for (OperatorAccount operator : operatorAccounts) {
					AuditTypeRule rule = getApplicable(rulesForThisAuditType, auditType, blank, null, operator);
					if (rule != null && rule.isInclude()) {
						// We need to add this category to the audit
						detail.operators.add(operator);
						if (rule.isMoreSpecific(detail.rule))
							detail.rule = rule;
						types.add(detail);
					}
				}
			
				continue;
			}
			
			AuditTypeDetail detail = new AuditTypeDetail();
			for (Trade trade : trades) {
				for (ContractorType type : contractorTypes) {
					for (OperatorAccount operator : operatorAccounts) {
						AuditTypeRule rule = getApplicable(rulesForThisAuditType, auditType, trade, type, operator);
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

	protected boolean isValid(AuditRule rule, Map<Integer, AuditData> contractorAnswers,
			Map<Integer, OperatorTag> opTags) {
		AuditTypeRule auditTypeRule = (AuditTypeRule) rule;
		
		// Based on PICS-2734, we're going to check for ManuallyAdded in AuditBuilder line 62
		//if (auditTypeRule.isManuallyAdded())
		//return false;
		
		if (auditTypeRule.getDependentAuditType() != null && auditTypeRule.getDependentAuditStatus() != null) {
			boolean found = false;
			for (ContractorAudit audit : contractor.getAudits()) {
				if (!audit.isExpired()
						&& audit.getAuditType().equals(auditTypeRule.getDependentAuditType())
						&& (audit.hasCaoStatus(auditTypeRule.getDependentAuditStatus()) || 
								audit.hasCaoStatusAfter(auditTypeRule.getDependentAuditStatus()))) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;				
			}
		}
		return super.isValid(rule, contractorAnswers, opTags);
	}

	private Map<Integer, AuditData> getAnswers(List<? extends AuditRule> rules) {
		Map<Integer, AuditData> contractorAnswersNeeded = new HashMap<Integer, AuditData>();
		for (AuditRule rule : rules) {
			if (rule.getQuestion() != null) {
				contractorAnswersNeeded.put(rule.getQuestion().getId(), null);
			}
		}

		if (contractorAnswersNeeded.size() > 0) {
			// Don't load the DAO if not needed. This is especially helpful for unit testing
			AuditDataDAO dao = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
			Map<Integer, AuditData> findAnswersByContractor = dao.findAnswersByContractor(contractor.getId(),
					contractorAnswersNeeded.keySet());
			for (Integer questionID : findAnswersByContractor.keySet()) {
				contractorAnswersNeeded.put(questionID, findAnswersByContractor.get(questionID));
			}
		}
		return contractorAnswersNeeded;
	}

	public List<AuditTypeRule> getRules() {
		return rules;
	}
}
