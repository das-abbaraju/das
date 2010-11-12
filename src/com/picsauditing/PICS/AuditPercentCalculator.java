package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
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

public class AuditPercentCalculator {

	private AuditCategoryRuleCache auditCategoryRuleCache;

	public AuditPercentCalculator(AuditCategoryRuleCache auditCategoryRuleCache) {
		this.auditCategoryRuleCache = auditCategoryRuleCache;
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
			if (question.getDependentRequired() != null)
				for (AuditQuestion dr : question.getDependentRequired())
					questionIDs.add(dr.getId());
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
			if (question.isCurrent()
					&& validDate.after(question.getEffectiveDate())
					&& validDate.before(question.getExpirationDate())) {
				boolean isRequired = question.isRequired();

				AuditData answer = answers.get(question.getId());
				// Getting all the dependsRequiredQuestions
				if (question.getRequiredQuestion() != null
						&& question.getRequiredAnswer() != null) {
					if (question.getRequiredAnswer().equals("NULL")) {
						AuditData otherAnswer = answers.get(question
								.getRequiredQuestion().getId());
						if (otherAnswer == null)
							isRequired = true;
					} else if (question.getRequiredAnswer().equals("NOTNULL")) {
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
				// Getting all the dependsVisible Questions
				if (question.getVisibleQuestion() != null
						&& question.getVisibleAnswer() != null) {
					if (question.getVisibleAnswer().equals("NULL")) {
						AuditData otherAnswer = answers.get(question
								.getVisibleQuestion().getId());
						if (otherAnswer == null)
							isRequired = true;
					} else if (question.getVisibleAnswer().equals("NOTNULL")) {
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

						if (catData.getAudit().getAuditType().isScoreable()) {
							if ("Radio".equals(question.getQuestionType())) {
								for (AuditQuestionOption option : question
										.getOptions()) {
									if (option.getOptionName().equals(
											answer.getAnswer())) {
										score += option.getScore();
										break;
									}
								}
								scoreCount += question.getScoreWeight();
							} else if ("Yes/No".equals(question
									.getQuestionType())
									|| "Yes/No/NA".equals(question
											.getQuestionType())) {
								if (answer.getAnswer().equals("Yes"))
									score += question.getScoreWeight();
								else if (answer.getAnswer().equals("NA"))
									score += question.getScoreWeight() / 2;
								else
									score += 0;

								scoreCount += question.getScoreWeight();
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
						if (answer.isVerified()) {
							verifiedCount++;
						} else if (isRequired
								&& catData.getAudit().getAuditType()
										.getWorkFlow().isHasSubmittedStep()) {
							verifiedCount++;
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
	}

	public void percentCalculateComplete(ContractorAudit conAudit) {
		percentCalculateComplete(conAudit, false);
	}

	public void percentCalculateComplete(ContractorAudit conAudit,
			boolean recalcCats) {
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
					if (data.isApplies()
							&& detail.categories.contains(data.getCategory())) {
						AuditCategoryRule auditCatRule = AuditBuilder
								.getApplicable(detail.rules,
										data.getCategory(), cao.getOperator());
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
				conAudit.setScore((int) ((score / scoreCount) * 100));
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
	 * Use the audit rule cache to query a list of applicable rules and the figure
	 * out which rules apply to which operators and categories
	 * 
	 * @param conAudit
	 * @return
	 */
	private AuditCategoriesDetail getAuditCategoryDetail(
			ContractorAudit conAudit) {
		List<AuditCategoryRule> applicableCategoryRules = auditCategoryRuleCache
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

		if (osha.getType().equals(OshaType.MSHA)
				|| osha.getType().equals(OshaType.COHS)) {
			numRequired = 1;
			if (osha.getManHours() > 0)
				count++;
			numVerified = count;
		}

		catData.setRequiredCompleted(count);
		catData.setNumRequired(numRequired);
		catData.setNumVerified(numVerified);
	}
}
