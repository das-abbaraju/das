package com.picsauditing.PICS;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.log.PicsLogger;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditBuilder {

	private Map<AuditType, AuditTypeDetail> auditTypes = new HashMap<AuditType, AuditTypeDetail>();

	public class AuditTypeDetail {

		public Map<OperatorAccount, OperatorAccount> operators = new HashMap<OperatorAccount, OperatorAccount>();
		public Set<OperatorAccount> governingBodies = new HashSet<OperatorAccount>();
		public Set<AuditCategory> categories = new HashSet<AuditCategory>();
	}

	public Map<AuditType, AuditTypeDetail> getAuditTypes() {
		return auditTypes;
	}

	public AuditTypeDetail getAuditTypes(AuditType auditType) {
		return auditTypes.get(auditType);
	}

	public Map<AuditType, AuditTypeDetail> calculateRequiredAuditTypes(List<AuditTypeRule> rules,
			Collection<OperatorAccount> operators) {
		PicsLogger.start("getRequiredAuditTypes");

		sortRules(rules);

		Set<AuditType> allCandidateAuditTypes = new HashSet<AuditType>();
		for (AuditTypeRule rule : rules) {
			if (rule.isInclude()) {
				// We will never have a rule that says to include all audit
				// types. So assuming that rule.getAuditType is never NULL is
				// fine. This fact also allows us to only evaluate the
				// auditTypes for the rules we have rather than using all
				// auditTypes.
				allCandidateAuditTypes.add(rule.getAuditType());
			}
		}

		for (AuditType auditType : allCandidateAuditTypes) {
			auditTypes.put(auditType, new AuditTypeDetail());
			for (OperatorAccount operator : operators) {
				if (isApplicable(rules, auditType, operator)) {
					auditTypes.get(auditType).operators.put(operator, null);
				}
			}
			if (auditTypes.get(auditType).operators.size() == 0)
				auditTypes.remove(auditType);
		}

		PicsLogger.stop();
		return auditTypes;
	}

	/**
	 * Determine which categories should be on a given audit
	 * 
	 * @param conAudit
	 */
	public void getRequiredCategories(AuditType auditType, List<AuditCategoryRule> rules) {
		sortRules(rules);

		AuditTypeDetail detail = auditTypes.get(auditType);
		if (detail == null) {
			auditTypes.put(auditType, new AuditTypeDetail());
			detail = auditTypes.get(auditType);
		} else {
			detail.categories.clear();
			detail.governingBodies.clear();
		}

		// Figure out which categories are required
		for (AuditCategory category : auditType.getCategories()) {
			includeCategory(category, rules);
		}

		// this is nice because it converts the collection into a distinct set
		detail.governingBodies.addAll(detail.operators.values());
	}

	/**
	 * Find the first rule that applies to this operator. Consider rules for any
	 * operator(*), this operator, or one its parent companies. Ignore rules for
	 * other operators.
	 * 
	 * @param rules
	 * @param auditType
	 * @param operator
	 * @return
	 */
	static private boolean isApplicable(List<AuditTypeRule> rules, AuditType auditType, OperatorAccount operator) {
		for (AuditTypeRule rule : rules) {
			if (rule.getAuditType().equals(auditType)) {
				if (rule.isApplies(operator))
					// Only consider rules for this operator
					return rule.isInclude();
			}
		}
		return false;
	}

	private void includeCategory(AuditCategory category, List<AuditCategoryRule> categoryRules) {
		AuditTypeDetail detail = this.auditTypes.get(category.getAuditType());

		for (OperatorAccount operator : detail.operators.keySet()) {
			AuditCategoryRule rule = getApplicable(categoryRules, category, operator);
			if (rule != null && rule.isInclude()) {
				detail.categories.add(category);

				if (rule.getOperatorAccount() != null)
					detail.operators.put(operator, rule.getOperatorAccount());
			}
		}

		for (AuditCategory subCategory : category.getSubCategories()) {
			includeCategory(subCategory, categoryRules);
		}
	}

	static private AuditCategoryRule getApplicable(List<AuditCategoryRule> rules, AuditCategory auditCategory,
			OperatorAccount operator) {
		for (AuditCategoryRule rule : rules) {
			if (rule.getAuditCategory() == null || rule.getAuditCategory().equals(auditCategory)) {
				if (rule.isApplies(operator))
					return rule;
			}
		}
		return null;
	}

	// TODO evaluate how long this takes and eliminate this eventually
	static private void sortRules(List<? extends AuditRule> rules) {
		for (BaseDecisionTreeRule rule : rules) {
			rule.calculatePriority();
		}

		Collections.sort(rules);
		Collections.reverse(rules);
	}

	static private void rulesToConvert() {
		/*
		 * // TODO Add rule for Annual Update needed by acceptsBids and
		 * everything // else is acceptsBids = No // TODO test Welcome Call
		 * Audits
		 * 
		 * // AuditData oqEmployees = //
		 * auditDataDAO.findAnswerToQuestion(pqfAudit.getId(), //
		 * AuditQuestion.OQ_EMPLOYEES); // AuditData hasCOR = //
		 * auditDataDAO.findAnswerToQuestion(pqfAudit.getId(), 2954);
		// Checking to see if the supplement COR or BPIISNCaseMgmt should be
		// required for this contractor
		ContractorAudit corAudit = null;
		ContractorAudit BpIisnSpecific = null;
		ContractorAudit HSECompetency = null;
		for (ContractorAudit audit : currentAudits) {
			if (auditTypeList.contains(audit.getAuditType())) {
				if (audit.getAuditType().getId() == AuditType.BPIISNSPECIFIC)
					BpIisnSpecific = audit;
				else if (audit.getAuditType().getId() == 99)
					HSECompetency = audit;
				else if (audit.getAuditType().getId() == AuditType.COR && hasCOR != null
						&& "Yes".equals(hasCOR.getAnswer()))
					corAudit = audit;
			}
		}

		// Find the PQF audit for this contractor
		// Only ever create ONE PQF audit
		// TODO we should probably take this part out. I don't think it's needed
		// anymore
		ContractorAudit pqfAudit = null;
		for (ContractorAudit conAudit : currentAudits) {
			if (conAudit.getAuditType().isPqf()) {
				if (conAudit.getAuditStatus().equals(AuditStatus.Expired)) {
					// This should never happen...but just in case
					conAudit.changeStatus(AuditStatus.Pending, user);
					cAuditDAO.save(conAudit);
				}
				pqfAudit = conAudit;
				break;
			}
		}

					switch (auditType.getId()) {
					case AuditType.DESKTOP:
						if (!pqfAudit.getAuditStatus().isActive()) {
							insertNow = false;
							break;
						}
						// If the contractor has answered Yes to the COR
						// question
						// don't create a Desktop Audit
						if (hasCOR != null && "Yes".equals(hasCOR.getAnswer()))
							insertNow = false;
						break;
					case AuditType.OFFICE:
						if (!pqfAudit.getAuditStatus().isActiveSubmitted())
							insertNow = false;
						break;
					case AuditType.DA:
						if (!pqfAudit.getAuditStatus().isActiveSubmitted() || oqEmployees == null
								|| !"Yes".equals(oqEmployees.getAnswer()))
							insertNow = false;
						break;
					case AuditType.COR:
						if (hasCOR == null || !"Yes".equals(hasCOR.getAnswer()))
							insertNow = false;
						break;
					case AuditType.SUPPLEMENTCOR:
						if (corAudit == null)
							insertNow = false;
						else if (!corAudit.getAuditStatus().isActive())
							insertNow = false;
						break;
					case AuditType.BPIISNCASEMGMT:
						if (BpIisnSpecific != null && !BpIisnSpecific.getAuditStatus().isActiveResubmittedExempt())
							insertNow = false;
						break;
					case 100:
						if (!HSECompetency.getAuditStatus().isActiveResubmittedExempt())
							insertNow = false;
						break;
					default:
						break;
					}

		// TODO If the auditType is a Desktop, then make sure the PQF is
		// ActiveSubmitted. Maybe we should add this to the ruleSet

		// TODO Fill in the tag and questions
		// Map<Integer, Integer> dependencies = new HashMap<Integer, Integer>();
		// dependencies.put(AuditCategory.OSHA_AUDIT, 2064);
		// dependencies.put(AuditCategory.MSHA, 2065);
		// dependencies.put(AuditCategory.CANADIAN_STATISTICS, 2066);
		// dependencies.put(AuditCategory.EMR, 2033);
		// dependencies.put(AuditCategory.LOSS_RUN, 2033);
		// dependencies.put(AuditCategory.WCB, 2967);
		// dependencies.put(AuditCategory.CITATIONS, 3546);
		// int auditID = conAudit.getId();
		//
		// AnswerMap answers = null;
		// answers = auditDataDAO.findAnswers(auditID, new
		// Vector<Integer>(dependencies.values()));

		 */
	}
}
