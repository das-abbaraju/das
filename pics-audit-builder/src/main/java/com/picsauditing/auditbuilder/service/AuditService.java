package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.util.DateBean;
import com.picsauditing.auditbuilder.util.Strings;

import java.math.BigDecimal;
import java.util.Date;

public class AuditService {
	public static AuditType getAuditType(AuditQuestion auditQuestion) {
		return getParentAuditType(auditQuestion.getCategory());
	}

	public static AuditType getParentAuditType(AuditCategory auditCategory) {
		if (auditCategory.getAuditType() == null) {
			return getParentAuditType(auditCategory.getParent());
		}

		return auditCategory.getAuditType();
	}

	public static boolean isMatchingAnswer(AuditRule auditRule, AuditData data) {
		if (data == null) {
			// TODO why would questionComparator be null? Do we return false?
			if (auditRule.getQuestionComparator() == null)
				return false;

			return auditRule.getQuestionComparator().equals(QuestionComparator.Empty);
		}

		// todo: Revisit. If an ineffective/expired question was effective/not expired when it was answered, is the answer
		// not still valid now? If it is valid, why would we exclude it here?
		Date auditEffectiveDate = (data.getAudit().getEffectiveDate() != null) ? data.getAudit().getEffectiveDate()
				: new Date();
		if (auditEffectiveDate.before(data.getQuestion().getEffectiveDate()))
			return false;
		if (!auditEffectiveDate.before(data.getQuestion().getExpirationDate()))
			return false;

		if (auditRule.getQuestionComparator() == null)
			return false;
		String answer = data.getAnswer();
		switch (auditRule.getQuestionComparator()) {
		case Empty:
			return Strings.isEmpty(answer);
		case NotEmpty:
			return !Strings.isEmpty(answer);
		case NotEquals:
			return !auditRule.getQuestionAnswer().equals(answer);
		case Verified:
			return isVerified(data);
		case StartsWith:
			return answer.startsWith(auditRule.getQuestionAnswer());
		}

		if (auditRule.getQuestionComparator() == QuestionComparator.LessThan || auditRule.getQuestionComparator() == QuestionComparator.LessThanEqual
				|| auditRule.getQuestionComparator() == QuestionComparator.GreaterThan
				|| auditRule.getQuestionComparator() == QuestionComparator.GreaterThanEqual) {
			if ("Decimal Number".equals(auditRule.getQuestion().getQuestionType()) || "Money".equals(auditRule.getQuestion().getQuestionType())
					|| "Number".equals(auditRule.getQuestion().getQuestionType()) || "Calculation".equals(auditRule.getQuestion().getQuestionType())) {
				try {
					BigDecimal parsedAnswer = new BigDecimal(answer.replace(",", ""));
					BigDecimal parsedQuestionAnswer = new BigDecimal(auditRule.getQuestionAnswer().replace(",", ""));
					if (auditRule.getQuestionComparator() == QuestionComparator.LessThan)
						return parsedAnswer.compareTo(parsedQuestionAnswer) < 0;
					else if (auditRule.getQuestionComparator() == QuestionComparator.LessThanEqual)
						return parsedAnswer.compareTo(parsedQuestionAnswer) <= 0;
					else if (auditRule.getQuestionComparator() == QuestionComparator.GreaterThan)
						return parsedAnswer.compareTo(parsedQuestionAnswer) > 0;
					else if (auditRule.getQuestionComparator() == QuestionComparator.GreaterThanEqual)
						return parsedAnswer.compareTo(parsedQuestionAnswer) >= 0;
				} catch (NumberFormatException nfe) {
					return false;
				}
			} else
				return false;
		}

		return auditRule.getQuestionAnswer().equals(answer);
	}

	public static boolean isVerified(AuditData auditData) {
		return auditData.getDateVerified() != null;
	}

	public static int getAuditYear(ContractorAudit contractorAudit) {
		int year = 0;
		if (contractorAudit.getAuditFor() != null) {
			try {
				year = Integer.parseInt(contractorAudit.getAuditFor());
			} catch (NumberFormatException ignored) {
			}
		}
		return year;
	}

