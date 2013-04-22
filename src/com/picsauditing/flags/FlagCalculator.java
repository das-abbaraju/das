package com.picsauditing.flags;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.flags.FlagAnswerParser;
import com.picsauditing.PICS.flags.MultiYearValueCalculator;
import com.picsauditing.PICS.flags.OshaResult;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

/**
 * This class is no longer being called anywhere in the PICS code, so it is
 * being deprecated.
 *
 * TODO: Deprecated 3/5/2012, delete by 6/5/2012 if we feel confident that it is
 * not being used anywhere.
 */
@Deprecated
public class FlagCalculator {

	@Autowired
	private AuditDataDAO auditDataDao;

	private ContractorOperator co;
	private ContractorAccount contractor;
	private Map<Integer, ContractorAuditOperator> annualUpdates;

	/**
	 * Getting Rules
	 *
	 * if (!worksForOperator || con.getAccountLevel().isBidOnly()) { // This is
	 * a check for if the contractor doesn't // work for the operator (Search
	 * for new), or is a bid only if (!criteria.getAuditType().isPicsPqf()) { //
	 * Ignore all audit requirements other than PQF return null; } }
	 */
	public Map<FlagCriteria, FlagColor> calculate(ContractorOperator co, List<FlagCriteriaRule> rules) {
		Map<FlagCriteria, FlagColor> results = new HashMap<FlagCriteria, FlagColor>();

		this.co = co;
		this.contractor = co.getContractorAccount();

		Set<Integer> criteriaQuestionSet = findAuditQuestionIdsForFlagging(rules);

		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(co.getContractorAccount().getId(),
				criteriaQuestionSet);

		for (FlagCriteriaRule rule : rules) {
			FlagCriteria criteria = rule.getCriteria();

			Boolean flagged = null;
			if (criteria.getAuditType() != null) {
				flagged = calculateAuditType(rule);
			} else if (criteria.getQuestion() != null) {
				if (criteria.getQuestion().getAuditType().isAnnualAddendum()) {
					flagged = calculateAnnualUpdateQuestion(rule);
				} else {
					flagged = calculateQuestion(rule, answerMap);
				}
			}

			if (flagged == null && criteria.isFlaggableWhenMissing()) {
				flagged = true;
			}

			if (flagged != null) {
				results.put(criteria, rule.getFlag());
			}
		}

		return results;
	}

	private Set<Integer> findAuditQuestionIdsForFlagging(List<FlagCriteriaRule> rules) {
		Set<Integer> criteriaQuestionSet = new HashSet<Integer>();

		for (FlagCriteriaRule rule : rules) {
			FlagCriteria fc = rule.getCriteria();
			if (fc.getQuestion() != null) {
				AuditType type = fc.getQuestion().getAuditType();
				if (type != null && !type.isAnnualAddendum()) {
					criteriaQuestionSet.add(fc.getQuestion().getId());
					if (fc.includeExcess() != null) {
						criteriaQuestionSet.add(fc.includeExcess());
					}
				}
			}
		}

		return criteriaQuestionSet;
	}

