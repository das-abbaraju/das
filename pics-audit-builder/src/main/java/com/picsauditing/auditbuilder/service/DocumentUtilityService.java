package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.entities.QuestionFunction.FunctionInput;
import com.picsauditing.auditbuilder.util.AnswerMap;
import com.picsauditing.auditbuilder.util.CorruptionPerceptionIndexMap2;
import com.picsauditing.auditbuilder.util.DateBean;
import com.picsauditing.auditbuilder.util.Strings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentUtilityService {
	public static AuditType getAuditType(DocumentQuestion documentQuestion) {
		return getParentAuditType(documentQuestion.getCategory());
	}

	public static AuditType getParentAuditType(DocumentCategory documentCategory) {
		if (documentCategory.getAuditType() == null) {
			return getParentAuditType(documentCategory.getParent());
		}

		return documentCategory.getAuditType();
	}

	public static boolean isMatchingAnswer(DocumentRule documentRule, DocumentData data) {
		if (data == null) {
			if (documentRule.getQuestionComparator() == null)
				return false;

			return documentRule.getQuestionComparator().equals(QuestionComparator.Empty);
		}

		Date auditEffectiveDate = (data.getAudit().getEffectiveDate() != null) ? data.getAudit().getEffectiveDate()
				: new Date();
		if (auditEffectiveDate.before(data.getQuestion().getEffectiveDate()))
			return false;
		if (!auditEffectiveDate.before(data.getQuestion().getExpirationDate()))
			return false;

		if (documentRule.getQuestionComparator() == null)
			return false;
		String answer = data.getAnswer();
		switch (documentRule.getQuestionComparator()) {
		case Empty:
			return Strings.isEmpty(answer);
		case NotEmpty:
			return !Strings.isEmpty(answer);
		case NotEquals:
			return !documentRule.getQuestionAnswer().equals(answer);
		case Verified:
			return isVerified(data);
		case StartsWith:
			return answer.startsWith(documentRule.getQuestionAnswer());
		}

		if (documentRule.getQuestionComparator() == QuestionComparator.LessThan || documentRule.getQuestionComparator() == QuestionComparator.LessThanEqual
				|| documentRule.getQuestionComparator() == QuestionComparator.GreaterThan
				|| documentRule.getQuestionComparator() == QuestionComparator.GreaterThanEqual) {
			if ("Decimal Number".equals(documentRule.getQuestion().getQuestionType()) || "Money".equals(documentRule.getQuestion().getQuestionType())
					|| "Number".equals(documentRule.getQuestion().getQuestionType()) || "Calculation".equals(documentRule.getQuestion().getQuestionType())) {
				try {
					BigDecimal parsedAnswer = new BigDecimal(answer.replace(",", ""));
					BigDecimal parsedQuestionAnswer = new BigDecimal(documentRule.getQuestionAnswer().replace(",", ""));
					if (documentRule.getQuestionComparator() == QuestionComparator.LessThan)
						return parsedAnswer.compareTo(parsedQuestionAnswer) < 0;
					else if (documentRule.getQuestionComparator() == QuestionComparator.LessThanEqual)
						return parsedAnswer.compareTo(parsedQuestionAnswer) <= 0;
					else if (documentRule.getQuestionComparator() == QuestionComparator.GreaterThan)
						return parsedAnswer.compareTo(parsedQuestionAnswer) > 0;
					else if (documentRule.getQuestionComparator() == QuestionComparator.GreaterThanEqual)
						return parsedAnswer.compareTo(parsedQuestionAnswer) >= 0;
				} catch (NumberFormatException nfe) {
					return false;
				}
			} else
				return false;
		}

		return documentRule.getQuestionAnswer().equals(answer);
	}

    public static boolean isUnverified(DocumentData documentData) {
        return documentData.getDateVerified() == null;
    }

    public static boolean isVerified(DocumentData documentData) {
		return documentData.getDateVerified() != null;
	}

	public static int getAuditYear(ContractorDocument contractorDocument) {
		int year = 0;
		if (contractorDocument.getAuditFor() != null) {
			try {
				year = Integer.parseInt(contractorDocument.getAuditFor());
			} catch (NumberFormatException ignored) {
			}
		}
		return year;
	}

    public static boolean isApplies(DocumentRule documentRule, Trade candidate) {
        if (documentRule.getTrade() == null)
            return true;

        if (documentRule.getTrade().equals(candidate))
            return true;

        if (TradeService.childOf(documentRule.getTrade(), candidate))
            return true;

        return TradeService.childOf(candidate, documentRule.getTrade());
    }

    public static boolean isApplies(DocumentRule documentRule, ContractorType candidate) {
        if (documentRule.getContractorType() == null)
            return true;

        return documentRule.getContractorType().equals(candidate);
    }

    public static boolean isApplies(DocumentRule documentRule, OperatorAccount operator) {
		if (documentRule.getOperatorAccount() == null)
			return true;

		if (AccountService.isCorporate(documentRule.getOperatorAccount())) {
			for (Facility facility : operator.getCorporateFacilities()) {
				if (documentRule.getOperatorAccount().equals(facility.getCorporate()))
					return true;
			}
			return false;
		} else {
			return documentRule.getOperatorAccount().equals(operator);
		}
	}

	public static boolean isExpired(ContractorDocument contractorDocument) {
		if (contractorDocument.getExpiresDate() == null)
			return false;
		return contractorDocument.getExpiresDate().before(new Date());
	}

	public static boolean hasCaoStatus(ContractorDocument contractorDocument, DocumentStatus documentStatus) {
		for (ContractorDocumentOperator cao : contractorDocument.getOperators()) {
			if (cao.isVisible() && cao.getStatus().equals(documentStatus))
				return true;
		}
		return false;
	}

	public static boolean hasCaoStatusAfter(ContractorDocument contractorDocument, DocumentStatus documentStatus) {
		return hasCaoStatusAfter(contractorDocument, documentStatus, false);
	}

	public static boolean hasCaoStatusAfter(ContractorDocument contractorDocument, DocumentStatus documentStatus, boolean ignoreNotApplicable) {
		for (ContractorDocumentOperator cao : contractorDocument.getOperators()) {
			if (ignoreNotApplicable && cao.getStatus().equals(DocumentStatus.NotApplicable))
				continue;
			if (cao.isVisible() && cao.getStatus().after(documentStatus)) {
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

    public static boolean willExpireSoon(ContractorDocument contractorDocument) {
        int daysToExpiration = 0;
        if (contractorDocument.getExpiresDate() == null)
            daysToExpiration = 1000;
        else
            daysToExpiration = DateBean.getDateDifference(contractorDocument.getExpiresDate());

        if (contractorDocument.getAuditType().getClassType() == AuditTypeClass.Policy) {
            return daysToExpiration <= 15;
        } else if (contractorDocument.getAuditType().getId() == AuditType.COR) {
            return daysToExpiration <= 180;
        } else if (contractorDocument.getAuditType().getId() == AuditType.SSIP) {
            return daysToExpiration <= 30;
        } else {
            return daysToExpiration <= 90;
        }
    }

	public static boolean isApplies(DocumentCategoryRule documentCategoryRule, DocumentCategory category) {
		if (documentCategoryRule.getDocumentCategory() == null) {
			if (documentCategoryRule.getRootCategory() == null) {
				return true;
			} else {
				if (documentCategoryRule.getRootCategory()) {
					if (category.getParent() == null)
						return true;
				} else {
					if (category.getParent() != null)
						return true;
				}
			}
		} else if (documentCategoryRule.getDocumentCategory().equals(category)) {
			return true;
		}
		return false;
	}

	public static boolean hasCaoStatusBefore(ContractorDocument contractorDocument, DocumentStatus documentStatus) {
		for (ContractorDocumentOperator cao : contractorDocument.getOperators()) {
			if (cao.isVisible() && cao.getStatus().before(documentStatus))
				return true;
		}
		return false;
	}

	public static ContractorDocumentOperatorWorkflow changeStatus(ContractorDocumentOperator contractorDocumentOperator, DocumentStatus documentStatus) {
		if (documentStatus.equals(contractorDocumentOperator.getStatus()))
			return null;

		ContractorDocumentOperatorWorkflow caow = new ContractorDocumentOperatorWorkflow();
		caow.setCao(contractorDocumentOperator);
		caow.setPreviousStatus(contractorDocumentOperator.getStatus());
		caow.setStatus(documentStatus);
		caow.setAuditColumns();

        contractorDocumentOperator.setAuditColumns();
        contractorDocumentOperator.setStatusChangedDate(new Date());
		contractorDocumentOperator.setStatus(documentStatus);

		if (contractorDocumentOperator.getStatus() != DocumentStatus.Incomplete) {
            contractorDocumentOperator.setDocumentSubStatus(null);
		}

		if (contractorDocumentOperator.getAudit().getAuditType().getId() == AuditType.PQF || contractorDocumentOperator.getAudit().getAuditType().getId() == AuditType.ANNUALADDENDUM)
			return caow;

		if (documentStatus == DocumentStatus.Pending)
			return caow;

		if (contractorDocumentOperator.getAudit().getEffectiveDate() == null)
            contractorDocumentOperator.getAudit().setEffectiveDate(new Date());

		return caow;
	}

	public static boolean isVisibleInAudit(DocumentQuestion documentQuestion, ContractorDocument audit) {
		for (DocumentCatData category : audit.getCategories()) {
			if (category.getCategory().getId() == documentQuestion.getCategory().getId()) {
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

	public static boolean pqfIsOkayToChangeCaoStatus(ContractorDocument contractorDocument, ContractorDocumentOperator cao) {
		if (contractorDocument.getAuditType().getId() == AuditType.PQF && cao.getPercentVerified() == 100) {
			for (DocumentData data : contractorDocument.getData()) {
				if (data.getQuestion().getId() == DocumentQuestion.MANUAL_PQF && isUnverified(data)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

    public static DocumentCategory getTopParent(DocumentCategory documentCategory) {
        if (documentCategory.getParent() != null) {
            return getTopParent(documentCategory.getParent());
        }

        return documentCategory;
    }

	public static Date getValidDate(ContractorDocument contractorDocument) {
		if (contractorDocument.getAuditType().getId() == AuditType.ANNUALADDENDUM)
			return contractorDocument.getEffectiveDate();
		if (hasCaoStatusAfter(contractorDocument, DocumentStatus.Incomplete)) {
			if (contractorDocument.getEffectiveDate() == null)
				return new Date();
			else
				return contractorDocument.getEffectiveDate();
		} else
			return new Date();
	}

	public static boolean isValidQuestion(DocumentQuestion documentQuestion, Date validDate) {
		if (validDate.after(documentQuestion.getEffectiveDate()) && validDate.before(documentQuestion.getExpirationDate())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isVisible(DocumentQuestion documentQuestion, DocumentData data) {
		if (documentQuestion.getVisibleQuestion() != null && documentQuestion.getVisibleAnswer() != null) {
			String answer = null;
			if (data != null) {
				answer = data.getAnswer();
			}
			return testVisibility(answer, documentQuestion.getVisibleAnswer());
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

	public static boolean isAnswered(DocumentData documentData) {
		if (documentData.getAnswer() != null && documentData.getAnswer().length() > 0 && !documentData.getAnswer().equals(DateBean.NULL_DATE_DB)) {
			return true;
		}
		return false;
	}

	public static boolean isScoreable(AuditType auditType) {
		return auditType.getScoreType() != null;
	}

	public static boolean isScoreApplies(DocumentData documentData) {
		return getScorePercentage(documentData) >= 0;
	}

	public static float getScorePercentage(DocumentData documentData) {
		float scorePercentage = 0f;
		if (documentData.getAnswer() != null && isMultipleChoice(documentData)) {
			for (DocumentOptionValue value : documentData.getQuestion().getOption().getValues()) {
				if (documentData.getAnswer().equals(getIdentifier(value))) {
					scorePercentage = getScorePercent(value);
					break;
				}
			}
		}
		return scorePercentage;
	}

	public static boolean isMultipleChoice(DocumentData documentData) {
		return documentData.getQuestion() != null && documentData.getQuestion().getQuestionType().equals("MultipleChoice") && documentData.getQuestion().getOption() != null;
	}

    public static String getIdentifier(DocumentOptionValue documentOptionValue) {
        if (!Strings.isEmpty(documentOptionValue.getUniqueCode())) {
            return documentOptionValue.getUniqueCode();
        }
        return documentOptionValue.getId() + "";
    }

	public static float getScorePercent(DocumentOptionValue documentOptionValue) {
		if (getMaxScore(documentOptionValue.getGroup()) == 0) {
			return 0;
		}
		return (((float) documentOptionValue.getScore()) / getMaxScore(documentOptionValue.getGroup()));
	}

	public static int getMaxScore(DocumentOptionGroup documentOptionGroup) {
        int maxScore = 0;

        for (DocumentOptionValue value : documentOptionGroup.getValues()) {
            if (maxScore < value.getScore())
                maxScore = value.getScore();
        }

		return maxScore;
	}

	public static float getScoreValue(DocumentData documentData) {
		return Math.round(getScorePercentage(documentData) * documentData.getQuestion().getScoreWeight());
	}

    public static boolean isOK(DocumentData documentData) {
        if (!documentData.getQuestion().isHasRequirement())
            return true;

        if (documentData.getAnswer() == null || documentData.getQuestion().getOkAnswer() == null)
            return false;

        if (documentData.getQuestion().getOkAnswer().contains(documentData.getAnswer()))
            return true;

        return false;
    }

    public static Object calculate(DocumentQuestionFunction documentQuestionFunction, AnswerMap answerMap, CorruptionPerceptionIndexMap2 cpiMap) {
        return calculate(documentQuestionFunction, answerMap, cpiMap, null);
    }

	public static Object calculate(DocumentQuestionFunction documentQuestionFunction, AnswerMap answerMap, CorruptionPerceptionIndexMap2 cpiMap, String currentAnswer) {
            Object result;
		try {
            FunctionInput input = new FunctionInput.Builder().answerMap(answerMap).watchers(documentQuestionFunction.getWatchers()).cpiMap(cpiMap).build();
            input.setCurrentAnswer(currentAnswer);
            input.setExpression(documentQuestionFunction.getExpression());
			result = documentQuestionFunction.getFunction().calculate(input);
		}
		catch (NumberFormatException e) {
			result = "Audit.missingParameter";
		}
		return result;
	}

    public static float getStraightScoreValue(DocumentData documentData) {
        float straightScoreValue = 0f;
        if (documentData.getAnswer() != null && isMultipleChoice(documentData)) {
            for (DocumentOptionValue value : documentData.getQuestion().getOption().getValues()) {
                if (documentData.getAnswer().equals(getIdentifier(value))) {
                    straightScoreValue = value.getScore();
                    break;
                }
            }
        }
        return straightScoreValue;
    }

	public static List<DocumentCategory> getTopCategories(AuditType auditType) {
        List<DocumentCategory> topCategories = new ArrayList<>();

        for (DocumentCategory cat : auditType.getCategories()) {
            if (cat.getParent() == null) {
                topCategories.add(cat);
            }
        }

		return topCategories;
	}

	public static boolean isCurrent(DocumentCategory documentCategory) {
		Date now = new Date();
		return isCurrent(documentCategory, now);
	}

	public static boolean isCurrent(DocumentCategory documentCategory, Date now) {
		if (documentCategory.getEffectiveDate() != null && documentCategory.getEffectiveDate().after(now)) {
			return false;
		}
		if (documentCategory.getExpirationDate() != null && documentCategory.getExpirationDate().before(now)) {
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