    public static boolean isApplies(AuditRule auditRule, Trade candidate) {
        if (auditRule.getTrade() == null)
            return true;

        if (auditRule.getTrade().equals(candidate))
            return true;

        if (TradeService.childOf(auditRule.getTrade(), candidate))
            return true;

        return TradeService.childOf(candidate, auditRule.getTrade());
    }

    public static boolean isApplies(AuditRule auditRule, ContractorType candidate) {
        if (auditRule.getContractorType() == null)
            return true;

        return auditRule.getContractorType().equals(candidate);
    }

    public static boolean isApplies(AuditRule auditRule, OperatorAccount operator) {
		if (auditRule.getOperatorAccount() == null)
			return true;

		// operatorAccount could be a corporate or an operator
		if (AccountService.isCorporate(auditRule.getOperatorAccount())) {
			for (Facility facility : operator.getCorporateFacilities()) {
				if (auditRule.getOperatorAccount().equals(facility.getCorporate()))
					return true;
			}
			return false;
		} else {
			return auditRule.getOperatorAccount().equals(operator);
		}
	}

	public static boolean isExpired(ContractorAudit contractorAudit) {
		if (contractorAudit.getExpiresDate() == null)
			return false;
		return contractorAudit.getExpiresDate().before(new Date());
	}

	public static boolean hasCaoStatus(ContractorAudit contractorAudit, AuditStatus auditStatus) {
		for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
			if (cao.isVisible() && cao.getStatus().equals(auditStatus))
				return true;
		}
		return false;
	}

	public static boolean hasCaoStatusAfter(ContractorAudit contractorAudit, AuditStatus auditStatus) {
		return hasCaoStatusAfter(contractorAudit, auditStatus, false);
	}

	public static boolean hasCaoStatusAfter(ContractorAudit contractorAudit, AuditStatus auditStatus, boolean ignoreNotApplicable) {
		for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
			if (ignoreNotApplicable && cao.getStatus().equals(AuditStatus.NotApplicable))
				continue;
			if (cao.isVisible() && cao.getStatus().after(auditStatus)) {
				return true;
			}
		}
		return false;
	}

    public static boolean isAnnualAddendum(AuditType auditType) {
        return (auditType.getId() == AuditType.ANNUALADDENDUM);
    }

	public static boolean isWCB(AuditType auditType) {
		return AuditType.CANADIAN_PROVINCES.contains(auditType.getId());
	}

    public static boolean willExpireSoon(ContractorAudit contractorAudit) {
        int daysToExpiration = 0;
        if (contractorAudit.getExpiresDate() == null)
            daysToExpiration = 1000;
        else
            daysToExpiration = DateBean.getDateDifference(contractorAudit.getExpiresDate());

        if (contractorAudit.getAuditType().getClassType() == AuditTypeClass.Policy) {
            return daysToExpiration <= 15;
        } else if (contractorAudit.getAuditType().getId() == AuditType.COR) {
            return daysToExpiration <= 180;
        } else if (contractorAudit.getAuditType().getId() == AuditType.SSIP) {
            return daysToExpiration <= 30;
        } else {
            return daysToExpiration <= 90;
        }
    }

	public static boolean isApplies(AuditCategoryRule auditCategoryRule, AuditCategory category) {
		if (auditCategoryRule.getAuditCategory() == null) {
			// We have a wildcard category, so let's figure out if it
			// matches on categories or subcategories or both
			if (auditCategoryRule.getRootCategory() == null) {
				// Any category or subcategory matches
				return true;
			} else {
				if (auditCategoryRule.getRootCategory()) {
					if (category.getParent() == null)
						// Only categories match
						return true;
				} else {
					if (category.getParent() != null)
						// Only subcategories match
						return true;
				}
			}
		} else if (auditCategoryRule.getAuditCategory().equals(category)) {
			// We have a direct category match
			return true;
		}
		return false;
	}

}
