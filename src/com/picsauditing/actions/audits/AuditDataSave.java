package com.picsauditing.actions.audits;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.model.events.AuditDataSaveEvent;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class AuditDataSave extends AuditActionSupport {

	private static final String NO = "No";

	private static final long serialVersionUID = 1103112846482868309L;

	protected static final int OSHA_INCIDENT_QUESTION_ID = 8838;
	protected static final int COHS_INCIDENT_QUESTION_ID = 8840;

	// protected static final int[] OSHA_INCIDENT_RELATED_QUESTION_IDS = new
	// int[] { 8812, 8813, 8814, 8815, 8816, 8817 };
	protected static final Set<Integer> OSHA_INCIDENT_RELATED_QUESTION_IDS = Collections
			.unmodifiableSet(new HashSet<Integer>(Arrays.asList(8812, 8813, 8814, 8815, 8816, 8817)));
	protected static final Set<Integer> COHS_INCIDENT_RELATED_QUESTION_IDS = Collections
			.unmodifiableSet(new HashSet<Integer>(Arrays.asList(8841, 8842, 8843, 8844, 11119, 8845, 8846, 8847, 11117,
					11118)));
	// protected static final int[] COHS_INCIDENT_RELATED_QUESTION_IDS = new
	// int[] { 8841, 8842, 8843, 8844, 11119, 8845,
	// 8846, 8847, 11117, 11118 };

	private AuditData auditData = null;
	private String[] multiAnswer;
	private AnswerMap answerMap;
	private String mode;
	private boolean toggleVerify = false;

	// e-signature data
	private String eSignatureName = null;
	private String eSignatureTitle = null;

	@Autowired
	private AuditQuestionDAO questionDao = null;
	@Autowired
	private NaicsDAO naicsDAO;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private AuditBuilder auditBuilder;

	public String execute() throws Exception {

		AuditCatData catData;
		try {
			getUser();
			AuditData newCopy = null;
			if (auditData.getId() > 0) {
				newCopy = auditDataDAO.find(auditData.getId());
			} else {
				if (auditData.getAudit() == null)
					throw new Exception("Missing Audit");
				if (auditData.getQuestion() == null)
					throw new Exception("Missing Question");
				newCopy = auditDataDAO.findAnswerToQuestion(auditData.getAudit().getId(), auditData.getQuestion()
						.getId());
			}

			auditID = auditData.getAudit().getId();

			/*
			 * If we are reloading the question, we need to exit early to
			 * prevent the object from saving.
			 */
			if ("reload".equals(button)) {
				if (auditData.getId() == 0 && newCopy != null) {
					auditData = newCopy;
				}
				loadAnswerMap();

				return SUCCESS;
			}

			boolean verifyButton = ("verify".equals(button));
			boolean commentChanged = false;
			boolean answerChanged = false;

			AuditQuestion dataQuestion = questionDao.find(auditData.getQuestion().getId());
			// get by lazy load
			dataQuestion.setCategory(dataQuestion.getCategory());
			auditData.setQuestion(dataQuestion);

			/*
			 * If the `newCopy` is not set, then this is the first time the
			 * question is being answered.
			 */
			if (newCopy == null) {
				// insert mode
				ContractorAudit audit = auditDao.find(auditData.getAudit().getId());
				auditData.setAudit(audit);
				if (!answerFormatValid(auditData, null)) {
					return SUCCESS;
				}
				SpringUtils.publishEvent(new AuditDataSaveEvent(auditData));
			} else {
				// update mode
				if (!answerFormatValid(auditData, newCopy)) {
					return SUCCESS;
				}

				commentChanged = hasCommentChanged(newCopy);
				answerChanged = hasAnswerChanged(newCopy);

				if (commentChanged) {
					newCopy.setComment(auditData.getComment());
				}

				if (answerChanged) {
					changeAuditDataAnswer(newCopy);
				}

				if (verifyButton) {
					verifyAuditData(newCopy);
				}

				auditData = newCopy;
			}

			loadAnswerMap();

			auditData.setAuditColumns(permissions);

			int currentQuestionId = auditData.getQuestion().getId();

			checkNaicsQuestionAndValidity(currentQuestionId);

			if (!auditData.getAnswer().isEmpty()) {
				if (!areAllHSEJobRoleQuestionsAnswered(currentQuestionId, auditData.getAudit().getContractorAccount()))
					return SUCCESS;
			}

			auditDataDAO.save(auditData);

			if (conAudit == null) {
				findConAudit();
			}

			// TODO IS THIS NEEDED?
			if (conAudit == null) {
				addActionError(getText("Audit.error.AuditNotFound"));
				return SUCCESS;
			}
			// END TODO

			checkUniqueCode(conAudit);

			if (auditData.getAudit() != null) {
				ContractorAudit tempAudit = null;
				if (!auditDao.isContained(auditData.getAudit())) {
					tempAudit = conAudit;
				} else
					tempAudit = auditData.getAudit();

				ContractorAccount contractor = tempAudit.getContractorAccount();
				contractor.incrementRecalculation();
				if (tempAudit.getAuditType().isPqf()) {
					if (auditData.getQuestion().getId() == 57) {
						if (isValidNAICScode(auditData.getAnswer())) {
							contractor.setNaics(new Naics());
							contractor.getNaics().setCode(auditData.getAnswer());
							contractor.setNaicsValid(true);
						} else {
							String guess = guessNaicsCode(auditData.getAnswer());
							contractor.setNaics(new Naics());
							contractor.setNaicsValid(true);
							contractor.getNaics().setCode(guess);
							// addActionError("Setting your current NAICS code to "
							// + guess);
						}
					}
				}
				contractorAccountDao.save(contractor);

				if (auditData.getQuestion().isRecalculateCategories()) {
					auditBuilder.recalculateCategories(tempAudit);
					auditDao.save(tempAudit);
				}

				auditCategoryRuleCache.initialize(auditRuleDAO);
				AuditCategoriesBuilder builder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);

				if (tempAudit.getAuditType().isAnnualAddendum() && !toggleVerify && !commentChanged) {
					boolean updateAudit = false;
					for (ContractorAuditOperator cao : tempAudit.getOperators()) {
						Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
						for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions())
							operators.add(caop.getOperator());
						builder.calculate(auditData.getAudit(), operators);

						if (cao.getStatus().between(AuditStatus.Submitted, AuditStatus.Complete)
								&& builder.isCategoryApplicable(auditData.getQuestion().getCategory(), cao)) {
							ContractorAuditOperatorWorkflow caow = cao
									.changeStatus(AuditStatus.Incomplete, permissions);
							caow.setNotes("Due to data change");
							caowDAO.save(caow);
							updateAudit = true;
							break;
						}
					}
					if (updateAudit)
						auditDao.save(tempAudit);
				}

				if (tempAudit.getAuditType().getId() == AuditType.COR) {
					if (auditData.getQuestion().getId() == 2950) {
						tempAudit.setAuditFor(auditData.getAnswer());
						auditDao.save(tempAudit);
					}
				}
			}

			// hook to calculation read/update
			// the ContractorAudit and AuditCatData
			try {
				catData = catDataDao.findAuditCatData(auditData.getAudit().getId(), auditData.getQuestion()
						.getCategory().getId());
			} catch (NoResultException e) {
				// Create AuditCatData for categories that don't have one
				// yet
				// I.E. - Old Categories
				catData = new AuditCatData();
				catData.setCategory(auditData.getQuestion().getCategory());
				catData.setAudit(auditData.getAudit());
				catData.setApplies(true);
				catData.setOverride(false);
				catData.setAuditColumns(new User(User.SYSTEM));

				catDataDao.save(catData);

				// Need to refresh the auditData to query up the required
				// questions off of it
				auditData = auditDataDAO.find(auditData.getId());

				loadAnswerMap();
			}

		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e.getMessage());
			return BLANK;
		}

		if (conAudit == null)
			findConAudit();

		if (toggleVerify) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
			auditDao.save(catData);
		}

		// check dependent questions, see if not in same cat
		// check rules to see if other cats get triggered now
		// if either true then run FAC
		if (!contractor.getStatus().isPending()) {

			if (conAudit.getAuditType().getClassType().isPolicy() && checkFlagCriteria()) {
				contractor.setLastRecalculation(null);
				contractor.setNeedsRecalculation(ContractorAccount.MAX_RECALC);
			}

			if (checkDependentQuestions() || checkOtherRules()) {
				auditBuilder.recalculateCategories(conAudit);
				auditPercentCalculator.percentCalculateComplete(conAudit, true);
				auditDao.save(conAudit);
			} else if (catData != null) {
				auditPercentCalculator.updatePercentageCompleted(catData);
				catData.setAuditColumns();
				auditDao.save(catData);
			} else
				addActionError("Error saving answer, please try again.");
		}
		autoFillRelatedOshaIncidentsQuestions(auditData);
		return SUCCESS;
	}

	private void checkNaicsQuestionAndValidity(int currentQuestionId) {
		if (currentQuestionId == 57) {
			if ("0".equals(guessNaicsCode(auditData.getAnswer()))) {
				addActionError("This is not a valid 2007 NAICS code");
			}
		}
	}

	private void verifyAuditData(AuditData newCopy) {
		// verify mode
		if (newCopy.isVerified()) {
			newCopy.setDateVerified(null);
			newCopy.setAuditor(null);
		} else {
			newCopy.setDateVerified(new Date());
			newCopy.setAuditor(getUser());
		}
	}

	private void changeAuditDataAnswer(AuditData newCopy) {
		boolean isAudit = newCopy.getAudit().getAuditType().getClassType().isAudit();
		boolean isAnnualUpdate = newCopy.getAudit().getAuditType().isAnnualAddendum();

		if (isAudit && !isAnnualUpdate) {
			AuditQuestion question = questionDao.find(auditData.getQuestion().getId());
			if (question.getOkAnswer() != null && question.getOkAnswer().contains(auditData.getAnswer())
					&& permissions.isAdmin()) {
				newCopy.setDateVerified(new Date());
				newCopy.setAuditor(getUser());
			}
			if (newCopy.isVerified()
					&& (newCopy.getAudit().getAuditType().getId() == AuditType.COR || newCopy.getAudit().getAuditType()
							.getId() == AuditType.IEC_AUDIT)) {
				newCopy.setDateVerified(null);
				newCopy.setAuditor(null);
			}
		} else if (newCopy.isVerified()) {
			newCopy.setDateVerified(null);
			newCopy.setAuditor(null);
		}

		if (newCopy.getAudit().hasCaoStatus(AuditStatus.Submitted) && permissions.isPicsEmployee())
			newCopy.setWasChanged(YesNo.Yes);

		newCopy.setAnswer(auditData.getAnswer());
	}

	private boolean hasAnswerChanged(AuditData newCopy) {
		if (auditData.getAnswer() != null) {
			if (newCopy.getAnswer() == null || !newCopy.getAnswer().equals(auditData.getAnswer()))
				return true;
		}
		return false;
	}

	private boolean hasCommentChanged(AuditData newCopy) {
		if (auditData.getComment() != null) {
			if (newCopy.getComment() == null || !newCopy.getComment().equals(auditData.getComment()))
				return true;
		}
		return false;
	}

	private boolean areAllHSEJobRoleQuestionsAnswered(int currentQuestionId, ContractorAccount contractor) {
		if (currentQuestionId == 3669) {
			if (contractor.getJobRoles().isEmpty()) {
				addActionError(getText("EmployeeGUARD.Error.AtLeastOne.JobRole"));
				return false;
			}
		} else if (currentQuestionId == 3675) {
			for (JobRole role : contractor.getJobRoles()) {
				if (role.getJobCompetencies().isEmpty()) {
					addActionError(getText("EmployeeGUARD.Error.AtLeastOne.CompetencyForEachJobRole"));
					return false;
				}
			}
		} else if (currentQuestionId == 3673) {
			if (contractor.getEmployees().isEmpty()) {
				addActionError(getText("EmployeeGUARD.Error.AtLeastOne.Employee"));
				return false;
			}
		} else if (currentQuestionId == 3674) {
			for (Employee employee : contractor.getActiveEmployees()) {
				if (employee.getEmployeeRoles().isEmpty()) {
					addActionError(getText("EmployeeGUARD.Error.AtLeastOne.JobRoleForEachEmployee"));
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * This is a special case where the Contractor can say they have not had any
	 * incidents this year and the questions related to the number of incidents
	 * are set to zero.
	 */
	private void autoFillRelatedOshaIncidentsQuestions(AuditData newCopy) {
		if (newCopy == null) {
			return;
		}

		boolean recalcAudit = false;

		if (newCopy.getQuestion().getId() == OSHA_INCIDENT_QUESTION_ID) {
			if (newCopy.getAnswer().equals(NO)) {
				recalcAudit = true;
				for (int incidentQuestionId : OSHA_INCIDENT_RELATED_QUESTION_IDS) {
					AuditData auditData = auditDataDAO.findAnswerToQuestion(this.auditData.getAudit().getId(),
							incidentQuestionId);
					if (auditData == null) {
						auditData = new AuditData();
						auditData.setId(0);
						auditData.setAudit(conAudit);
						AuditQuestion auditQuestion = questionDao.find(incidentQuestionId);
						auditData.setQuestion(auditQuestion);
					}

					auditData.setAuditColumns(permissions);
					auditData.setAnswer("0");

					auditDataDAO.save(auditData);
				}
			}
		} else if (newCopy.getQuestion().getId() == COHS_INCIDENT_QUESTION_ID) {
			if (newCopy.getAnswer().equals(NO)) {
				recalcAudit = true;
				for (int incidentQuestionId : COHS_INCIDENT_RELATED_QUESTION_IDS) {
					AuditData auditData = auditDataDAO.findAnswerToQuestion(this.auditData.getAudit().getId(),
							incidentQuestionId);
					if (auditData == null) {
						auditData = new AuditData();
						auditData.setId(0);
						auditData.setAudit(conAudit);
						AuditQuestion auditQuestion = questionDao.find(incidentQuestionId);
						auditData.setQuestion(auditQuestion);
					}

					auditData.setAuditColumns(permissions);
					auditData.setAnswer("0");

					auditDataDAO.save(auditData);
				}
			}
		}

		if (recalcAudit) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
		}

	}

	private void checkUniqueCode(ContractorAudit tempAudit) {
		// TODO: Extract this into it's own class.
		if ("policyExpirationDate".equals(auditData.getQuestion().getUniqueCode())
				&& !StringUtils.isEmpty(auditData.getAnswer())) {
			Date expiresDate = DateBean.getNextDayMidnight(DateBean.parseDate(auditData.getAnswer()));
			if (!DateBean.isNullDate(expiresDate))
				tempAudit.setExpiresDate(expiresDate);
			// In case the answer is not a valid date we add 1 year
			// to the policy's creation date.
			if (tempAudit.getExpiresDate() == null) {
				tempAudit.setExpiresDate(DateBean.setToEndOfDay(DateBean.addMonths(tempAudit.getCreationDate(), 12)));
			}
			auditDao.save(tempAudit);
		}
		if ("policyExpirationDatePlus120".equals(auditData.getQuestion().getUniqueCode())
				&& !StringUtils.isEmpty(auditData.getAnswer())) {
			Date expiresDate = DateBean.getNextDayMidnight(DateBean.parseDate(auditData.getAnswer()));
			if (!DateBean.isNullDate(expiresDate)) {
				Calendar date = Calendar.getInstance();
				date.setTime(expiresDate);
				date.add(Calendar.DATE, 120);
				tempAudit.setExpiresDate(date.getTime());
			}
			// In case the answer is not a valid date we add 1 year
			// to the policy's creation date.
			if (tempAudit.getExpiresDate() == null) {
				tempAudit.setExpiresDate(DateBean.setToEndOfDay(DateBean.addMonths(tempAudit.getCreationDate(), 12)));
			}
			auditDao.save(tempAudit);
		}
		if ("policyExpirationDatePlusMonthsToExpire".equals(auditData.getQuestion().getUniqueCode())
				&& !StringUtils.isEmpty(auditData.getAnswer())) {
			int monthsToExpire = 12;
			Integer specifiedMonthsToExpire = tempAudit.getAuditType().getMonthsToExpire();
			if (specifiedMonthsToExpire != null)
				monthsToExpire = specifiedMonthsToExpire.intValue();

			Date expiresDate = DateBean.getNextDayMidnight(DateBean.parseDate(auditData.getAnswer()));
			if (!DateBean.isNullDate(expiresDate)) {
				Calendar date = Calendar.getInstance();
				date.setTime(expiresDate);
				date.add(Calendar.MONTH, monthsToExpire);
				tempAudit.setExpiresDate(date.getTime());
			}
			// In case the answer is not a valid date we add months to expire
			// to the policy's creation date.
			if (tempAudit.getExpiresDate() == null) {
				tempAudit.setExpiresDate(DateBean.setToEndOfDay(DateBean.addMonths(tempAudit.getCreationDate(),
						monthsToExpire)));
			}
			auditDao.save(tempAudit);
		}
		if ("policyEffectiveDate".equals(auditData.getQuestion().getUniqueCode())
				&& !StringUtils.isEmpty(auditData.getAnswer())) {
			Date creationDate = DateBean.parseDate(auditData.getAnswer());
			if (!DateBean.isNullDate(creationDate))
				tempAudit.setCreationDate(creationDate);
			auditDao.save(tempAudit);
		}
		if ("exipireMonths12".equals(auditData.getQuestion().getUniqueCode())
				&& !StringUtils.isEmpty(auditData.getAnswer())) {
			Date expiresDate = DateBean.setToEndOfDay(DateBean.parseDate(auditData.getAnswer()));
			if (!DateBean.isNullDate(expiresDate)) {
				Calendar date = Calendar.getInstance();
				date.setTime(expiresDate);
				date.add(Calendar.MONTH, 12);
				tempAudit.setExpiresDate(date.getTime());
			}
			auditDao.save(tempAudit);
		}
	}

	/**
	 * @return True if a rule that would be triggered from this question, false
	 *         otherwise
	 */
	private boolean checkOtherRules() {
		for (AuditCategoryRule acr : auditRuleDAO.findCategoryRulesByQuestion(auditData.getQuestion().getId())) {
			if (acr.isMatchingAnswer(auditData))
				return true;
		}
		return false;
	}

	/**
	 * @return True if a dependent question is in a different category, false
	 *         otherwise
	 */
	private boolean checkDependentQuestions() {
		for (AuditQuestion aq : auditData.getQuestion().getDependentQuestions()) {
			if (aq.getCategory() != auditData.getQuestion().getCategory())
				return true;
		}
		return false;
	}

	private boolean checkFlagCriteria() {
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			for (FlagCriteriaOperator fco : co.getOperatorAccount().getFlagCriteriaInherited()) {
				if (fco.getCriteria().getQuestion() != null
						&& fco.getCriteria().getQuestion().getId() == auditData.getQuestion().getId()) {
					return true;
				}
			}
		}

		return false;
	}

	private void loadAnswerMap() {
		List<Integer> questionIds = new ArrayList<Integer>();
		AuditQuestion question = questionDao.find(auditData.getQuestion().getId());
		auditData.setQuestion(question);
		questionIds.add(auditData.getQuestion().getId());
		if (auditData.getQuestion().getRequiredQuestion() != null) {
			AuditQuestion q = auditData.getQuestion().getRequiredQuestion();
			while (q != null) {
				questionIds.add(q.getId());
				q = q.getRequiredQuestion();
			}
		}
		if (auditData.getQuestion().getVisibleQuestion() != null) {
			AuditQuestion q = auditData.getQuestion().getVisibleQuestion();
			while (q != null) {
				questionIds.add(q.getId());
				q = q.getVisibleQuestion();
			}
		}

		questionIds.addAll(auditData.getQuestion().getSiblingQuestionWatchers());

		answerMap = auditDataDAO.findAnswers(auditID, questionIds);
	}

	public String getMode() {
		return mode == null ? "View" : mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}

	public String[] getMultiAnswer() {
		return multiAnswer;
	}

	public void setMultiAnswer(String[] multiAnswer) {
		this.multiAnswer = multiAnswer;
	}

	public AnswerMap getAnswerMap() {
		return answerMap;
	}

	public boolean isToggleVerify() {
		return toggleVerify;
	}

	public void setToggleVerify(boolean toggleVerify) {
		this.toggleVerify = toggleVerify;
	}

	public ArrayList<String> getEmrProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Need EMR");
		list.add("Need Loss Run");
		list.add("Not Insurance Issued");
		list.add("Incorrect Upload");
		list.add("Incorrect Year");
		return list;
	}

	private boolean answerFormatValid(AuditData auditData, AuditData databaseCopy) {

		if (databaseCopy == null) {
			databaseCopy = auditData;
		}

		String questionType = databaseCopy.getQuestion().getQuestionType();
		String answer = auditData.getAnswer();

		if ("ESignature".equals(questionType)) {
			if (eSignatureName == null && eSignatureTitle == null && !Strings.isEmpty(answer) && answer.contains(" / ")) {
				String[] esig = answer.split(" / ");
				eSignatureName = esig[0];
				eSignatureTitle = esig[1];
			}

			if (Strings.isEmpty(eSignatureName)) {
				addActionError(getText("AuditData.ESignature.name.missing"));
			}

			if (Strings.isEmpty(eSignatureTitle)) {
				addActionError(getText("AuditData.ESignature.title.missing"));
			}

			if (hasActionErrors()) {
				return false;
			}

			// Strip the first comma that results from the two part answer.
			auditData.setAnswer(eSignatureName + " / " + eSignatureTitle);
			auditData.setComment(getIP());
		}

		// Null or blank answers are always OK
		if (Strings.isEmpty(answer))
			return true;

		if ("Money".equals(questionType) || "Decimal Number".equals(questionType) || "Number".equals(questionType)) {
			answer = trimWhitespaceLeadingZerosAndAllCommas(answer);

			boolean hasBadChar = false;
			for (int i = 0; i < answer.length(); i++) {
				char c = answer.charAt(i);
				if (!Character.isDigit(c) && (c != '.') && (c != '-'))
					hasBadChar = true;
			}

			if (hasBadChar) {
				addActionError(getText("AuditData.error.MustBeNumber"));
				return false;
			}

			NumberFormat format;
			if ("Decimal Number".equals(questionType)) {
				format = new DecimalFormat("#,##0.000");
			} else if ("Number".equals(questionType)) {
				format = new DecimalFormat("###0");
			} else {
				format = new DecimalFormat("#,##0");
			}

			try {
				BigDecimal value = new BigDecimal(answer);
				auditData.setAnswer(format.format(value));
			} catch (Exception ignore) {
				addActionError(getText("Audit.message.InvalidFormat"));
				return false;
			}
		}

		if ("Date".equals(questionType)) {
			return setAnswerToDateOrRecordError(auditData, answer);
		}

		if ("Check Box".equals(questionType)) {
			if (answer.equals("false"))
				auditData.setAnswer("");
		}

		return true;
	}

	private boolean setAnswerToDateOrRecordError(AuditData auditData, String answer) {
		Date newDate = DateBean.parseDate(answer);

		if (newDate == null) {
			addActionError(getText("AuditData.error.InvalidDate"));
			return false;
		} else if (newDate.after(DateBean.parseDate("9999-12-31"))) {
			addActionError(getText("AuditData.error.DateOutOfRange"));
			return false;
		} else {
			String a = DateBean.toDBFormat(newDate);
			auditData.setAnswer(a);
			return true;
		}
	}

	public static String trimWhitespaceLeadingZerosAndAllCommas(String answer) {
		return answer.trim().replaceAll(",", "").replaceAll("^0+(?!$)", "");
	}

	private boolean isValidNAICScode(String code) {
		Naics naics = naicsDAO.find(code);
		if (naics != null)
			return true;
		return false;
	}

	private String guessNaicsCode(String naics) {
		if (Strings.isEmpty(naics))
			return "0";

		if (isValidNAICScode(naics))
			return naics;

		return guessNaicsCode(naics.substring(0, naics.length() - 1));
	}

	public String getESignatureName() {
		return eSignatureName;
	}

	public void setESignatureName(String eSignatureName) {
		this.eSignatureName = eSignatureName;
	}

	public String getESignatureTitle() {
		return eSignatureTitle;
	}

	public void setESignatureTitle(String eSignatureTitle) {
		this.eSignatureTitle = eSignatureTitle;
	}
}