	private Boolean calculateAuditType(FlagCriteriaRule rule) {
		FlagCriteria criteria = rule.getCriteria();
		AuditType auditType = criteria.getAuditType();
		String hurdle = getHurdle(rule);

		if (auditType.isAnnualAddendum()) {
			// Create a map of annual updates
			this.annualUpdates = new HashMap<Integer, ContractorAuditOperator>();

			// Find all the annual updates for the past 4 years
			int currentYear = DateBean.getCurrentYear();
			for (ContractorAudit ca : co.getContractorAccount().getAudits()) {
				if (ca.getAuditType().isAnnualAddendum() && !ca.isExpired()) {
					for (ContractorAuditOperator cao : ca.getOperators()) {
						if (cao.hasCaop(co.getOperatorAccount().getId())) {
							int auditYear = Integer.parseInt(ca.getAuditFor());
							annualUpdates.put(currentYear - auditYear, cao);
						}
					}
				}
			}

			if (annualUpdates.size() == 0) {
				return null;
			}

			int count = 0;
			for (Integer year : annualUpdates.keySet()) {
				ContractorAuditOperator cao = annualUpdates.get(year);
				// TODO support unverified Annual Updates if we want to
				if (cao.getStatus().after(AuditStatus.Submitted)) {
					count++;
				}
			}

			return (count >= 3);
		}

		if ("number".equals(criteria.getDataType()) && auditType.isScoreable()) {
			// Check for Audits with scoring
			for (ContractorAudit ca : contractor.getAudits()) {
				if (ca.getAuditType().equals(auditType) && !ca.isExpired()) {
					ContractorAuditOperator cao = getCaoForOperator(ca, co.getOperatorAccount());
					if (cao == null) {
						// This audit isn't required by this operator, we can
						// ignore it
					} else {
						// TODO use after or = status
						if (criteria.getRequiredStatus() == null
								|| criteria.getRequiredStatus().equals(cao.getStatus())) {
							return compare("number", criteria.getComparison(), hurdle, Float.toString(ca.getScore()));
						}
					}
				}
			}

			return null;
		}

		// Any other audit, PQF, or Policy
		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().equals(criteria.getAuditType()) && !ca.isExpired()) {
				ContractorAuditOperator cao = getCaoForOperator(ca, co.getOperatorAccount());
				if (cao != null) { // This audit is required by this operator
					// TODO Handle Bid Only
					if (flagCAO(criteria, cao)) {
						return true;
					}
				}
			}
		}

		return null;
	}

	private Boolean calculateQuestion(FlagCriteriaRule rule, Map<Integer, AuditData> answerMap) {
		FlagCriteria criteria = rule.getCriteria();

		String dataType = criteria.getDataType();
		String comparison = criteria.getComparison();
		String hurdle = getHurdle(rule);

		final AuditData auditData = answerMap.get(criteria.getQuestion().getId());

		if (auditData == null || Strings.isEmpty(auditData.getAnswer())) {
			return null;
		}

		String answer = auditData.getAnswer();
		if (criteria.getQuestion().getQuestionType().equals("AMBest")) {
			AmBestDAO amBestDAO = SpringUtils.getBean(SpringUtils.AM_BEST_DAO);
			AmBest amBest = amBestDAO.findByNaic(auditData.getComment());
			if (amBest == null) {
				return null;
			}
			if (criteria.getCategory().equals("Insurance AMB Rating")) {
				answer = Integer.toString(amBest.getRatingCode());
			}
			if (criteria.getCategory().equals("Insurance AMB Class")) {
				answer = Integer.toString(amBest.getFinancialCode());
			}
		} else {
			answer = FlagAnswerParser.parseAnswer(criteria, auditData);

			if (criteria.includeExcess() != null) {
				final AuditData excess = answerMap.get(criteria.includeExcess());
				try {
					Float baseLimit = Float.parseFloat(answer);
					Float excessLimit = Float.parseFloat(excess.getAnswer().replace(",", ""));
					baseLimit += excessLimit;
					answer = "" + baseLimit.intValue();
				} catch (Exception flagIfMessedUp) {
					return true;
				}
			}
		}

		return compare(dataType, comparison, hurdle, answer);
	}

	private Boolean calculateAnnualUpdateQuestion(FlagCriteriaRule rule) {
		FlagCriteria criteria = rule.getCriteria();

		String dataType = criteria.getDataType();
		String comparison = criteria.getComparison();
		String hurdle = getHurdle(rule);

		if (criteria.getQuestion().getId() == AuditQuestion.EMR) {
			OshaResult oshaResult = MultiYearValueCalculator.calculateOshaResultsForEMR(criteria, contractor);
			if (oshaResult != null) {
				if (oshaResult.getAnswer() != null) {
					return compare(dataType, comparison, hurdle, oshaResult.getAnswer());
				}
				else {
					return null;
				}
			} else {
				// when the oshaResult is null, it means that an Exception was caught during the calculation
			    // processing so true is returned to keep the behavior the same as it was before refactoring.
				return true;
			}
		}

		if (criteria.getQuestion().getId() == AuditQuestion.CITATIONS) {
			// Citations question
			ContractorAudit annualUpdate = contractor.getCompleteAnnualUpdates().get(criteria.getMultiYearScope());

			if (annualUpdate != null) {
				for (AuditData data : annualUpdate.getData()) {
					if (data.getQuestion().getId() == AuditQuestion.CITATIONS) {
						return compare(dataType, comparison, hurdle, data.getAnswer());
					}
				}
			}
			return null;
		}

		ContractorAudit annualUpdate = contractor.getCompleteAnnualUpdates().get(criteria.getMultiYearScope());

		if (annualUpdate != null) {
			if (annualUpdate.isCategoryApplicable(criteria.getQuestion().getCategory().getId())) {
				for (AuditData data : annualUpdate.getData()) {
					if (data.getQuestion().getId() == criteria.getQuestion().getId()) {
						return compare(dataType, comparison, hurdle, data.getAnswer());
					}
				}
			}
		}

		return null;
	}

	/**
	 *
	 * @param criteria
	 * @param cao
	 * @return
	 */
	private boolean flagCAO(FlagCriteria criteria, ContractorAuditOperator cao) {
		if (criteria.getRequiredStatus() == null) {
			return true;
		}

		String compare = criteria.getRequiredStatusComparison();
		if (compare == null) {
			compare = "<";
		}

		if (compare.equals(">")) {
			return !cao.getStatus().after(criteria.getRequiredStatus());
		}

		if (compare.equals("=")) {
			return !cao.getStatus().equals(criteria.getRequiredStatus());
		}

		if (compare.equals("!=")) {
			return cao.getStatus().equals(criteria.getRequiredStatus());
		}

		// Default is "<"
		return !cao.getStatus().before(criteria.getRequiredStatus());
	}

	private String getHurdle(FlagCriteriaRule rule) {
		FlagCriteria criteria = rule.getCriteria();
		String hurdle = criteria.getDefaultValue();

		if (criteria.isAllowCustomValue() && !Strings.isEmpty(rule.getHurdle())) {
			hurdle = rule.getHurdle();
		}
		return hurdle;
	}

	/**
	 * @return true if something is BAD
	 */
	private boolean compare(String dataType, String comparison, String hurdle, String answer) {
		try {
			if (dataType.equals("boolean")) {
				return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
			}

			if (dataType.equals("number")) {
				float answer2 = Float.parseFloat(answer.replace(",", ""));
				float hurdle2 = Float.parseFloat(hurdle.replace(",", ""));
				if (comparison.equals("=")) {
					return answer2 == hurdle2;
				}
				if (comparison.equals(">")) {
					return answer2 > hurdle2;
				}
				if (comparison.equals("<")) {
					return answer2 < hurdle2;
				}
				if (comparison.equals(">=")) {
					return answer2 >= hurdle2;
				}
				if (comparison.equals("<=")) {
					return answer2 <= hurdle2;
				}
				if (comparison.equals("!=")) {
					return answer2 != hurdle2;
				}
			}

			if (dataType.equals("string")) {
				if (comparison.equals("NOT EMPTY")) {
					return Strings.isEmpty(answer);
				}
				if (comparison.equalsIgnoreCase("contains")) {
					return answer.contains(hurdle);
				}
				if (comparison.equals("=")) {
					return hurdle.equals(answer);
				}
			}

			if (dataType.equals("date")) {
				Date conDate = DateBean.parseDate(answer);
				Date opDate;

				if (hurdle.equals("Today")) {
					opDate = new Date();
				} else {
					opDate = DateBean.parseDate(hurdle);
				}

				if (comparison.equals("<")) {
					return conDate.before(opDate);
				}
				if (comparison.equals(">")) {
					return conDate.after(opDate);
				}
				if (comparison.equals("=")) {
					return conDate.equals(opDate);
				}
			}
			return false;
		} catch (Exception e) {
			Logger logger = LoggerFactory.getLogger(FlagCalculator.class);
			logger.error("Datatype is {} but values were not {} s", dataType, dataType);
			return true;
		}
	}

	/**
	 * Usually just a single matching cao record for the given operator
	 */
	private ContractorAuditOperator getCaoForOperator(ContractorAudit conAudit, OperatorAccount operator) {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible()) {
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
					if (caop.getOperator().equals(operator)) {
						return cao;
					}
				}
			}
		}

		return null;
	}

}