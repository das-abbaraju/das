package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

public class AuditPercentCalculator {
	@Autowired
	private AuditCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	private AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;

	protected float subScorePossible;
	private final Logger logger = LoggerFactory.getLogger(AuditPercentCalculator.class);

	/**
	 * Calculate the percent complete for all questions in this category
	 *
	 * @param catData
	 */
	public void updatePercentageCompleted(AuditCatData catData) {
		if (catData == null)
			return;

		if (!catData.isApplies())
			return;

		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;
		float scoreWeight = 0;
		float score = 0;
		HashSet<Integer> circularRequiredQuestionIds = new HashSet<Integer>();
		HashSet<Integer> circularVisualQuestionIds = new HashSet<Integer>();

		Date validDate = catData.getAudit().getValidDate();

		// Get a list of questions/answers for this category
		Set<Integer> questionIDs = collectQuestionIdsFromAuditCatData(catData);
		Collection<Integer> functionWatcherQuestionIds = collectFunctionWatcherQuestionIdsFromAuditCatData(catData);

		AnswerMap currentWatcherAnswers = auditDataDAO.findAnswersByAuditAndQuestions(catData.getAudit()
				, functionWatcherQuestionIds);

		// Run functions to update answers
		for (AuditQuestion question : catData.getCategory().getQuestions()) {
			if (question.isValidQuestion(validDate) && question.getFunctions().size() > 0) {
				AuditData target = auditDataDAO.findAnswerByAuditQuestion(catData.getAudit().getId(), question.getId());

				if (target == null) {
					target = new AuditData();
					target.setAudit(catData.getAudit());
					target.setQuestion(question);
				}

				String results = null;

				for (AuditQuestionFunction function : question.getFunctions()) {
					if (function.getType() == QuestionFunctionType.Calculation) {
						if (!target.isAnswered() || function.isOverwrite()) {
                            if (function.getFunction() == QuestionFunction.AUDIT_SCORE) {
                                results = String.valueOf(catData.getAudit().getScore());
                            }
                            else {
                                Object calculation = function.calculate(currentWatcherAnswers);
                                if (calculation != null) {
                                    results = calculation.toString();
                                    break;
                                }
                            }
						}
					}
				}

				if (results != null) {
					target.setAnswer(results);
					target.setAuditColumns(new User(User.SYSTEM));
				}

				if (!catData.getAudit().getData().contains(target))
					catData.getAudit().getData().add(target);
			}
		}

		// Get a map of all answers in this audit
		List<AuditData> requiredAnswers = new ArrayList<AuditData>();
		for (AuditData answer : catData.getAudit().getData())
			if (questionIDs.contains(answer.getQuestion().getId()))
				requiredAnswers.add(answer);
		AnswerMap answers = new AnswerMap(requiredAnswers);

		// Get a list of questions/answers for this category
		for (AuditQuestion question : catData.getCategory().getQuestions()) {

			if (question.isValidQuestion(validDate)) {
				AuditQuestion questionBeingReviewed = question;
				boolean isRequired = questionBeingReviewed.isRequired();;

				AuditData answer = answers.get(questionBeingReviewed.getId());

				circularRequiredQuestionIds.clear();
				while (questionBeingReviewed != null) {
					if (questionBeingReviewed.getRequiredQuestion() != null && questionBeingReviewed.getRequiredAnswer() != null) {
						if (questionBeingReviewed.getRequiredAnswer().equals("NULL")) {
							AuditData otherAnswer = answers.get(questionBeingReviewed.getRequiredQuestion().getId());
							if (otherAnswer == null || Strings.isEmpty(otherAnswer.getAnswer()))
								isRequired = true;
						} else if (questionBeingReviewed.getRequiredAnswer().equals("NOTNULL")) {
							AuditData otherAnswer = answers.get(questionBeingReviewed.getRequiredQuestion().getId());
							if (otherAnswer != null && !Strings.isEmpty(otherAnswer.getAnswer()))
								isRequired = true;
						} else {
							// This question is dependent on another
							// question's answer
							// Use the parentAnswer, so we get answers in
							// the same tuple as this one
							AuditData otherAnswer = answers.get(questionBeingReviewed.getRequiredQuestion().getId());
							if (otherAnswer != null && questionBeingReviewed.getRequiredAnswer().equals(otherAnswer.getAnswer()))
								isRequired = true;
						}
					}

					// make sure this dependent required question is visible
					if (isRequired) {
						if (catData.getAudit().getEffectiveDate() != null && catData.getAudit().getEffectiveDate().before(questionBeingReviewed.getEffectiveDate()))
							isRequired = false;
						else if (questionBeingReviewed.getVisibleQuestion() != null && questionBeingReviewed.getVisibleAnswer() != null) {
							AuditData otherAnswer = answers.get(questionBeingReviewed.getVisibleQuestion().getId());
							if (!questionBeingReviewed.isVisible(otherAnswer))
								isRequired = false;
						}

						// is visible question visible
						AuditQuestion questionVisibilityParent = questionBeingReviewed.getVisibleQuestion();
						circularVisualQuestionIds.clear();
						while (questionVisibilityParent != null && isRequired) {
							if (questionVisibilityParent.getVisibleQuestion() != null && questionVisibilityParent.getVisibleAnswer() != null) {
								if (!questionVisibilityParent.isVisible(answers.get(questionVisibilityParent.getVisibleQuestion().getId())))
									isRequired = false;
							}

							if (circularVisualQuestionIds.contains(questionVisibilityParent.getId())) {
								logger.warn("Circular visible questions detected with question id {}", questionVisibilityParent.getId());
								break;
							}
							circularVisualQuestionIds.add(questionVisibilityParent.getId());
							questionVisibilityParent = questionVisibilityParent.getVisibleQuestion();
						}
					}

					if (!isRequired)
						break;

					if (circularRequiredQuestionIds.contains(questionBeingReviewed.getId())) {
						logger.warn("Circular required questions detected with question id {}", questionBeingReviewed.getId());
						break;
					}
					circularRequiredQuestionIds.add(questionBeingReviewed.getId());
					questionBeingReviewed = questionBeingReviewed.getRequiredQuestion();
				}

				if (isRequired) {
					requiredCount++;
				}

				if (answer != null) {
					if (answer.isAnswered()) {
						if (catData.getAudit().getAuditType().isScoreable()) {
							if (answer.isScoreApplies()) {
								score += answer.getScoreValue();
								scoreWeight += question.getScoreWeight();
							}
						}

						answeredCount++;
						if (isRequired)
							requiredAnsweredCount++;
					}

					if (answer.getQuestion().isHasRequirement()) {
						if (answer.isOK())
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
		// categoryDataDAO.save(catData);
	}

	private Collection<Integer> collectFunctionWatcherQuestionIdsFromAuditCatData(AuditCatData catData) {
		Collection<Integer> functionWatcherQuestionIds = new ArrayList<Integer>();
		Date validDate = catData.getAudit().getValidDate();

		for (AuditQuestion question : catData.getCategory().getQuestions()) {
			if (question.isValidQuestion(validDate)) {
				for (AuditQuestionFunction aqf : question.getFunctions())
					for (AuditQuestionFunctionWatcher aqfw : aqf.getWatchers())
						if (aqfw.getQuestion().isValidQuestion(validDate))
							functionWatcherQuestionIds.add(aqfw.getQuestion().getId());
			}
		}

		return functionWatcherQuestionIds;
	}

	private Set<Integer> collectQuestionIdsFromAuditCatData(AuditCatData catData) {
		Set<Integer> questionIDs = new HashSet<Integer>();

		for (AuditQuestion question : catData.getCategory().getQuestions()) {
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

	private Set<Integer> collectChainOfRequiredQuestionIds(AuditQuestion question) {
		Set<Integer> questionIDs = new HashSet<Integer>();
		HashSet<Integer> requiredQuestionIdsSeen = new HashSet<Integer>();
		AuditQuestion q = question.getRequiredQuestion();
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

	private Set<Integer> collectChainOfVisibleQuestionIds(AuditQuestion question) {
		Set<Integer> questionIDs = new HashSet<Integer>();
		HashSet<Integer> visibleQuestionIdsSeen = new HashSet<Integer>();
		AuditQuestion q = question.getVisibleQuestion();
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

	private int addVerifiedCount(AuditCatData catData, int requiredCount, int verifiedCount, AuditData answer) {
		AuditQuestion question = answer.getQuestion();
		// Anything that requires verification, should be
		// listed as Required.
		// If we don't then it's possible that the verified
		// count will be higher than the required total,
		// resulting in a > 100% verified
		if (answer.isVerified())
			verifiedCount++;
		// This is used for manual/implementation audits
		// with questions with no requirements, so we need
		// to increment the count so we can close it.
		else if (catData.getAudit().getAuditType().getWorkFlow().isHasRequirements()) {
			verifiedCount++;
		} else if (question.getId() == 2447 || question.getId() == 2448 || question.getId() == 10217 || question.getId() == 15353 || question.getId() == 15354) {
			verifiedCount++;
		} else if (catData.getAudit().getAuditType().isPqf()) {
			boolean needsVerification = false;
			for (AuditData auditData : getVerifiedPqfData(catData.getAudit().getId())) {
				if (auditData.getQuestion().equals(answer.getQuestion())) {
					needsVerification = true;
					break;
				}
			}
			if (!needsVerification)
				verifiedCount++;
		} else if (catData.getAudit().getAuditType().getClassType().isPolicy()) {
			verifiedCount = requiredCount;
			// If the questions are explicited ignored from
			// verification but still required then we
			// should increase the verifiedCount so we can
			// close it
		} else if (!catData.getAudit().getAuditType().getWorkFlow().isHasSubmittedStep()) {
			// For audits without the submitted step we
			// don't have to verify the questions
			verifiedCount++;
		}
		int categoryId = catData.getCategory().getId();

		// COHS, OSHA Additional Logs and MSHA are not verified
		if (categoryId == OshaAudit.CAT_ID_COHS || categoryId == OshaAudit.CAT_ID_OSHA_ADDITIONAL
				|| categoryId == OshaAudit.CAT_ID_MSHA) {
			verifiedCount++;
		}
		return verifiedCount;
	}

	public void percentCalculateComplete(ContractorAudit conAudit) {
		percentCalculateComplete(conAudit, false);
	}

	/**
	 * For each CAO, roll up all the category complete stats to calculate the percent complete for the cao
	 *
	 * @param conAudit
	 * @param recalcCats
	 */
	public void percentCalculateComplete(ContractorAudit conAudit, boolean recalcCats) {
		if (recalcCats)
			recalcAllAuditCatDatas(conAudit);

		auditCategoryRuleCache.initialize(auditDecisionTableDAO);
		AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, conAudit
				.getContractorAccount());

		Set<AuditCategory> auditCategories = builder.calculate(conAudit);

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			int required = 0;
			int answered = 0;
			int verified = 0;

			float scoreWeight = 0;
			float score = 0;

			for (AuditCatData data : conAudit.getCategories()) {
				boolean applies = false;
				if (data.isOverride())
					applies = data.isApplies();
				else {
					if (data.isApplies()) {
						if (conAudit.getAuditType().isDesktop() && cao.getStatus().after(AuditStatus.Incomplete))
							applies = true;
						else if (conAudit.getAuditType().getId() == AuditType.IMPORT_PQF)
							// Import PQF and Welcome Call don't have any operators, so just always assume the
							// categories apply
							applies = true;
						else if (conAudit.getAuditType().getId() == AuditType.WELCOME)
							applies = true;
						else
							applies = builder.isCategoryApplicable(data.getCategory(), cao);
					}
				}

				if (applies) {
					required += data.getNumRequired();
					answered += data.getRequiredCompleted();
					verified += data.getNumVerified();

					if (conAudit.getAuditType().getScoreType() == ScoreType.Percent ||
							conAudit.getAuditType().getScoreType() == ScoreType.Actual) {
						if (data.getScorePossible() > 0) {
							score += data.getScore();
							scoreWeight += data.getScorePossible();
						}
					}
				}
			}

			if (scoreWeight > 0) {
				if (conAudit.getAuditType().getScoreType() == ScoreType.Percent)
					conAudit.setScore((int) Math.min(Math.round(score), 100L));
				else if (conAudit.getAuditType().getScoreType() == ScoreType.Actual)
					conAudit.setScore((int) Math.round(score));
			}

			int percentComplete = 0;
			int percentVerified = 0;
			if (required > 0) {
				percentComplete = (int) Math.floor(100 * answered / required);
				if (percentComplete >= 100) {
					percentComplete = 100;
				}

				percentVerified = (int) Math.floor(100 * verified / required);
				if (percentVerified >= 100) {
					percentVerified = 100;
				}
			} else {
				percentComplete = 100;
				percentVerified = 100;
			}

			cao.setPercentComplete(percentComplete);
			cao.setPercentVerified(percentVerified);

			ContractorAuditOperator caoWithStatus = null;
			if (cao.getStatus().isPending()) {
				if (conAudit.getAuditType().isPqf() && percentComplete == 100) {
					if (percentVerified == 100)
						caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Complete);
					if (caoWithStatus == null)
						caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Submitted);
					if (caoWithStatus == null)
						caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Resubmitted);
				} else if (conAudit.getAuditType().isDesktop()) {
					caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Complete);
					if (caoWithStatus == null)
						caoWithStatus = findCaoWithStatus(conAudit, AuditStatus.Submitted);
				}
			}

			if (caoWithStatus != null) {
				ContractorAuditOperatorWorkflow caoW = new ContractorAuditOperatorWorkflow();
				caoW.setCao(cao);
				// TODO: I18N
				caoW.setNotes("Advancing status because 100 percent complete.");
				caoW.setPreviousStatus(cao.getStatus());
				caoW.setAuditColumns(new User(User.SYSTEM));
				caoW.setStatus(caoWithStatus.getStatus());
				caoDAO.save(caoW);

				cao.changeStatus(caoWithStatus.getStatus(), null);
				cao.setStatusChangedDate(caoWithStatus.getStatusChangedDate());
			}
		}

		if (conAudit.getAuditType().getScoreType() == ScoreType.Weighted) {
			calculateWeightedScore(conAudit);
		}
        if (conAudit.getAuditType().getScoreType() == ScoreType.Aggregate) {
            calculateStraightAggregate(conAudit);
        }
	}

    private void calculateStraightAggregate(ContractorAudit scoredAudit){
        float runningTotal = 0;
        for (AuditData auditData: scoredAudit.getData()) {
            runningTotal += auditData.getStraightScoreValue();
        }
        int score = Math.round(runningTotal);
        // TODO Technical Debt: Update the database and remove this code.
        scoredAudit.setScore(score > 127 ? 127 : score);
    }

	private void calculateWeightedScore(ContractorAudit ca) {
		float cumulativeScore = 0f;

		/*
		 * Gather the category data to use in the recursive calculation.
		 */
		Map<AuditCategory, AuditCatData> catDatas = new HashMap<AuditCategory, AuditCatData>();
		for (AuditCatData data : ca.getCategories()) {
			catDatas.put(data.getCategory(), data);
		}

		/*
		 * Iterate over the top level categories and calculate the score based on their weights
		 */
		for (AuditCategory category : ca.getAuditType().getTopCategories()) {
			subScorePossible = 0f;
			float score = calculateWeightedScore(category, catDatas);
			if (subScorePossible > 0)
				cumulativeScore += category.getScoreWeight() * score;
			else
				cumulativeScore += category.getScoreWeight();
		}

		ca.setScore(Math.min(Math.round(cumulativeScore), 100));
	}

	private float calculateWeightedScore(AuditCategory category, Map<AuditCategory, AuditCatData> catDatas) {
		float subScore = 0f;
		float scorePossible = 0f;
		/*
		 * We either collect the questions (i.e. catData.score and catData.scorePossible) or the subcategories. We
		 * cannot do both currently, as it is comparing apples and oranges.
		 */
		if (category.getSubCategories().size() > 0) {
			for (AuditCategory subCategory : category.getSubCategories()) {
				float runningScore = calculateWeightedScore(subCategory, catDatas);
				/*
				 * Handle the N/A Categories. Categories without a possible score should not affect the total weight.
				 */
				if (catDatas.get(subCategory).getScorePossible() > 0) {
					subScore += runningScore;
					scorePossible += subCategory.getScoreWeight();
				}
			}
			/*
			 * Prevent a divide by 0. This will likely never happen
			 */
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

	private ContractorAuditOperator findCaoWithStatus(ContractorAudit conAudit, AuditStatus auditStatus) {
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			if (cao.getStatus().equals(auditStatus))
				return cao;
		}
		return null;
	}

	/**
	 * Recalculate all categories including the OSHA ones too
	 * 
	 * @param conAudit
	 */
	public void recalcAllAuditCatDatas(ContractorAudit conAudit) {
		for (AuditCatData data : conAudit.getCategories()) {
				updatePercentageCompleted(data);
		}
	}

	public List<AuditData> getVerifiedPqfData(int auditID) {
		return auditDataDAO.findCustomPQFVerifications(auditID);
	}

}
