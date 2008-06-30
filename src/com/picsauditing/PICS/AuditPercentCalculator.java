package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditPercentCalculator {
	private AuditDataDAO auditDataDao;
	private AuditCategoryDataDAO catDataDao;

	public AuditPercentCalculator(AuditDataDAO auditDataDao,
			AuditCategoryDataDAO catDataDao) {
		this.auditDataDao = auditDataDao;
		this.catDataDao = catDataDao;
	}

	public void updatePercentageCompleted(AuditCatData catData) {
		if (catData == null)
			return;

		if ("Yes".equals(catData.isAppliesB())) {
			return;
		}

		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;

		// Get a list of questions/answers for this category
		List<Integer> questionIDs = new ArrayList<Integer>();

		for (AuditSubCategory subCategory : catData.getCategory()
				.getSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
				questionIDs.add(question.getQuestionID());
				if ("Depends".equals(question.getDependsOnAnswer())
						&& question.getDependsOnQuestion() != null) {
					int dependsOnQID = question.getDependsOnQuestion()
							.getQuestionID();
					questionIDs.add(dependsOnQID);
				}
			}
		}
		// Get a map of all answers in this audit
		Map<Integer, AuditData> answers = auditDataDao.findAnswers(catData
				.getAudit().getId(), questionIDs);

		// Get a list of questions/answers for this category
		for (AuditSubCategory subCategory : catData.getCategory()
				.getSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
				if (question.getDependsOnQuestion() != null)
					question.getDependsOnQuestion().setAnswer(
							answers.get(question.getDependsOnQuestion()
									.getQuestionID()));

				boolean isRequired = question.isRequired();

				// if ("Depends".equals(question.getIsRequired())) {
				// int dependsOnQID = question.getDependsOnQuestion()
				// .getQuestionID();
				// String dependsOnAnswer = question.getDependsOnAnswer();
				// if (answers.get(dependsOnQID) != null
				// &&
				// dependsOnAnswer.equals(answers.get(dependsOnQID).getAnswer()))
				// isRequired = true;
				// if (answers.get(dependsOnQID) != null &&
				// dependsOnAnswer.equals("Yes*")) {
				// isRequired = true;
				// }
				// }
				if (isRequired)
					requiredCount++;

				AuditData answer = answers.get(question.getQuestionID());
				if (answer != null) {
					String answerToQuestion = answer.getAnswer();
					if (!"".equals(answerToQuestion)
							&& !com.picsauditing.PICS.DateBean.NULL_DATE_DB
									.equals(answerToQuestion)) {
						answeredCount++;
						if (isRequired)
							requiredAnsweredCount++;
					}
					if ("Yes".equals(answerToQuestion)
							|| "NA".equals(answerToQuestion)) {
						// This is a valid Desktop or Office audit answer so,
						// it's "Verified"
						verifiedCount++;
					}
				}
			}
		}
		catData.setNumAnswered(answeredCount);
		catData.setNumRequired(requiredCount);
		catData.setRequiredCompleted(requiredAnsweredCount);

		if (requiredCount > 0) {
			int percentCompleted = (int) Math
					.floor((100 * requiredAnsweredCount) / requiredCount);
			int percentVerified = (int) Math.floor((100 * verifiedCount)
					/ requiredCount);
			catData.setPercentCompleted(percentCompleted);
			catData.setPercentVerified(percentVerified);
		} else {
			catData.setPercentCompleted(100);
			catData.setPercentVerified(100);
		}
		catDataDao.save(catData);
	}

	public void percentCalculateComplete(ContractorAudit conAudit) {
		int required = 0;
		int answered = 0;
		int verified = 0;
		for (AuditCatData data : conAudit.getCategories()) {
			if (!conAudit.getAuditType().isDynamicCategories()
					|| data.isAppliesB()) {
				// The category applies or the audit type doesn't have dynamic
				// categories
				required += data.getNumRequired();
				answered += data.getRequiredCompleted();
				verified += (int) Math.round(data.getNumRequired()
						* data.getPercentVerified() / 100);
			}
		}
		int percentComplete = 0;
		int percentVerified = 0;
		if (required > 0) {
			percentComplete = (int) Math.floor(100 * answered / required);
			if (percentComplete > 100)
				percentComplete = 100;

			percentVerified = (int) Math.floor(100 * verified / required);
			if (percentVerified > 100)
				percentVerified = 100;
		}
		conAudit.setPercentComplete(percentComplete);
		if (conAudit.getAuditType().isHasRequirements()
				&& !conAudit.getAuditType().isPqf())
			conAudit.setPercentVerified(percentVerified);
	}
}
