package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;

public class AuditPercentCalculator {

	private AuditDataDAO auditDataDao;
	private AuditCategoryDataDAO catDataDao;
	private AuditDecisionTableDAO auditRulesDAO;

	public AuditPercentCalculator(AuditDataDAO auditDataDAO,
			AuditCategoryDataDAO catDataDAO, AuditDecisionTableDAO auditRulesDAO) {
		this.auditDataDao = auditDataDAO;
		this.catDataDao = catDataDAO;
		this.auditRulesDAO = auditRulesDAO;
	}

	public void updatePercentageCompleted(AuditCatData catData) {
		if (catData == null)
			return;

		if (catData.isApplies()) {
			return;
		}

		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;
		int scoreCount = 0;
		int score = 0;

		// Get a list of questions/answers for this category
		List<Integer> questionIDs = new ArrayList<Integer>();
		for (AuditQuestion question : catData.getCategory().getQuestions()) {
			questionIDs.add(question.getId());
			if (question.getDependentRequired() != null)
				questionIDs.add(question.getId());
			if (question.getVisibleQuestion() != null)
				questionIDs.add(question.getId());
		}
		// Get a map of all answers in this audit
		AnswerMap answers = auditDataDao.findAnswers(
				catData.getAudit().getId(), questionIDs);
		// System.out.println(answers);

		if (answers != null) {
			@SuppressWarnings("serial")
			Map<String, Integer> scoreMap = new HashMap<String, Integer>() {

				{
					put("Red", 0);
					put("Yellow", 1);
					put("Green", 2);
				}
			};

			// Get a list of questions/answers for this category
			Date validDate = catData.getAudit().getValidDate();
			for (AuditQuestion question : catData.getCategory().getQuestions()) {
				if (question.isCurrent() && validDate.after(question.getEffectiveDate())
						&& validDate.before(question.getExpirationDate())) {
					boolean isRequired = false;

					AuditData answer = answers.get(question.getId());
					isRequired = true == question.isRequired();
					// Getting all the dependsRequiredQuestions
					if (question.getRequiredQuestion() != null && question.getRequiredAnswer() != null) {
						if (question.getRequiredAnswer().equals("NULL")) {
							AuditData otherAnswer = answers.get(question
									.getRequiredQuestion().getId());
							if (otherAnswer == null)
								isRequired = true;
						} else if (question.getRequiredAnswer().equals(
								"NOTNULL")) {
							AuditData otherAnswer = answers.get(question
									.getRequiredQuestion().getId());
							if (otherAnswer != null)
								isRequired = true;
						} else {
							// This question is dependent on another
							// question's answer
							// Use the parentAnswer, so we get answers in
							// the same tuple as this one
							AuditData otherAnswer = answers.get(question
									.getRequiredQuestion().getId());
							if (otherAnswer != null
									&& question.getRequiredAnswer().equals(
											otherAnswer.getAnswer()))
								isRequired = true;
						}
					}
					// Getting all the dependsVisible  Questions
					if (question.getVisibleQuestion() != null && question.getVisibleAnswer() != null) {
						if (question.getVisibleAnswer().equals("NULL")) {
							AuditData otherAnswer = answers.get(question
									.getVisibleQuestion().getId());
							if (otherAnswer == null)
								isRequired = true;
						} else if (question.getVisibleAnswer().equals(
								"NOTNULL")) {
							AuditData otherAnswer = answers.get(question
									.getVisibleQuestion().getId());
							if (otherAnswer != null)
								isRequired = true;
						} else {
							AuditData otherAnswer = answers.get(question
									.getVisibleQuestion().getId());
							if (otherAnswer != null
									&& question.getVisibleAnswer().equals(
											otherAnswer.getAnswer()))
								isRequired = true;
						}
					}

					if (isRequired)
						requiredCount++;

					if (answer != null) {
						if (answer.isAnswered()) {

							if ("Radio".equals(question.getQuestionType())) {
								Integer tempScore = scoreMap.get(answer
										.getAnswer());
								score += tempScore != null ? tempScore : -1000;
							}

							answeredCount++;
							if (isRequired)
								requiredAnsweredCount++;
						}

						if (answer.getQuestion().isHasRequirement()) {
							if (answer.isOK())
								verifiedCount++;
						} else {
							if (answer.isVerified()) {
								verifiedCount++;
							} else if (isRequired
									&& catData.getAudit().getAuditType()
											.getWorkFlow().isHasSubmittedStep()) {
								verifiedCount++;
							}
						}
					}

					if ("Radio".equals(question.getQuestionType())) {
						scoreCount++;
					}
				}
			}

			catData.setNumAnswered(answeredCount);
			catData.setNumRequired(requiredCount);
			catData.setRequiredCompleted(requiredAnsweredCount);

			if (requiredCount > 0) {
				int percentCompleted = (int) Math
						.floor((100 * requiredAnsweredCount) / requiredCount);
				if (percentCompleted >= 100)
					percentCompleted = 100;
				if (catData.getAudit().getAuditType().isAnnualAddendum()
						&& catData.getCategory().getId() == AuditCategory.GENERAL_INFORMATION
						&& catData.getNumRequired() > 2) {
					requiredCount = requiredCount - 2;
				}
				int percentVerified = (int) Math.floor((100 * verifiedCount)
						/ requiredCount);
				if (percentVerified >= 100)
					percentVerified = 100;
				catData.setPercentCompleted(percentCompleted);
				catData.setPercentVerified(percentVerified);
			} else {
				catData.setPercentCompleted(100);
				catData.setPercentVerified(100);
			}

			if (scoreCount > 0) {
				float scoreAverage = (float) score / (float) scoreCount;
				catData.setScore(scoreAverage);
				catData.setScoreCount(scoreCount);
			}

			catDataDao.save(catData);
		}
	}

