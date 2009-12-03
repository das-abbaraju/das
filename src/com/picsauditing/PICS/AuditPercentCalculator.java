package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

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
		
		Set<String> countries = catData.getAudit().getContractorAccount().getCountries();
		catData.getCategory().setCountries(countries);
		
		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int verifiedCount = 0;
		int scoreCount = 0;
		int score = 0;

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
		// System.out.println(answers);

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
		for (AuditSubCategory subCategory : catData.getCategory().getValidSubCategories()) {
			for (AuditQuestion question : subCategory.getQuestions()) {
				if (validDate.after(question.getEffectiveDate()) && validDate.before(question.getExpirationDate())
						&& Strings.isInCountries(question.getCountries(), countries)) {

					boolean isRequired = false;

					AuditData answer = answers.get(question.getId());
					// This question isn't part of a tuple
					isRequired = "Yes".equals(question.getIsRequired());
					if ("Depends".equals(question.getIsRequired()) && question.getDependsOnQuestion() != null
							&& question.getDependsOnAnswer() != null) {
						if (question.getDependsOnAnswer().equals("NULL")) {
							AuditData otherAnswer = answers.get(question.getDependsOnQuestion().getId());
							if (otherAnswer == null)
								isRequired = true;
						} else {
							// This question is dependent on another question's
							// answer
							// Use the parentAnswer, so we get answers in the
							// same tuple as this one
							AuditData otherAnswer = answers.get(question.getDependsOnQuestion().getId());
							if (otherAnswer != null && question.getDependsOnAnswer().equals(otherAnswer.getAnswer()))
								isRequired = true;
						}
					}
					if (isRequired)
						requiredCount++;

					if (answer != null) {
						if (answer.isAnswered()) {

							if ("Radio".equals(question.getQuestionType())) {
								Integer tempScore = scoreMap.get(answer.getAnswer());
								score += tempScore != null ? tempScore : -1000;
							}

							answeredCount++;
							if (isRequired)
								requiredAnsweredCount++;
						}

						if (answer.getQuestion().isHasRequirementB()) {
							if (answer.isOK())
								verifiedCount++;
						} else {
							if (answer.isVerified()) {
								verifiedCount++;
							} else if (catData.getAudit().getAuditType().isHasRequirements()) {
								verifiedCount++;
							}
						}	
					}

					if ("Radio".equals(question.getQuestionType())) {
						scoreCount++;
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
			if (catData.getAudit().getAuditType().isAnnualAddendum()
					&& catData.getCategory().getId() == AuditCategory.GENERAL_INFORMATION
					&& catData.getNumRequired() > 2) {
				requiredCount = requiredCount - 2;
			}
			int percentVerified = (int) Math.floor((100 * verifiedCount) / requiredCount);
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

	public void percentCalculateComplete(ContractorAudit conAudit) {
		percentCalculateComplete(conAudit, false);
	}

	public void percentCalculateComplete(ContractorAudit conAudit, boolean recalcCats) {
		int required = 0;
		int answered = 0;
		int verified = 0;

		int scoreCount = 0;
		float runningScore = 0;

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

				if (data.getScoreCount() > 0) {
					scoreCount += data.getScoreCount();
					runningScore += (data.getScore() * data.getScoreCount());
				}
			}
		}

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
		conAudit.setPercentComplete(percentComplete);
		if (conAudit.getAuditType().isHasRequirements() || conAudit.getAuditType().isMustVerify()) {
			if (conAudit.getAuditType().isPqf()) {
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
			} else
				conAudit.setPercentVerified(percentVerified);
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
		if (osha.getType().equals(OshaType.MSHA) || osha.getType().equals(OshaType.COHS)) {
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
