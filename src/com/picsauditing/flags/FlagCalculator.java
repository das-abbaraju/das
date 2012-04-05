package com.picsauditing.flags;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
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
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.YearList;

/**
 * This class is no longer being called anywhere in the PICS code, so 
 * it is being deprecated. 
 * 
 * TODO: Deprecated 3/5/2012, delete by 6/5/2012 if we feel confident
 * that it is not being used anywhere.
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
	 * if (!worksForOperator || con.getAccountLevel().isBidOnly()) { // This is a check for if the contractor doesn't //
	 * work for the operator (Search for new), or is a bid only if (!criteria.getAuditType().isPqf()) { // Ignore all
	 * audit requirements other than PQF return null; } }
	 */
	public Map<FlagCriteria, FlagColor> calculate(ContractorOperator co, List<FlagCriteriaRule> rules) {
		Map<FlagCriteria, FlagColor> results = new HashMap<FlagCriteria, FlagColor>();

		this.co = co;
		this.contractor = co.getContractorAccount();

		Set<Integer> criteriaQuestionSet = new HashSet<Integer>();
		// Get AuditQuestion ids that are used
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

		Map<Integer, AuditData> answerMap = auditDataDao.findAnswersByContractor(co.getContractorAccount().getId(),
				criteriaQuestionSet);

		for (FlagCriteriaRule rule : rules) {
			FlagCriteria criteria = rule.getCriteria();

			Boolean flagged = null;
			if (criteria.getAuditType() != null) {
				flagged = calculateAuditType(rule);
			} else if (criteria.getQuestion() != null) {
				if (criteria.getQuestion().getAuditType().isAnnualAddendum())
					flagged = calculateAnnualUpdateQuestion(rule);
				else
					flagged = calculateQuestion(rule, answerMap);
			} else if (criteria.getOshaType() != null) {
				flagged = calculateOSHA(rule);
			}
			if (flagged == null && criteria.isFlaggableWhenMissing())
				flagged = true;

			if (flagged != null) {
				results.put(criteria, rule.getFlag());
			}
		}

		return results;
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

			if (annualUpdates.size() == 0)
				return null;

			int count = 0;
			for (Integer year : annualUpdates.keySet()) {
				ContractorAuditOperator cao = annualUpdates.get(year);
				// TODO support unverified Annual Updates if we want to
				if (cao.getStatus().after(AuditStatus.Submitted))
					count++;
			}

			return (count >= 3);
		}

		if ("number".equals(criteria.getDataType()) && auditType.isScoreable()) {
			// Check for Audits with scoring
			for (ContractorAudit ca : contractor.getAudits()) {
				if (ca.getAuditType().equals(auditType) && !ca.isExpired()) {
					ContractorAuditOperator cao = getCaoForOperator(ca, co.getOperatorAccount());
					if (cao == null) {
						// This audit isn't required by this operator, we can ignore it
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
				if (cao == null) {
					// This audit isn't required by this operator, we can ignore it
				} else {
					// TODO Handle Bid Only
					if (flagCAO(criteria, cao)) {
						return true;
					}
				}
			}
		}

		return null;
	}
	
	// TODO: Delete me once the OSHA changes are deployed to LIVE
	private Boolean calculateOSHA(FlagCriteriaRule rule) {
		/*FlagCriteria flagCriteria = rule.getCriteria();
		String hurdle = getHurdle(rule);

		OshaOrganizer osha = contractor.getOshaOrganizer();
		Float answer = osha.getRate(flagCriteria.getOshaType(), flagCriteria.getMultiYearScope(),
				flagCriteria.getOshaRateType());

		if (answer == null)
			return null;

		return compare(flagCriteria.getDataType(), flagCriteria.getComparison(), hurdle, answer.toString());*/
		return null;
	}

	private Boolean calculateQuestion(FlagCriteriaRule rule, Map<Integer, AuditData> answerMap) {
		FlagCriteria criteria = rule.getCriteria();

		String dataType = criteria.getDataType();
		String comparison = criteria.getComparison();
		String hurdle = getHurdle(rule);

		final AuditData auditData = answerMap.get(criteria.getQuestion().getId());

		if (auditData == null || Strings.isEmpty(auditData.getAnswer()))
			return null;

		String answer = auditData.getAnswer();
		if (criteria.getQuestion().getQuestionType().equals("AMBest")) {
			AmBestDAO amBestDAO = SpringUtils.getBean("AmBestDAO");
			AmBest amBest = amBestDAO.findByNaic(auditData.getComment());
			if (amBest == null)
				return null;
			if (criteria.getCategory().equals("Insurance AMB Rating"))
				answer = Integer.toString(amBest.getRatingCode());
			if (criteria.getCategory().equals("Insurance AMB Class"))
				answer = Integer.toString(amBest.getFinancialCode());
		} else {
			answer = parseAnswer(criteria, auditData);

			// Check verified Data
			// criteria.getRequiredStatus()
			// auditData.isVerified();

			if (criteria.includeExcess() != null) {
				final AuditData excess = answerMap.get(criteria.includeExcess());
				try {
					Float baseLimit = Float.parseFloat(answer);
					Float excessLimit = Float.parseFloat(excess.getAnswer().replace(",", ""));
					baseLimit += excessLimit;
					// fcc.setAnswer2("Includes " + excessLimit.intValue() + " from Excess");
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
			Map<String, OshaResult> oshaResults = MultiYearValueCalculator.getOshaResultsForEMR(contractor.getSortedAnnualUpdates());			

			if (!oshaResults.isEmpty()) {
				Float answer = null;
				// TODO: refactor this code and remove answer2 and verified if they are not being used
				String answer2 = "";
				boolean verified = true; // Has the data been verified?
				YearList yearList = new YearList();

				for (String year:oshaResults.keySet()) {
					yearList.add(year);
				}

				try {
					switch (criteria.getMultiYearScope()) {
					case ThreeYearAverage:
						OshaResult oshaResult = MultiYearValueCalculator.calculateAverageEMR(oshaResults.values()); 
						answer = (oshaResult.getAnswer() != null) ? Float.valueOf(Strings.formatNumber(oshaResult.getAnswer())) : null;
						verified = oshaResult.isVerified();
						answer2 = "Years: " + oshaResult.getYear();						
						break;
					case ThreeYearsAgo:
					case TwoYearsAgo:
					case LastYearOnly:
						Integer year = yearList.getYearForScope(criteria.getMultiYearScope());
						if (year != null) {
							OshaResult result = oshaResults.get(year.toString());
							if (result != null) {
								answer = Float.valueOf(Strings.formatNumber(result.getAnswer()));
								verified = result.isVerified();
								answer2 = "Year: " + result.getYear();
							}
						}
						break;
					default:
						throw new RuntimeException("Invalid MultiYear scope of "
								+ criteria.getMultiYearScope().toString() + " specified for flag criteria id "
								+ criteria.getId() + ", contractor id " + contractor.getId());
					}
				} catch (Throwable t) {
					return true;
				}

				if (answer != null)
					return compare(dataType, comparison, hurdle, answer.toString());
				else
					return null;
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
	
	private boolean isLast2Years(String auditFor) {
		int lastYear = DateBean.getCurrentYear() - 1;
		if (Integer.toString(lastYear).equals(auditFor) || Integer.toString(lastYear - 1).equals(auditFor))
			return true;
		
		return false;
	}

	private String parseAnswer(FlagCriteria flagCriteria, AuditData auditData) {
		String questionType = auditData.getQuestion().getQuestionType();
		String criteriaType = flagCriteria.getDataType();
		String answer = auditData.getAnswer();

		if ("Check Box".equals(questionType)) {
			if (!"boolean".equals(criteriaType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
			if ("X".equals(answer))
				return "true";
			else
				return "false";
		}
		if ("Manual".equals(questionType)) {
			if (!"string".equals(criteriaType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
			return answer;
		}
		if (auditData.isMultipleChoice()
				&& ("YesNoNA".equals(auditData.getQuestion().getOption().getUniqueCode()) || "Yes/No".equals(auditData
						.getQuestion().getOption().getUniqueCode()))) {
			if (!"string".equals(criteriaType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to boolean but isn't");
			return answer;
		}
		if ("Date".equals(questionType)) {
			if (!"date".equals(criteriaType))
				System.out.println("WARNING!! " + flagCriteria + " should be set to date but isn't");
			try {
				DateBean.parseDate(answer);
				return answer;
			} catch (Exception doNothingRightHere) {
				System.out.println("Failed to parse date [" + answer + "]");
				return "";
			}
		}
		if ("number".equals(criteriaType)) {
			answer = answer.replace(",", "");
			try {
				Float parsedAnswer = Float.parseFloat(answer);
				return parsedAnswer.toString();
			} catch (Exception doNothingRightHere) {
				System.out.println("Failed to parse date [" + answer + "]");
				return "";
			}
		}
		if ("string".equals(criteriaType)) {
			return answer;
		}
		System.out.println("Failed to parse type " + criteriaType + " " + questionType);
		return "";
	}

	/**
	 * 
	 * @param criteria
	 * @param cao
	 * @return
	 */
	private boolean flagCAO(FlagCriteria criteria, ContractorAuditOperator cao) {
		if (criteria.getRequiredStatus() == null)
			return true;

		String compare = criteria.getRequiredStatusComparison();
		if (compare == null)
			compare = "<";
		
		if (compare.equals(">"))
			return !cao.getStatus().after(criteria.getRequiredStatus());
		if (compare.equals("="))
			return !cao.getStatus().equals(criteria.getRequiredStatus());
		if (compare.equals("!="))
			return cao.getStatus().equals(criteria.getRequiredStatus());
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

		// if (criteria.getOshaRateType() != null && criteria.getOshaRateType().equals(OshaRateType.LwcrNaics)) {
		// return answer2 > (Utilities.getIndustryAverage(true, conCriteria.getContractor().getNaics()) * hurdle2) /
		// 100;
		// }
		// if (criteria.getOshaRateType() != null && criteria.getOshaRateType().equals(OshaRateType.TrirNaics)) {
		// return answer2 > (Utilities.getIndustryAverage(false, conCriteria.getContractor().getNaics()) * hurdle2) /
		// 100;
		// }

		try {
			if (dataType.equals("boolean")) {
				return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
			}

			if (dataType.equals("number")) {
				float answer2 = Float.parseFloat(answer.replace(",", ""));
				float hurdle2 = Float.parseFloat(hurdle.replace(",", ""));
				if (comparison.equals("="))
					return answer2 == hurdle2;
				if (comparison.equals(">"))
					return answer2 > hurdle2;
				if (comparison.equals("<"))
					return answer2 < hurdle2;
				if (comparison.equals(">="))
					return answer2 >= hurdle2;
				if (comparison.equals("<="))
					return answer2 <= hurdle2;
				if (comparison.equals("!="))
					return answer2 != hurdle2;
			}

			if (dataType.equals("string")) {
				if (comparison.equals("NOT EMPTY"))
					return Strings.isEmpty(answer);
				if (comparison.equalsIgnoreCase("contains"))
					return answer.contains(hurdle);
				if (comparison.equals("="))
					return hurdle.equals(answer);
			}

			if (dataType.equals("date")) {
				Date conDate = DateBean.parseDate(answer);
				Date opDate;

				if (hurdle.equals("Today"))
					opDate = new Date();
				else
					opDate = DateBean.parseDate(hurdle);

				if (comparison.equals("<"))
					return conDate.before(opDate);
				if (comparison.equals(">"))
					return conDate.after(opDate);
				if (comparison.equals("="))
					return conDate.equals(opDate);
			}
			return false;
		} catch (Exception e) {
			System.out.println("Datatype is " + dataType + " but values were not " + dataType + "s");
			return true;
		}
	}

	private void todoPolicies() {
		// Calculate and save the recommended flag color for policies
		for (ContractorAudit audit : co.getContractorAccount().getAudits()) {
			if (audit.getAuditType().getClassType().isPolicy() && !audit.isExpired()) {
				for (ContractorAuditOperator cao : audit.getOperators()) {
					if (cao.getStatus().after(AuditStatus.Pending)) {
						if (cao.hasCaop(co.getOperatorAccount().getId())) {
							FlagColor flagColor = calculateCaoStatus(audit.getAuditType(), co.getFlagDatas());
							cao.setFlag(flagColor);
						}
					}
				}
			}
		}
	}

	private FlagColor calculateCaoStatus(AuditType auditType, Set<FlagData> flagDatas) {
		FlagColor flag = null;
		for (FlagData flagData : flagDatas) {
			if (flagData.getCriteria().isInsurance()
					&& flagData.getCriteria().getQuestion().getAuditType().equals(auditType)) {
				flag = FlagColor.getWorseColor(flag, flagData.getFlag());
				if (flag.isRed()) {
					return flag;
				}
			}
		}
		if (flag == null)
			flag = FlagColor.Green;

		return flag;
	}

	private FlagDataOverride hasForceDataFlag(List<FlagDataOverride> overrides, OperatorAccount operator) {
		if (overrides.size() > 0) {
			for (FlagDataOverride flagDataOverride : overrides) {
				if (flagDataOverride.getOperator().equals(operator) && flagDataOverride.isInForce())
					return flagDataOverride;
			}
			// Huh, Why??
			if (overrides.get(0).isInForce())
				return overrides.get(0);
		}
		return null;
	}

	/**
	 * 
	 * @param conAudit
	 * @param operator
	 * @return Usually just a single matching cao record for the given operator
	 */
	private ContractorAuditOperator getCaoForOperator(ContractorAudit conAudit, OperatorAccount operator) {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.isVisible())
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
					if (caop.getOperator().equals(operator))
						return cao;
				}
		}

		return null;
	}

}