	public void percentCalculateComplete(ContractorAudit conAudit) {
		percentCalculateComplete(conAudit, false);
	}

	public void percentCalculateComplete(ContractorAudit conAudit,
			boolean recalcCats) {
		if (recalcCats) {
			recalcAllAuditCatDatas(conAudit);
		}

		AuditCategoriesDetail detail = getAuditCategoryDetail(conAudit);

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			int required = 0;
			int answered = 0;
			int verified = 0;

			int scoreCount = 0;
			float runningScore = 0;

			for (AuditCatData data : conAudit.getCategories()) {
				boolean applies = false;
				if (data.isOverride())
					applies = data.isApplies();
				else
					applies = detail.categories.contains(data.getCategory());

				if (applies) {
					required += data.getNumRequired();
					answered += data.getRequiredCompleted();
					verified += (int) Math.round(data.getNumRequired()
							* data.getPercentVerified() / 100);

					if (data.getScoreCount() > 0) {
						scoreCount += data.getScoreCount();
						runningScore += (data.getScore() * data.getScoreCount());
					}
				}
			}

			// Eventually we'll move the score over to the cao
			if (scoreCount > 0) {
				conAudit.setScore(runningScore / (float) scoreCount);
			} else {
				conAudit.setScore(-1);
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
		}
	}

	/**
	 * Use the audit rule DAO to query a list of applicable rules and the figure
	 * out which rules apply to which operators and categories
	 * 
	 * @param conAudit
	 * @return
	 */
	private AuditCategoriesDetail getAuditCategoryDetail(
			ContractorAudit conAudit) {
		List<AuditCategoryRule> applicableCategoryRules = auditRulesDAO
				.getApplicableCategoryRules(conAudit.getContractorAccount(),
						conAudit.getAuditType());

		AuditBuilder builder = new AuditBuilder();

		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			operators.add(cao.getOperator());
		}
		return builder.getDetail(conAudit.getAuditType(),
				applicableCategoryRules, operators);
	}

	public void recalcAllAuditCatDatas(ContractorAudit conAudit) {
		for (AuditCatData data : conAudit.getCategories()) {

			if (!conAudit.getAuditType().isAnnualAddendum()) {
				updatePercentageCompleted(data);
			} else {
				for (OshaAudit osha : conAudit.getOshas()) {
					if (osha.isCorporate()) {
						percentOshaComplete(osha, data);
					}
				}
				updatePercentageCompleted(data);
			}
		}
	}

	public void percentOshaComplete(OshaAudit osha, AuditCatData catData) {
		int count = 0;
		int percentComplete = 0;
		int numRequired = 2;

		if (osha.getType().equals(OshaType.OSHA)) {
			if (osha.getManHours() > 0)
				count++;
			if (osha.isFileUploaded())
				count++;
			percentComplete = Math.round(count * 100 / 2);

			if (osha.isVerified()) {
				catData.setPercentVerified(100);
			} else {
				catData.setPercentVerified(0);
			}

		}
		if (osha.getType().equals(OshaType.MSHA)
				|| osha.getType().equals(OshaType.COHS)) {
			numRequired = 1;
			if (osha.getManHours() > 0)
				count++;
			percentComplete = Math.round(count * 100);
			if (percentComplete == 100)
				catData.setPercentVerified(100);
		}

		catData.setRequiredCompleted(count);
		catData.setNumRequired(numRequired);
		catData.setPercentCompleted(percentComplete);

		catDataDao.save(catData);
	}
}
