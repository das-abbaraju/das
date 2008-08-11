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
import com.picsauditing.jpa.entities.OshaLog;
import com.picsauditing.jpa.entities.OshaLogYear;

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
		
		
		int questID = 0;

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
				
				if (isRequired 
						&& question.getEffectiveDate().before(catData.getAudit().getCreatedDate()) 
						&& question.getExpirationDate().after(catData.getAudit().getCreatedDate())) {
					questID = question.getQuestionID();
					requiredCount++;
				}

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
			int percentCompleted = (int) Math.floor((100 * requiredAnsweredCount) / requiredCount);
			if (percentCompleted >= 100)
				percentCompleted = 100;
			int percentVerified = (int) Math.floor((100 * verifiedCount) / requiredCount);
			if (percentVerified >= 100)
				percentVerified = 100;
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
			if (!conAudit.getAuditType().isDynamicCategories() || data.isAppliesB()) {
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
			if (percentComplete >= 100)
				percentComplete = 100;

			percentVerified = (int) Math.floor(100 * verified / required);
			if (percentVerified >= 100)
				percentVerified = 100;
		}
		conAudit.setPercentComplete(percentComplete);
		if (conAudit.getAuditType().isHasRequirements()	&& !conAudit.getAuditType().isPqf())
			conAudit.setPercentVerified(percentVerified);
	}
	
	public void percentOshaComplete(OshaLog osha, AuditCatData catData) {
		int count = 0;
		count += getOshaYearValidCount(osha.getYear1());
		count += getOshaYearValidCount(osha.getYear2());
		count += getOshaYearValidCount(osha.getYear3());
		
		int percentComplete = Math.round(count * 100 / 6);
		int factor = 5; // Let's make the osha section "weigh" a bit more in the overall audit % complete
		catData.setRequiredCompleted(count * factor);
		catData.setNumRequired(6 * factor);
		catData.setPercentCompleted(percentComplete);
		catDataDao.save(catData);
	}
	
	private int getOshaYearValidCount(OshaLogYear year) {
		if (!year.isApplicable())
			return 2;
		int count = 0;
		if (year.getManHours() > 0)
			count++;
		if (year.isUploaded())
			count++;
		return count;
	}
}
