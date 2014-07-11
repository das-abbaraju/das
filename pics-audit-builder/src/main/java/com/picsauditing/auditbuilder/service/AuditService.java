package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.entities.QuestionFunction.FunctionInput;
import com.picsauditing.auditbuilder.util.AnswerMap;
import com.picsauditing.auditbuilder.util.CorruptionPerceptionIndexMap;
import com.picsauditing.auditbuilder.util.DateBean;
import com.picsauditing.auditbuilder.util.Strings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
			if (auditRule.getQuestionComparator() == null)
				return false;

			return auditRule.getQuestionComparator().equals(QuestionComparator.Empty);
		}

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

    public static boolean isUnverified(AuditData auditData) {
        return auditData.getDateVerified() == null;
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
			if (auditCategoryRule.getRootCategory() == null) {
				return true;
			} else {
				if (auditCategoryRule.getRootCategory()) {
					if (category.getParent() == null)
						return true;
				} else {
					if (category.getParent() != null)
						return true;
				}
			}
		} else if (auditCategoryRule.getAuditCategory().equals(category)) {
			return true;
		}
		return false;
	}

	public static boolean hasCaoStatusBefore(ContractorAudit contractorAudit, AuditStatus auditStatus) {
		for (ContractorAuditOperator cao : contractorAudit.getOperators()) {
			if (cao.isVisible() && cao.getStatus().before(auditStatus))
				return true;
		}
		return false;
	}

	public static ContractorAuditOperatorWorkflow changeStatus(ContractorAuditOperator contractorAuditOperator, AuditStatus auditStatus) {
		if (auditStatus.equals(contractorAuditOperator.getStatus()))
			return null;

		ContractorAuditOperatorWorkflow caow = new ContractorAuditOperatorWorkflow();
		caow.setCao(contractorAuditOperator);
		caow.setPreviousStatus(contractorAuditOperator.getStatus());
		caow.setStatus(auditStatus);
		caow.setAuditColumns();

        contractorAuditOperator.setAuditColumns();
        contractorAuditOperator.setStatusChangedDate(new Date());
		contractorAuditOperator.setStatus(auditStatus);

		if (contractorAuditOperator.getStatus() != AuditStatus.Incomplete) {
            contractorAuditOperator.setAuditSubStatus(null);
		}

		if (contractorAuditOperator.getAudit().getAuditType().getId() == AuditType.PQF || contractorAuditOperator.getAudit().getAuditType().getId() == AuditType.ANNUALADDENDUM)
			return caow;

		if (auditStatus == AuditStatus.Pending)
			return caow;

		if (contractorAuditOperator.getAudit().getEffectiveDate() == null)
            contractorAuditOperator.getAudit().setEffectiveDate(new Date());

		return caow;
	}

	public static boolean isVisibleInAudit(AuditQuestion auditQuestion, ContractorAudit audit) {
		for (AuditCatData category : audit.getCategories()) {
			if (category.getCategory().getId() == auditQuestion.getCategory().getId()) {
				return category.isApplies();
			}
		}

		return false;
	}

    public static WorkflowStep getFirstStep(Workflow workflow) {
        for (WorkflowStep step : workflow.getSteps()) {
            if (step.getOldStatus() == null)
                return step;
        }
        return null;
    }

	public static boolean pqfIsOkayToChangeCaoStatus(ContractorAudit contractorAudit, ContractorAuditOperator cao) {
		if (contractorAudit.getAuditType().getId() == AuditType.PQF && cao.getPercentVerified() == 100) {
			for (AuditData data : contractorAudit.getData()) {
				if (data.getQuestion().getId() == AuditQuestion.MANUAL_PQF && isUnverified(data)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

    public static AuditCategory getTopParent(AuditCategory auditCategory) {
        if (auditCategory.getParent() != null) {
            return getTopParent(auditCategory.getParent());
        }

        return auditCategory;
    }

	public static Date getValidDate(ContractorAudit contractorAudit) {
		if (contractorAudit.getAuditType().getId() == AuditType.ANNUALADDENDUM)
			return contractorAudit.getEffectiveDate();
		if (hasCaoStatusAfter(contractorAudit, AuditStatus.Incomplete)) {
			if (contractorAudit.getEffectiveDate() == null)
				return new Date();
			else
				return contractorAudit.getEffectiveDate();
		} else
			return new Date();
	}

	public static boolean isValidQuestion(AuditQuestion auditQuestion, Date validDate) {
		if (validDate.after(auditQuestion.getEffectiveDate()) && validDate.before(auditQuestion.getExpirationDate())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isVisible(AuditQuestion auditQuestion, AuditData data) {
		if (auditQuestion.getVisibleQuestion() != null && auditQuestion.getVisibleAnswer() != null) {
			String answer = null;
			if (data != null) {
				answer = data.getAnswer();
			}
			return testVisibility(answer, auditQuestion.getVisibleAnswer());
		}
		return true;
	}

    private static boolean testVisibility(String answer, String comparisonAnswer) {
        if (comparisonAnswer.equals("NULL") && Strings.isEmpty(answer)) {
            return true;
        }
        if (comparisonAnswer.equals("NOTNULL") && !Strings.isEmpty(answer)) {
            return true;
        }
        if (comparisonAnswer.equals(answer)) {
            return true;
        }
        return false;
    }

	public static boolean isAnswered(AuditData auditData) {
		if (auditData.getAnswer() != null && auditData.getAnswer().length() > 0 && !auditData.getAnswer().equals(DateBean.NULL_DATE_DB)) {
			return true;
		}
		return false;
	}

	public static boolean isScoreable(AuditType auditType) {
		return auditType.getScoreType() != null;
	}

	public static boolean isScoreApplies(AuditData auditData) {
		return getScorePercentage(auditData) >= 0;
	}

	public static float getScorePercentage(AuditData auditData) {
		float scorePercentage = 0f;
		if (auditData.getAnswer() != null && isMultipleChoice(auditData)) {
			for (AuditOptionValue value : auditData.getQuestion().getOption().getValues()) {
				if (auditData.getAnswer().equals(getIdentifier(value))) {
					scorePercentage = getScorePercent(value);
					break;
				}
			}
		}
		return scorePercentage;
	}

	public static boolean isMultipleChoice(AuditData auditData) {
		return auditData.getQuestion() != null && auditData.getQuestion().getQuestionType().equals("MultipleChoice") && auditData.getQuestion().getOption() != null;
	}

    public static String getIdentifier(AuditOptionValue auditOptionValue) {
        if (!Strings.isEmpty(auditOptionValue.getUniqueCode())) {
            return auditOptionValue.getUniqueCode();
        }
        return auditOptionValue.getId() + "";
    }

	public static float getScorePercent(AuditOptionValue auditOptionValue) {
		if (getMaxScore(auditOptionValue.getGroup()) == 0) {
			return 0;
		}
		return (((float) auditOptionValue.getScore()) / getMaxScore(auditOptionValue.getGroup()));
	}

	public static int getMaxScore(AuditOptionGroup auditOptionGroup) {
        int maxScore = 0;

        for (AuditOptionValue value : auditOptionGroup.getValues()) {
            if (maxScore < value.getScore())
                maxScore = value.getScore();
        }

		return maxScore;
	}

	public static float getScoreValue(AuditData auditData) {
		return Math.round(getScorePercentage(auditData) * auditData.getQuestion().getScoreWeight());
	}

    public static boolean isOK(AuditData auditData) {
        if (!auditData.getQuestion().isHasRequirement())
            return true;

        if (auditData.getAnswer() == null || auditData.getQuestion().getOkAnswer() == null)
            return false;

        if (auditData.getQuestion().getOkAnswer().contains(auditData.getAnswer()))
            return true;

        return false;
    }

    public static Object calculate(AuditQuestionFunction auditQuestionFunction, AnswerMap answerMap, CorruptionPerceptionIndexMap cpiMap) {
        return calculate(auditQuestionFunction, answerMap, cpiMap, null);
    }

	public static Object calculate(AuditQuestionFunction auditQuestionFunction, AnswerMap answerMap, CorruptionPerceptionIndexMap cpiMap, String currentAnswer) {
            Object result;
		try {
            FunctionInput input = new FunctionInput.Builder().answerMap(answerMap).watchers(auditQuestionFunction.getWatchers()).cpiMap(cpiMap).build();
            input.setCurrentAnswer(currentAnswer);
            input.setExpression(auditQuestionFunction.getExpression());
			result = auditQuestionFunction.getFunction().calculate(input);
		}
		catch (NumberFormatException e) {
			result = "Audit.missingParameter";
		}
		return result;
	}

    public static float getStraightScoreValue(AuditData auditData) {
        float straightScoreValue = 0f;
        if (auditData.getAnswer() != null && isMultipleChoice(auditData)) {
            for (AuditOptionValue value : auditData.getQuestion().getOption().getValues()) {
                if (auditData.getAnswer().equals(getIdentifier(value))) {
                    straightScoreValue = value.getScore();
                    break;
                }
            }
        }
        return straightScoreValue;
    }

	public static List<AuditCategory> getTopCategories(AuditType auditType) {
        List<AuditCategory> topCategories = new ArrayList<>();

        for (AuditCategory cat : auditType.getCategories()) {
            if (cat.getParent() == null) {
                topCategories.add(cat);
            }
        }

		return topCategories;
	}

	public static boolean isCurrent(AuditCategory auditCategory) {
		Date now = new Date();
		return isCurrent(auditCategory, now);
	}

	public static boolean isCurrent(AuditCategory auditCategory, Date now) {
		if (auditCategory.getEffectiveDate() != null && auditCategory.getEffectiveDate().after(now)) {
			return false;
		}
		if (auditCategory.getExpirationDate() != null && auditCategory.getExpirationDate().before(now)) {
			return false;
		}
		return true;
	}

    public static boolean isHasSubmittedStep(Workflow workflow) {
        for (WorkflowStep step : workflow.getSteps()) {
            if (step.getNewStatus().isSubmitted())
                return true;
        }
        return false;
    }
}