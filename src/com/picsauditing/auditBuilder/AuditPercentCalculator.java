package com.picsauditing.auditBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

public class AuditPercentCalculator {
	private List<AuditData> verifiedPqfData = null;

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

				// make sure this dependent required question is visible
				if (isRequired) {
					if (question.getVisibleQuestion() != null && question.getVisibleAnswer() != null) {
						AuditData otherAnswer = answers.get(question.getVisibleQuestion().getId());
						if (!question.isVisible(otherAnswer))
							isRequired = false;
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
							float scorePercentage = 0.0f;

							if (answer.isMultipleChoice()) {
								for (AuditOptionValue value : question.getOption().getValues()) {
									if (answer.getAnswer().equals(value.getIdentifier())) {
										scorePercentage = value.getScorePercent();
										break;
									}
								}
							}

							score += Math.round(question.getScoreWeight() * scorePercentage);
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
		catData.setScoreCount(scoreCount);
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
		AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache,
				conAudit.getContractorAccount());

		Set<AuditCategory> auditCategories = builder.calculate(conAudit);

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
		if (verifiedPqfData == null)
			verifiedPqfData = auditDataDAO.findCustomPQFVerifications(auditID);
		return verifiedPqfData;
	}

}