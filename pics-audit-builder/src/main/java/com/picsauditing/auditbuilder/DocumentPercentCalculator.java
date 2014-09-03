package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.ContractorDocumentDAO;
import com.picsauditing.auditbuilder.dao.ContractorDocumentOperatorDAO;
import com.picsauditing.auditbuilder.dao.DocumentDataDAO;
import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.service.DocumentPeriodService;
import com.picsauditing.auditbuilder.service.DocumentUtilityService;
import com.picsauditing.auditbuilder.service.DocumentAutoAdvancer;
import com.picsauditing.auditbuilder.util.AnswerMap;
import com.picsauditing.auditbuilder.util.CorruptionPerceptionIndexMap2;
import com.picsauditing.auditbuilder.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class DocumentPercentCalculator {
	@Autowired
	private DocumentCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	private CorruptionPerceptionIndexMap2 corruptionPerceptionIndexMap;
	@Autowired
	private DocumentDataDAO auditDataDAO;
	@Autowired
	protected ContractorDocumentOperatorDAO caoDAO;
    @Autowired
    protected DocumentPeriodService auditPeriodService;
    @Autowired
    protected ContractorDocumentDAO contractorAuditDAO;

	protected float subScorePossible;
	private final Logger logger = LoggerFactory.getLogger(DocumentPercentCalculator.class);

    public void updatePercentageCompleted(int auditCatDataId) {
        DocumentCatData documentCatData = contractorAuditDAO.find(DocumentCatData.class, auditCatDataId);
        updatePercentageCompleted(documentCatData);
    }

	public void updatePercentageCompleted(DocumentCatData catData) {
		if (catData == null)
			return;

		if (!catData.isApplies())
			return;

        if (!DocumentUtilityService.isCurrent(catData.getCategory()))
            return;

        int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;
		float scoreWeight = 0;
		float score = 0;
		HashSet<Integer> circularRequiredQuestionIds = new HashSet<>();
		HashSet<Integer> circularVisualQuestionIds = new HashSet<>();

		Date validDate = DocumentUtilityService.getValidDate(catData.getAudit());

		Set<Integer> questionIDs = collectQuestionIdsFromAuditCatData(catData);
		Collection<Integer> functionWatcherQuestionIds = collectFunctionWatcherQuestionIdsFromAuditCatData(catData);

        runRollupFunctions(catData, validDate);

        AnswerMap currentWatcherAnswers = auditDataDAO.findAnswersByAuditAndQuestions(catData.getAudit(),
                functionWatcherQuestionIds);

        runCalculationFunctions(catData, validDate, currentWatcherAnswers);

		List<DocumentData> requiredAnswers = new ArrayList<>();
		for (DocumentData answer : catData.getAudit().getData())
			if (questionIDs.contains(answer.getQuestion().getId()))
				requiredAnswers.add(answer);
		AnswerMap answers = new AnswerMap(requiredAnswers);

		for (DocumentQuestion question : catData.getCategory().getQuestions()) {

			if (DocumentUtilityService.isValidQuestion(question, validDate)) {
				DocumentQuestion questionBeingReviewed = question;
				boolean isRequired = questionBeingReviewed.isRequired();

				DocumentData answer = answers.get(questionBeingReviewed.getId());
                boolean isVisibleToRecalculate = isVisibleToRecalculate(answers.get(question.getId()), answers);

				circularRequiredQuestionIds.clear();
				while (questionBeingReviewed != null) {
					if (questionBeingReviewed.getRequiredQuestion() != null
							&& questionBeingReviewed.getRequiredAnswer() != null) {
						if (questionBeingReviewed.getRequiredAnswer().equals("NULL")) {
							DocumentData otherAnswer = answers.get(questionBeingReviewed.getRequiredQuestion().getId());
							if (otherAnswer == null || Strings.isEmpty(otherAnswer.getAnswer()))
								isRequired = true;
						} else if (questionBeingReviewed.getRequiredAnswer().equals("NOTNULL")) {
							DocumentData otherAnswer = answers.get(questionBeingReviewed.getRequiredQuestion().getId());
							if (otherAnswer != null && !Strings.isEmpty(otherAnswer.getAnswer()))
								isRequired = true;
						} else {
							DocumentData otherAnswer = answers.get(questionBeingReviewed.getRequiredQuestion().getId());
							if (otherAnswer != null
									&& questionBeingReviewed.getRequiredAnswer().equals(otherAnswer.getAnswer()))
								isRequired = true;
						}
					}

					if (isRequired) {
						if (catData.getAudit().getEffectiveDate() != null
								&& catData.getAudit().getEffectiveDate()
										.before(questionBeingReviewed.getEffectiveDate()))
							isRequired = false;
						else if (questionBeingReviewed.getVisibleQuestion() != null
								&& questionBeingReviewed.getVisibleAnswer() != null) {
							DocumentData otherAnswer = answers.get(questionBeingReviewed.getVisibleQuestion().getId());
							if (!DocumentUtilityService.isVisible(questionBeingReviewed, otherAnswer))
								isRequired = false;
						}

						DocumentQuestion questionVisibilityParent = questionBeingReviewed.getVisibleQuestion();
						circularVisualQuestionIds.clear();
						while (questionVisibilityParent != null && isRequired) {
							if (questionVisibilityParent.getVisibleQuestion() != null
									&& questionVisibilityParent.getVisibleAnswer() != null) {
								if (!DocumentUtilityService.isVisible(questionVisibilityParent, answers.get(questionVisibilityParent
                                        .getVisibleQuestion().getId()))) {
									isRequired = false;
                                    isVisibleToRecalculate = false;
                                }
							}

							if (circularVisualQuestionIds.contains(questionVisibilityParent.getId())) {
								logger.warn("Circular visible questions detected with question id {}",
										questionVisibilityParent.getId());
								break;
							}
							circularVisualQuestionIds.add(questionVisibilityParent.getId());
							questionVisibilityParent = questionVisibilityParent.getVisibleQuestion();
						}
					}

					if (!isRequired)
						break;

					if (circularRequiredQuestionIds.contains(questionBeingReviewed.getId())) {
						logger.warn("Circular required questions detected with question id {}",
								questionBeingReviewed.getId());
						break;
					}
					circularRequiredQuestionIds.add(questionBeingReviewed.getId());
					questionBeingReviewed = questionBeingReviewed.getRequiredQuestion();
				}

				if (isRequired) {
					requiredCount++;
				}

				if (answer != null) {
					if (DocumentUtilityService.isAnswered(answer)) {
						if (DocumentUtilityService.isScoreable(catData.getAudit().getAuditType())) {
							if (DocumentUtilityService.isScoreApplies(answer) && isVisibleToRecalculate) {
								score += DocumentUtilityService.getScoreValue(answer);
								scoreWeight += question.getScoreWeight();
							}
						}

						answeredCount++;
						if (isRequired)
							requiredAnsweredCount++;
					}

					if (answer.getQuestion().isHasRequirement()) {
						if (DocumentUtilityService.isOK(answer))
							verifiedCount++;
					} else {
						if (isRequired) {
							verifiedCount = addVerifiedCount(catData, requiredCount, verifiedCount, answer);
						}
					}
				}
			}
		}

		catData.setNumAnswered(answeredCount);
		catData.setNumRequired(requiredCount);
		catData.setRequiredCompleted(requiredAnsweredCount);
		catData.setNumVerified(verifiedCount);
		catData.setScore(score);
		catData.setScorePossible(scoreWeight);
	}

    private boolean isVisibleToRecalculate(DocumentData data, AnswerMap answers) {
        boolean visible = true;

        if (data == null)
            return true;

        if (data.getQuestion().getVisibleQuestion() != null) {
            DocumentData answer = answers.get(data.getQuestion().getVisibleQuestion().getId());
            if (answer == null || !DocumentUtilityService.isVisible(data.getQuestion(), answer))
                return false;

        }

        return visible;
    }

    private void runCalculationFunctions(DocumentCatData catData, Date validDate, AnswerMap currentWatcherAnswers) {
        for (DocumentQuestion question : catData.getCategory().getQuestions())
            if (DocumentUtilityService.isValidQuestion(question, validDate) && question.getFunctions().size() > 0) {
                DocumentData target = auditDataDAO.findAnswerByAuditQuestion(catData.getAudit().getId(), question.getId());
                boolean newTarget = false;

                if (target == null) {
                    target = new DocumentData();
                    target.setAudit(catData.getAudit());
                    target.setQuestion(question);
                    newTarget = true;
                }

                for (DocumentQuestionFunction function : question.getFunctions()) {
                    if (function.getType() == QuestionFunctionType.Calculation) {
                        String results = null;
                        if (!DocumentUtilityService.isAnswered(target) || function.isOverwrite()) {
                            if (function.getFunction() == QuestionFunction.AUDIT_SCORE) {
                                results = String.valueOf(catData.getAudit().getScore());
                            } else {
                                Object calculation = DocumentUtilityService.calculate(function, currentWatcherAnswers, corruptionPerceptionIndexMap);
                                if (calculation != null) {
                                    results = calculation.toString();
                                }
                            }
                            if (results != null) {
                                target.setAnswer(results);
                                target.setAuditColumns(new User(User.SYSTEM));
                            }
                        }
                    }
                }

                if (newTarget) {
                    auditDataDAO.save(target);
                }

                for (DocumentQuestionFunctionWatcher aqfw : question.getFunctionWatchers()) {
                    if (aqfw.getFunction().getQuestion().getCategory().getId() != question.getCategory().getId()) {
                        DocumentCatData aqfwCatData = findCatData(catData.getAudit(), aqfw.getFunction().getQuestion().getCategory());
                        if (aqfwCatData == null) {
                            continue;
                        }
                        updatePercentageCompleted(aqfwCatData);
                    }
                }
            }
    }

    private void runRollupFunctions(DocumentCatData catData, Date validDate) {
        for (DocumentQuestion question : catData.getCategory().getQuestions())
            if (DocumentUtilityService.isValidQuestion(question, validDate) && question.getFunctions().size() > 0) {
                DocumentData target = auditDataDAO.findAnswerByAuditQuestion(catData.getAudit().getId(), question.getId());

                if (target == null) {
                    target = new DocumentData();
                    target.setAudit(catData.getAudit());
                    target.setQuestion(question);
                }

                String results;

                for (DocumentQuestionFunction function : question.getFunctions()) {
                    if (function.getType() == QuestionFunctionType.Rollup) {
                        results = calculateRollup(catData.getAudit().getContractorAccount().getId(),
                                function, catData.getAudit().getAuditFor());
                        if (results != null) {
                            target.setAnswer(results);
                            target.setAuditColumns(new User(User.SYSTEM));
                            auditDataDAO.save(target);
                        }
                   }
                }
            }
    }

    private String calculateRollup(int conId, DocumentQuestionFunction function, String parentAuditFor) {
        if (function.getWatchers().size() == 0)
            return null;
        int questionId = function.getWatchers().get(0).getQuestion().getId();
        int auditTypeId = DocumentUtilityService.getAuditType(function.getWatchers().get(0).getQuestion()).getId();

        List<String> auditFors = auditPeriodService.getChildPeriodAuditFors(parentAuditFor);
        List<ContractorDocument> sourceAudits = contractorAuditDAO.findAuditsByContractorAuditTypeAuditFors(conId, auditTypeId, auditFors);

        int sum = 0;
        boolean foundOne = false;
        for (ContractorDocument audit:sourceAudits) {
            if (DocumentUtilityService.hasCaoStatus(audit, DocumentStatus.Complete)) {
                DocumentData data = auditDataDAO.findAnswerByAuditQuestion(audit.getId(), questionId);
                if (data != null) {
                    try {
                        sum += Integer.parseInt(data.getAnswer());
                        foundOne = true;
                    } catch (Exception e) {

                    }
                }
            }
        }

        if (foundOne) {
            return "" + sum;
        }

        return QuestionFunction.MISSING_PARAMETER;
    }

    private DocumentCatData findCatData(ContractorDocument audit, DocumentCategory category) {
		for (DocumentCatData catData:audit.getCategories()) {
			if (catData.getCategory().getId() == category.getId()) {
				return catData;
			}
		}
		return null;
	}

	private Collection<Integer> collectFunctionWatcherQuestionIdsFromAuditCatData(DocumentCatData catData) {
		Collection<Integer> functionWatcherQuestionIds = new ArrayList<>();
		Date validDate = DocumentUtilityService.getValidDate(catData.getAudit());

		for (DocumentQuestion question : catData.getCategory().getQuestions()) {
			if (DocumentUtilityService.isValidQuestion(question, validDate)) {
				for (DocumentQuestionFunction aqf : question.getFunctions())
					for (DocumentQuestionFunctionWatcher aqfw : aqf.getWatchers())
						if (DocumentUtilityService.isValidQuestion(aqfw.getQuestion(), validDate))
							functionWatcherQuestionIds.add(aqfw.getQuestion().getId());
			}
		}

		return functionWatcherQuestionIds;
	}

	private Set<Integer> collectQuestionIdsFromAuditCatData(DocumentCatData catData) {
		Set<Integer> questionIDs = new HashSet<>();

		for (DocumentQuestion question : catData.getCategory().getQuestions()) {
			questionIDs.add(question.getId());

			if (question.getRequiredQuestion() != null) {
				questionIDs.addAll(collectChainOfRequiredQuestionIds(question));
			}

			if (question.getVisibleQuestion() != null) {
				questionIDs.addAll(collectChainOfVisibleQuestionIds(question));
			}
		}
		return questionIDs;
	}

	private Set<Integer> collectChainOfRequiredQuestionIds(DocumentQuestion question) {
		Set<Integer> questionIDs = new HashSet<>();
		HashSet<Integer> requiredQuestionIdsSeen = new HashSet<>();
		DocumentQuestion q = question.getRequiredQuestion();
		while (q != null) {
			questionIDs.add(q.getId());
			if (requiredQuestionIdsSeen.contains(q.getId())) {
				logger.warn("Circular required questions detected with question id {}", q.getId());
				return questionIDs;
			}
			requiredQuestionIdsSeen.add(q.getId());
			q = q.getRequiredQuestion();
		}
		return questionIDs;
	}

	private Set<Integer> collectChainOfVisibleQuestionIds(DocumentQuestion question) {
		Set<Integer> questionIDs = new HashSet<>();
		HashSet<Integer> visibleQuestionIdsSeen = new HashSet<>();
		DocumentQuestion q = question.getVisibleQuestion();
		while (q != null) {
			questionIDs.add(q.getId());
			if (visibleQuestionIdsSeen.contains(q.getId())) {
				logger.warn("Circular visible questions detected with question id {}", q.getId());
				return questionIDs;
			}
			visibleQuestionIdsSeen.add(q.getId());
			q = q.getVisibleQuestion();
		}
		return questionIDs;
	}

	private int addVerifiedCount(DocumentCatData catData, int requiredCount, int verifiedCount, DocumentData answer) {
		DocumentQuestion question = answer.getQuestion();

		if (DocumentUtilityService.isVerified(answer))
			verifiedCount++;
		else if (catData.getAudit().getAuditType().getWorkFlow().isHasRequirements()) {
			verifiedCount++;
		} else if (question.getId() == 2447 || question.getId() == 2448 || question.getId() == 10217
				|| question.getId() == 15353 || question.getId() == 15354) {
			verifiedCount++;
		} else if (catData.getAudit().getAuditType().getId() == AuditType.PQF) {
			boolean needsVerification = false;
			for (DocumentData documentData : getVerifiedPqfData(catData.getAudit().getId())) {
				if (documentData.getQuestion().equals(answer.getQuestion())) {
					needsVerification = true;
					break;
				}
			}
			if (!needsVerification)
				verifiedCount++;
		} else if (catData.getAudit().getAuditType().getClassType().isPolicy()) {
			verifiedCount = requiredCount;
		} else if (!DocumentUtilityService.isHasSubmittedStep(catData.getAudit().getAuditType().getWorkFlow())) {
			verifiedCount++;
		}
		int categoryId = catData.getCategory().getId();

		if (categoryId == OshaDocument.CAT_ID_COHS || categoryId == OshaDocument.CAT_ID_OSHA_ADDITIONAL
				|| categoryId == OshaDocument.CAT_ID_MSHA) {
			verifiedCount++;
		}

        if ("Calculation".equals(question.getQuestionType()))
            verifiedCount++;

		return verifiedCount;
	}

    public void percentCalculateComplete(ContractorDocument conAudit, boolean recalcCats) {
        percentCalculateComplete(conAudit, recalcCats, true);
    }

    public void percentCalculateComplete(int auditID, boolean recalcCats) {
        ContractorDocument conAudit = contractorAuditDAO.find(auditID);
        percentCalculateComplete(conAudit, recalcCats, true);
        conAudit.setLastRecalculation(new Date());
        conAudit.setAuditColumns();
        contractorAuditDAO.save(conAudit);
    }

    public void percentCalculateComplete(int auditID, boolean recalcCats, boolean advanceCaos) {
        ContractorDocument conAudit = contractorAuditDAO.find(auditID);
        percentCalculateComplete(conAudit, recalcCats, advanceCaos);
        conAudit.setLastRecalculation(new Date());
        conAudit.setAuditColumns();
        contractorAuditDAO.save(conAudit);
    }

    public void percentCalculateComplete(ContractorDocument conAudit, boolean recalcCats, boolean advanceCaos) {
        if (recalcCats)
            recalcAllAuditCatDatas(conAudit);

        DocumentCategoriesBuilder builder = new DocumentCategoriesBuilder(auditCategoryRuleCache,
                conAudit.getContractorAccount());

        builder.calculate(conAudit);

        for (ContractorDocumentOperator cao : conAudit.getOperators()) {
            int required = 0;
            int answered = 0;
            int verified = 0;

            float score = 0;

            for (DocumentCatData data : conAudit.getCategories()) {
                boolean applies = doesCategoryApply(conAudit, builder, cao, data);

                if (applies) {
                    required += data.getNumRequired();
                    answered += data.getRequiredCompleted();
                    verified += data.getNumVerified();

                    if (shouldAdjustAuditScore(conAudit, cao)) {
                        if (data.getScorePossible() > 0) {
                            score += data.getScore();
                        }
                    }
                }
            }

            if (shouldAdjustAuditScore(conAudit, cao)) {
                if (conAudit.getAuditType().getScoreType() == ScoreType.Percent)
                    conAudit.setScore((int) Math.min(Math.round(score), 100L));
                else if (conAudit.getAuditType().getScoreType() == ScoreType.Actual)
                    conAudit.setScore(Math.round(score));
            }

            int percentComplete = 0;
            int percentVerified = 0;
            if (required > 0) {
                if (isNotSubmittedPolicySoAutoSubmitDoesNotRecalculatePercentComplete(conAudit, cao)) {
                    percentComplete = (int) Math.floor(100 * answered / required);
                    if (percentComplete >= 100) {
                        percentComplete = 100;
                    }

                } else {
                    percentComplete = cao.getPercentComplete();
                }
                percentVerified = (int) Math.floor(100 * verified / required);
                if (percentVerified >= 100) {
                    percentVerified = 100;
                }
            } else if (!conAudit.getAuditType().getClassType().isPolicy()) {
                percentComplete = 100;
                percentVerified = 100;
            } else {
                logger.warn("Policy with no required questions {}", conAudit);
            }

            cao.setPercentComplete(percentComplete);
            cao.setPercentVerified(percentVerified);

            if (advanceCaos) {
                ContractorDocumentOperatorWorkflow caow = DocumentAutoAdvancer.advanceCaoStatus(conAudit, cao, percentComplete, percentVerified);
                if (caow != null) {
                    caoDAO.save(caow);
                }
            }
        }

        if (conAudit.getAuditType().getScoreType() == ScoreType.Weighted) {
            calculateWeightedScore(conAudit);
        }
        if (conAudit.getAuditType().getScoreType() == ScoreType.Aggregate) {
            calculateStraightAggregate(conAudit);
        }
    }

    private boolean doesCategoryApply(ContractorDocument conAudit, DocumentCategoriesBuilder builder, ContractorDocumentOperator cao, DocumentCatData data) {
        boolean applies = false;
        if (!DocumentUtilityService.isCurrent(data.getCategory()))
            return false;

        if (data.isOverride())
            applies = data.isApplies();
        else {
            if (data.isApplies()) {
                if (conAudit.getAuditType().getId() == AuditType.MANUAL_AUDIT && cao.getStatus().after(DocumentStatus.Incomplete))
                    applies = true;
                else if (conAudit.getAuditType().getId() == AuditType.IMPORT_PQF)
                    applies = true;
                else if (conAudit.getAuditType().getId() == AuditType.WELCOME)
                    applies = true;
                else {
                    applies = builder.isCategoryApplicable(data.getCategory(), cao);
                    DocumentCategory parent = data.getCategory().getParent();
                    while (parent != null && applies) {
                        applies = builder.isCategoryApplicable(parent, cao);
                        parent = parent.getParent();
                    }
                }
            }
        }
        return applies;
    }

    private boolean shouldAdjustAuditScore(ContractorDocument conAudit, ContractorDocumentOperator cao) {
        if (!cao.isVisible())
            return false;
        if (conAudit.getAuditType().getScoreType() == ScoreType.Percent)
            return true;
        if (conAudit.getAuditType().getScoreType() == ScoreType.Actual)
            return true;
        return false;
    }

    private boolean isNotSubmittedPolicySoAutoSubmitDoesNotRecalculatePercentComplete(ContractorDocument conAudit, ContractorDocumentOperator cao) {
        if (conAudit.getAuditType().getClassType() != DocumentTypeClass.Policy) {
            return true;
        }

        if (cao.getStatus().after(DocumentStatus.Incomplete)) {
            return false;
        }

        return true;
    }

    private void calculateStraightAggregate(ContractorDocument scoredAudit) {
		float runningTotal = 0;
		for (DocumentData documentData : scoredAudit.getData()) {
			runningTotal += DocumentUtilityService.getStraightScoreValue(documentData);
		}
		int score = Math.round(runningTotal);
		scoredAudit.setScore(score);
	}

	private void calculateWeightedScore(ContractorDocument ca) {
		float cumulativeScore = 0f;

		Map<DocumentCategory, DocumentCatData> catDatas = new HashMap<>();
		for (DocumentCatData data : ca.getCategories()) {
			catDatas.put(data.getCategory(), data);
		}

		for (DocumentCategory category : DocumentUtilityService.getTopCategories(ca.getAuditType())) {
			subScorePossible = 0f;
			float score = calculateWeightedScore(category, catDatas);
			if (subScorePossible > 0)
				cumulativeScore += category.getScoreWeight() * score;
			else
				cumulativeScore += category.getScoreWeight();
		}

		ca.setScore(Math.min(Math.round(cumulativeScore), 100));
	}

	private float calculateWeightedScore(DocumentCategory category, Map<DocumentCategory, DocumentCatData> catDatas) {
		float subScore = 0f;
		float scorePossible = 0f;

		if (category.getSubCategories().size() > 0) {
			for (DocumentCategory subCategory : category.getSubCategories()) {
				float runningScore = calculateWeightedScore(subCategory, catDatas);

				if (catDatas.get(subCategory).getScorePossible() > 0) {
					subScore += runningScore;
					scorePossible += subCategory.getScoreWeight();
				}
			}

			if (scorePossible > 0) {
				subScore = subScore / scorePossible;
			}
		} else {
			if (catDatas.get(category).getScorePossible() > 0)
				subScore += catDatas.get(category).getScore() / catDatas.get(category).getScorePossible();
			scorePossible = catDatas.get(category).getScorePossible();
		}

		if (category.getParent() == null) {
			subScorePossible = scorePossible;
		}
		return subScore;
	}

    public void recalcAllAuditCatDatas(int auditId) {
        ContractorDocument audit = contractorAuditDAO.find(auditId);
        recalcAllAuditCatDatas(audit);
    }

    public void recalcAllAuditCatDatas(ContractorDocument conAudit) {
		for (DocumentCatData data : conAudit.getCategories()) {
			updatePercentageCompleted(data);
		}
	}

	public List<DocumentData> getVerifiedPqfData(int auditID) {
		return auditDataDAO.findCustomPQFVerifications(auditID);
	}
}