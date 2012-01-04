package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionFunction;
import com.picsauditing.jpa.entities.AuditQuestionFunctionWatcher;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.QuestionFunctionType;
import com.picsauditing.jpa.entities.ScoreType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

public class AuditPercentCalculator {
	@Autowired
	private AuditCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	private AuditCategoryDataDAO categoryDataDAO;
	@Autowired
	private AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;

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

		Date validDate = catData.getAudit().getValidDate();

		// Get a list of questions/answers for this category
		Set<Integer> questionIDs = new HashSet<Integer>();
		Collection<Integer> functionWatcherQuestionIds = new ArrayList<Integer>();

		for (AuditQuestion question : catData.getCategory().getQuestions()) {
			questionIDs.add(question.getId());

			if (question.getRequiredQuestion() != null)
				questionIDs.add(question.getRequiredQuestion().getId());
			if (question.getVisibleQuestion() != null)
				questionIDs.add(question.getVisibleQuestion().getId());

			if (question.isValidQuestion(validDate)) {
				for (AuditQuestionFunction aqf : question.getFunctions())
					for (AuditQuestionFunctionWatcher aqfw : aqf.getWatchers())
						if (aqfw.getQuestion().isValidQuestion(validDate))
							functionWatcherQuestionIds.add(aqfw.getQuestion().getId());
			}
		}

		AnswerMap currentWatcherAnswers = auditDataDAO.findCurrentAnswers(catData.getAudit().getContractorAccount()
				.getId(), functionWatcherQuestionIds);

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
							Object calculation = function.calculate(currentWatcherAnswers);
							if (calculation != null) {
								results = calculation.toString();
								break;
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
				boolean isRequired = question.isRequired();

				AuditData answer = answers.get(question.getId());
				// Getting all the dependsRequiredQuestions
				if (question.getRequiredQuestion() != null && question.getRequiredAnswer() != null) {
					if (question.getRequiredAnswer().equals("NULL")) {
						AuditData otherAnswer = answers.get(question.getRequiredQuestion().getId());
						if (otherAnswer == null || Strings.isEmpty(otherAnswer.getAnswer()))
							isRequired = true;
					} else if (question.getRequiredAnswer().equals("NOTNULL")) {
						AuditData otherAnswer = answers.get(question.getRequiredQuestion().getId());
						if (otherAnswer != null && !Strings.isEmpty(otherAnswer.getAnswer()))
							isRequired = true;
					} else {
						// This question is dependent on another
						// question's answer
						// Use the parentAnswer, so we get answers in
						// the same tuple as this one
						AuditData otherAnswer = answers.get(question.getRequiredQuestion().getId());
						if (otherAnswer != null && question.getRequiredAnswer().equals(otherAnswer.getAnswer()))
							isRequired = true;
					}
				}

				// make sure this dependent required question is visible
				if (isRequired) {
					if (catData.getAudit().getEffectiveDate() != null && catData.getAudit().getEffectiveDate().before(question.getEffectiveDate()))
						isRequired = false;
					else if (question.getVisibleQuestion() != null && question.getVisibleAnswer() != null) {
						AuditData otherAnswer = answers.get(question.getVisibleQuestion().getId());
						if (!question.isVisible(otherAnswer))
							isRequired = false;
					}
				}

				if (isRequired)
					requiredCount++;

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
							} else if (catData.getAudit().getAuditType().isPqf()) {
								boolean needsVerification = false;
								for (AuditData auditData : getVerifiedPqfData(catData.getAudit().getId())) {
									if (auditData.getQuestion().getCategory().equals(catData.getCategory())) {
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
							} else if (question.getId() == 2447 || question.getId() == 2448) {
								verifiedCount++;
							} else if (!catData.getAudit().getAuditType().getWorkFlow().isHasSubmittedStep()) {
								// For audits without the submitted step we
								// don't have to verify the questions
								verifiedCount++;
							}
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

					if (conAudit.getAuditType().getScoreType() == ScoreType.Percent) {
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
				if (percentVerified >= 100)
					percentVerified = 100;
			}

			cao.setPercentComplete(percentComplete);
			cao.setPercentVerified(percentVerified);

			ContractorAuditOperator caoWithStatus = null;
			if (cao.getStatus().isPending()) {
				if (conAudit.getAuditType().isPqf() && percentComplete == 100) {
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
				caoW.setNotes("Updating status to same as " + caoWithStatus.getOperator().getName());
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
	}

	private void calculateWeightedScore(ContractorAudit ca) {
		float subScore = 0f;

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
			subScore += category.getScoreWeight() * calculateWeightedScore(category, catDatas);
		}

		ca.setScore(Math.min(Math.round(subScore), 100));
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
			OshaType typeFromCategory = OshaTypeConverter.getTypeFromCategory(data.getCategory().getId());
			if (typeFromCategory != null) {
				for (OshaAudit osha : conAudit.getOshas()) {
					if (osha.isCorporate() && osha.getType().equals(typeFromCategory)) {
						percentOshaComplete(osha, data);
					}
				}
			} else {
				updatePercentageCompleted(data);
			}
		}
	}

	public void percentOshaComplete(OshaAudit osha, AuditCatData catData) {
		int count = 0;
		int numRequired = 2;
		int numVerified = 0;

		if (osha.getType().equals(OshaType.OSHA)) {
			if (osha.getManHours() > 0)
				count++;
			if (osha.isFileUploaded())
				count++;
			if (osha.isVerified()) {
				numVerified = 2;
			}
		}

		if (osha.getType().equals(OshaType.MSHA) || osha.getType().equals(OshaType.COHS)) {
			numRequired = 1;
			if (osha.getManHours() > 0)
				count++;
			numVerified = count;
		}

		catData.setRequiredCompleted(count);
		catData.setNumRequired(numRequired);
		catData.setNumVerified(numVerified);
		categoryDataDAO.save(catData);
	}

	public List<AuditData> getVerifiedPqfData(int auditID) {
		return auditDataDAO.findCustomPQFVerifications(auditID);
	}

}