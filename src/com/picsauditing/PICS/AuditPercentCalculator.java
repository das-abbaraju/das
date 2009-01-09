package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.util.AnswerMap;

public class AuditPercentCalculator {
	private AuditDataDAO auditDataDao;
	private AuditCategoryDataDAO catDataDao;

	public AuditPercentCalculator(AuditDataDAO auditDataDao, AuditCategoryDataDAO catDataDao) {
		this.auditDataDao = auditDataDao;
		this.catDataDao = catDataDao;
	}

	public void updatePercentageCompleted(AuditCatData catData) {
		if (catData == null)
			return;

		if ("Yes".equals(catData.isAppliesB())) {
			return;
		}

		catData.getCategory().setValidDate(catData.getAudit().getValidDate());

		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;

		// Get a list of questions/answers for this category
		List<Integer> questionIDs = new ArrayList<Integer>();
		for (AuditSubCategory subCategory : catData.getCategory().getValidSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
				questionIDs.add(question.getId());
				if ("Depends".equals(question.getIsRequired()) && question.getDependsOnQuestion() != null) {
					int dependsOnQID = question.getDependsOnQuestion().getId();
					questionIDs.add(dependsOnQID);
				}
			}
		}
		// Get a map of all answers in this audit
		AnswerMap answers = auditDataDao.findAnswers(catData.getAudit().getId(), questionIDs);
		System.out.println(answers);

		// Get a list of questions/answers for this category
		Date validDate = catData.getAudit().getValidDate();
		for (AuditSubCategory subCategory : catData.getCategory().getValidSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
				if (validDate.after(question.getEffectiveDate())
						&& validDate.before(question.getExpirationDate())) {
					boolean isRequired = false;
					
					if (question.isAllowMultipleAnswers()) {
						AuditData answer = answers.get(question.getId());
						// Only require the tuple if at least one minimum tuple is required
						isRequired = question.getMinimumTuples() > 0;
						if (isRequired)
							requiredCount++;
						
						if (answer != null) {
							if (answer.isAnswered()) {
								answeredCount++;
								if (isRequired)
									requiredAnsweredCount++;
							}
							if (answer.isVerified() || answer.isOK())
								verifiedCount++;
						}

					} else if (question.getParentQuestion() == null) {
						AuditData answer = answers.get(question.getId());
						// This question isn't part of a tuple
						isRequired = "Yes".equals(question.getIsRequired());
						if ("Depends".equals(question.getIsRequired()) && question.getDependsOnQuestion() != null && question.getDependsOnAnswer() != null) {
							// This question is dependent on another question's answer
							// Use the parentAnswer, so we get answers in the same tuple as this one
							AuditData otherAnswer = answers.get(question.getDependsOnQuestion().getId());
							if (otherAnswer != null) {
								if (question.getDependsOnAnswer().equals(
										otherAnswer.getAnswer()))
									isRequired = true;
							}
						}
						if (isRequired)
							requiredCount++;
						
						if (answer != null) {
							if (answer.isAnswered()) {
								answeredCount++;
								if (isRequired)
									requiredAnsweredCount++;
							}
							if (answer.isVerified() || answer.isOK())
								verifiedCount++;
						}

					} else {
						// This must be part of a tuple
						AuditQuestion parentQuestion = question.getParentQuestion();
						for(AuditData rowAnchor : answers.getAnswerList(parentQuestion.getId())) {
							// For each tuple, see if this question is required and filled in
							AuditData answer = answers.get(question, rowAnchor);
							
							isRequired = "Yes".equals(question.getIsRequired());
							if ("Depends".equals(question.getIsRequired())
									&& question.getDependsOnQuestion() != null 
									&& question.getDependsOnAnswer() != null) {
								// This question is dependent on another question's answer
								// Use the parentAnswer, so we get answers in the same tuple as this one
								AuditData otherAnswer = answers.get(question.getDependsOnQuestion(), rowAnchor);
								if (question.getDependsOnAnswer().equals(otherAnswer.getAnswer()))
									isRequired = true;
							}
							if (isRequired)
								requiredCount++;

							if (answer != null) {
								if (answer.isAnswered()) {
									answeredCount++;
									if (isRequired)
										requiredAnsweredCount++;
								}
								if (answer.isVerified() || answer.isOK())
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
		percentCalculateComplete(conAudit, false);
	}

	public void percentCalculateComplete(ContractorAudit conAudit, boolean recalcCats) {
		int required = 0;
		int answered = 0;
		int verified = 0;

		if (recalcCats) {
			recalcAllAuditCatDatas(conAudit);
		}

		for (AuditCatData data : conAudit.getCategories()) {
			if (!conAudit.getAuditType().isDynamicCategories() || data.isAppliesB()) {
				// The category applies or the audit type doesn't have dynamic
				// categories
				required += data.getNumRequired();
				answered += data.getRequiredCompleted();
				verified += (int) Math.round(data.getNumRequired() * data.getPercentVerified() / 100);
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
		if (conAudit.getAuditType().isHasRequirements() && !conAudit.getAuditType().isPqf())
			conAudit.setPercentVerified(percentVerified);
		else if (conAudit.getAuditType().isPqf()) {

			List<AuditData> temp = auditDataDao.findCustomPQFVerifications(conAudit.getId());
			verified = 0;
			int verifiedTotal = 0;

			for (AuditData auditData : temp) {
				// either the pqf or the EMF for the annual addendum
				if (auditData.isVerified()) {
					verified++;
				}
				verifiedTotal++;
			}

			conAudit.setPercentVerified(Math.round((float) (100 * verified) / verifiedTotal));
		}

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
			}
		}
	}

	public void percentOshaComplete(OshaAudit osha, AuditCatData catData) {
		int count = 0;

		if (!osha.isApplicable())
			count = 2;
		else {
			if (osha.getManHours() > 0)
				count++;
			if (osha.isFileUploaded())
				count++;
		}

		int percentComplete = Math.round(count * 100 / 2);
		catData.setRequiredCompleted(count);
		catData.setNumRequired(2);
		catData.setPercentCompleted(percentComplete);

		if (osha.isVerified()) {
			catData.setPercentVerified(100);
		} else {
			catData.setPercentVerified(0);
		}

		catDataDao.save(catData);
	}

}
