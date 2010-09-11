package com.picsauditing.PICS;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.BaseDecisionTreeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.log.PicsLogger;

/**
 * Determine which audits and categories are needed for a contractor.
 */
public class AuditBuilder {

	public class AuditTypeDetail {

		public AuditTypeRule rule;
		/**
		 * Operator Accounts, not corporate, may be the same as the CAO
		 */
		public Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
	}

	public class AuditCategoriesDetail {

		public List<AuditCategoryRule> rules;
		public Map<OperatorAccount, AuditCategoryRule> operators = new HashMap<OperatorAccount, AuditCategoryRule>();
		public Set<OperatorAccount> governingBodies = new HashSet<OperatorAccount>();
		public Set<AuditCategory> categories = new HashSet<AuditCategory>();
	}

	/**
	 * 
	 * @param rules
	 * @param operators
	 *            a list of operators (not corporate) accounts associated with
	 *            this contractor
	 * @return
	 */
	public Map<AuditType, AuditTypeDetail> calculateRequiredAuditTypes(List<AuditTypeRule> rules,
			Collection<OperatorAccount> operators) {
		Map<AuditType, AuditTypeDetail> auditTypes = new HashMap<AuditType, AuditTypeDetail>();

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
				AuditTypeRule rule = getApplicable(rules, auditType, operator);
				if (rule.isInclude()) {
					auditTypes.get(auditType).operators.add(operator);
					auditTypes.get(auditType).rule = rule;
				}
			}
			if (auditTypes.get(auditType).operators.size() == 0)
				auditTypes.remove(auditType);
		}

		PicsLogger.stop();
		return auditTypes;
	}

	static public Map<AuditType, AuditTypeDetail> calculateRequiredAuditTypes(ContractorAccount contractor) {
		// This isn't super efficient, but it works
		AuditBuilder builder = new AuditBuilder();
		AuditDecisionTableDAO dao = (AuditDecisionTableDAO) SpringUtils.getBean("AuditDecisionTableDAO");
		List<AuditTypeRule> rules = dao.getApplicableAuditRules(contractor);
		return builder.calculateRequiredAuditTypes(rules, contractor.getOperatorAccounts());
	}

	/**
	 * Determine which categories should be on a given audit
	 * 
	 * @param rules
	 *            Make sure that these rules are filtered for the requested
	 *            contractorAudit
	 */
	public AuditCategoriesDetail getDetail(AuditType auditType, List<AuditCategoryRule> rules,
			Collection<OperatorAccount> operators) {
		AuditCategoriesDetail detail = new AuditCategoriesDetail();
		sortRules(rules);
		detail.rules = rules;
		for (OperatorAccount operator : operators) {
			detail.operators.put(operator, null);
		}

		// Figure out which categories are required
		for (AuditCategory category : auditType.getCategories()) {
			includeCategory(detail, category, rules);
		}

		for (AuditCategoryRule rule : detail.operators.values()) {
			detail.governingBodies.add(rule == null ? null : rule.getOperatorAccount());
		}
		return detail;
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
	static private AuditTypeRule getApplicable(List<AuditTypeRule> rules, AuditType auditType, OperatorAccount operator) {
		for (AuditTypeRule rule : rules) {
			if (rule.getAuditType() == null || rule.getAuditType().equals(auditType)) {
				if (rule.isApplies(operator))
					// Only consider rules for this operator
					return rule;
			}
		}
		return null;
	}

	private void includeCategory(AuditCategoriesDetail detail, AuditCategory category,
			List<AuditCategoryRule> categoryRules) {
		for (OperatorAccount operator : detail.operators.keySet()) {
			AuditCategoryRule rule = getApplicable(categoryRules, category, operator);
			if (rule != null && rule.isInclude()) {
				detail.categories.add(category);
				if (rule.isMoreSpecific(detail.operators.get(operator)))
					detail.operators.put(operator, rule);
			}
		}

		for (AuditCategory subCategory : category.getSubCategories()) {
			includeCategory(detail, subCategory, categoryRules);
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

	// TODO evaluate how long this takes or eliminate this once the converted
	// rule priorities are properly calculated
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
		 * auditDataDAO.findAnswerToQuestion(pqfAudit.getId(), 2954); //
		 * Checking to see if the supplement COR or BPIISNCaseMgmt should be //
		 * required for this contractor ContractorAudit corAudit = null;
		 * ContractorAudit BpIisnSpecific = null; ContractorAudit HSECompetency
		 * = null; for (ContractorAudit audit : currentAudits) { if
		 * (auditTypeList.contains(audit.getAuditType())) { if
		 * (audit.getAuditType().getId() == AuditType.BPIISNSPECIFIC)
		 * BpIisnSpecific = audit; else if (audit.getAuditType().getId() == 99)
		 * HSECompetency = audit; else if (audit.getAuditType().getId() ==
		 * AuditType.COR && hasCOR != null && "Yes".equals(hasCOR.getAnswer()))
		 * corAudit = audit; } }
		 * 
		 * // Find the PQF audit for this contractor // Only ever create ONE PQF
		 * audit // TODO we should probably take this part out. I don't think
		 * it's needed // anymore ContractorAudit pqfAudit = null; for
		 * (ContractorAudit conAudit : currentAudits) { if
		 * (conAudit.getAuditType().isPqf()) { if
		 * (conAudit.getAuditStatus().equals(AuditStatus.Expired)) { // This
		 * should never happen...but just in case
		 * conAudit.changeStatus(AuditStatus.Pending, user);
		 * cAuditDAO.save(conAudit); } pqfAudit = conAudit; break; } }
		 * 
		 * switch (auditType.getId()) { case AuditType.DESKTOP: if
		 * (!pqfAudit.getAuditStatus().isActive()) { insertNow = false; break; }
		 * // If the contractor has answered Yes to the COR // question // don't
		 * create a Desktop Audit if (hasCOR != null &&
		 * "Yes".equals(hasCOR.getAnswer())) insertNow = false; break; case
		 * AuditType.OFFICE: if (!pqfAudit.getAuditStatus().isActiveSubmitted())
		 * insertNow = false; break; case AuditType.DA: if
		 * (!pqfAudit.getAuditStatus().isActiveSubmitted() || oqEmployees ==
		 * null || !"Yes".equals(oqEmployees.getAnswer())) insertNow = false;
		 * break; case AuditType.COR: if (hasCOR == null ||
		 * !"Yes".equals(hasCOR.getAnswer())) insertNow = false; break; case
		 * AuditType.SUPPLEMENTCOR: if (corAudit == null) insertNow = false;
		 * else if (!corAudit.getAuditStatus().isActive()) insertNow = false;
		 * break; case AuditType.BPIISNCASEMGMT: if (BpIisnSpecific != null &&
		 * !BpIisnSpecific.getAuditStatus().isActiveResubmittedExempt())
		 * insertNow = false; break; case 100: if
		 * (!HSECompetency.getAuditStatus().isActiveResubmittedExempt())
		 * insertNow = false; break; default: break; }
		 * 
		 * // TODO If the auditType is a Desktop, then make sure the PQF is //
		 * ActiveSubmitted. Maybe we should add this to the ruleSet
		 */
	}
}
