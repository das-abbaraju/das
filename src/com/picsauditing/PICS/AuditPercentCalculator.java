package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionOption;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

public class AuditPercentCalculator {

	private AuditCategoryRuleCache auditCategoryRuleCache;
	private AuditCategoryDataDAO categoryDataDAO;
	private ContractorAuditOperatorDAO caoDao;
	private AuditDataDAO auditDataDAO;
	private List<AuditData> verifiedPqfData = null;

	public AuditPercentCalculator(AuditCategoryRuleCache auditCategoryRuleCache, AuditCategoryDataDAO categoryDataDAO,
			ContractorAuditOperatorDAO caoDao, AuditDataDAO auditDataDAO) {
		this.auditCategoryRuleCache = auditCategoryRuleCache;
		this.categoryDataDAO = categoryDataDAO;
		this.caoDao = caoDao;
		this.auditDataDAO = auditDataDAO;
	}

	public void updatePercentageCompleted(AuditCatData catData) {
		if (catData == null)
			return;

		if (!catData.isApplies())
			return;

		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;
		int scoreCount = 0;
		float score = 0;

		// Get a list of questions/answers for this category
		Set<Integer> questionIDs = new HashSet<Integer>();

		for (AuditQuestion question : catData.getCategory().getQuestions()) {
			questionIDs.add(question.getId());
			if (question.getRequiredQuestion() != null)
				questionIDs.add(question.getRequiredQuestion().getId());
			if (question.getVisibleQuestion() != null)
				questionIDs.add(question.getVisibleQuestion().getId());
		}

		// Get a map of all answers in this audit
		List<AuditData> requiredAnswers = new ArrayList<AuditData>();
		for (AuditData answer : catData.getAudit().getData())
			if (questionIDs.contains(answer.getQuestion().getId()))
				requiredAnswers.add(answer);
		AnswerMap answers = new AnswerMap(requiredAnswers);
		// Get a list of questions/answers for this category
		Date validDate = catData.getAudit().getValidDate();
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
				// Getting all the dependsVisible Questions
				if (question.getVisibleQuestion() != null && question.getVisibleAnswer() != null) {
					if (question.getVisibleAnswer().equals("NULL")) {
						AuditData otherAnswer = answers.get(question.getVisibleQuestion().getId());
						if (otherAnswer == null || Strings.isEmpty(otherAnswer.getAnswer()))
							isRequired = true;
					} else if (question.getVisibleAnswer().equals("NOTNULL")) {
						AuditData otherAnswer = answers.get(question.getVisibleQuestion().getId());
						if (otherAnswer != null && !Strings.isEmpty(otherAnswer.getAnswer()))
							isRequired = true;
					} else {
						AuditData otherAnswer = answers.get(question.getVisibleQuestion().getId());
						if (otherAnswer != null && question.getVisibleAnswer().equals(otherAnswer.getAnswer()))
							isRequired = true;
					}
				}

				if (isRequired)
					requiredCount++;

				// Always include the score count. Blank audits will receive a
				// score of 0
				if (catData.getAudit().getAuditType().isScoreable())
					scoreCount += question.getScoreWeight();

				if (answer != null) {
					if (answer.isAnswered()) {
						if (catData.getAudit().getAuditType().isScoreable()) {
							int answerValue = 0;
							float scale = 1.0f;

							if ("Radio".equals(question.getQuestionType())) {
								for (AuditQuestionOption option : question.getOptions()) {
									scale = Math.max(scale, option.getScore());
									if (answer.getAnswer().equals(option.getOptionName())) {
										answerValue = option.getScore();
									}
								}
							} else if ("Yes/No".equals(question.getQuestionType())) {
								scale = 1.0f;
								if (answer.getAnswer().equals("Yes"))
									answerValue = 1;
							} else if ("Yes/No/NA".equals(question.getQuestionType())) {
								scale = 2.0f;
								if (answer.getAnswer().equals("Yes"))
									answerValue = 2;
								else if (answer.getAnswer().equals("NA"))
									answerValue = 1;
							} else if ("Rating 1-5".equals(question.getQuestionType())) {
								scale = 4.0f;
								try {
									answerValue = Integer.parseInt(answer.getAnswer()) - 1;
								} catch (NumberFormatException justIgnoreIt) {
								}
							}

							score += Math.round((question.getScoreWeight() / scale) * answerValue);

						}

						if ((catData.getAudit().getAuditType().isDesktop() && !answer.isRequirementOpen())
								|| !catData.getAudit().getAuditType().isDesktop()) {
							answeredCount++;
							if (isRequired)
								requiredAnsweredCount++;
						}
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
		catData.setScoreCount(scoreCount);
		// categoryDataDAO.save(catData);
	}

	public void percentCalculateComplete(ContractorAudit conAudit) {
		percentCalculateComplete(conAudit, false);
	}

	public void percentCalculateComplete(ContractorAudit conAudit, boolean recalcCats) {
		if (recalcCats)
			recalcAllAuditCatDatas(conAudit);

		AuditCategoriesDetail detail = getAuditCategoryDetail(conAudit);

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			int required = 0;
			int answered = 0;
			int verified = 0;

			int scoreCount = 0;
			float score = 0;

			for (AuditCatData data : conAudit.getCategories()) {
				boolean applies = false;
				if (data.isOverride())
					applies = data.isApplies();
				else {
					if (data.isApplies() && detail.categories.contains(data.getCategory())) {
						AuditCategoryRule auditCatRule = AuditBuilder.getApplicable(detail.rules, data.getCategory(),
								cao.getOperator());
						if (auditCatRule != null)
							applies = auditCatRule.isInclude();
					}
				}
				if (applies) {
					required += data.getNumRequired();
					answered += data.getRequiredCompleted();
					verified += data.getNumVerified();

					if (data.getScoreCount() > 0) {
						score += data.getScore();
						scoreCount += data.getScoreCount();
					}
				}
			}

			if (scoreCount > 0) {
				if (conAudit.getAuditType().isScoreExtrapolated())
					conAudit.setScore((int) ((score / scoreCount) * 100));
				else
					conAudit.setScore((int) score);
			}

			int percentComplete = 0;
			int percentVerified = 0;
			if (required > 0) {
				percentComplete = (int) Math.floor(100 * answered / required);
				if (percentComplete >= 100)
					percentComplete = 100;

				percentVerified = (int) Math.floor(100 * verified / required);
				if (percentVerified >= 100)
					percentVerified = 100;
			}

			cao.setPercentComplete(percentComplete);
			cao.setPercentVerified(percentVerified);
			// caoDao.save(cao);
		}
	}

	/**
	 * Use the audit rule cache to query a list of applicable rules and the
	 * figure out which rules apply to which operators and categories
	 * 
	 * @param conAudit
	 * @return
	 */
	private AuditCategoriesDetail getAuditCategoryDetail(ContractorAudit conAudit) {
		List<AuditCategoryRule> applicableCategoryRules = auditCategoryRuleCache.getApplicableCategoryRules(
				conAudit.getContractorAccount(), conAudit.getAuditType());

		AuditBuilder builder = new AuditBuilder();

		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			operators.add(cao.getOperator());
		}
		return builder.getDetail(conAudit.getAuditType(), applicableCategoryRules, operators);
	}

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
		int requiredCompleted = 0;
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
		if (verifiedPqfData == null)
			verifiedPqfData = auditDataDAO.findCustomPQFVerifications(auditID);
		return verifiedPqfData;
	}

}