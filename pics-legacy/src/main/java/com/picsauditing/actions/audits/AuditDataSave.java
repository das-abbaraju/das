package com.picsauditing.actions.audits;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.audits.AuditBuilderFactory;
import com.picsauditing.audits.AuditPercentCalculator;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.models.audits.CaoSaveModel;
import com.picsauditing.rbic.InsuranceCriteriaDisplay;
import com.picsauditing.service.AuditDataService;
import com.picsauditing.service.ContractorAuditService;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AuditDataSave extends AuditActionSupport {

	public static final int VALID_YEARS_IN_FUTURE = 10;
	public static final int ANSWER_MIN_YEAR = 1800;// TODO: should be moved to app_properties[AuditData.minYear]

	private static final String NO = "No";

	private static final long serialVersionUID = 1103112846482868309L;

	protected static final int OSHA_INCIDENT_QUESTION_ID = 8838;
	protected static final int COHS_INCIDENT_QUESTION_ID = 8840;

	protected static final Set<Integer> OSHA_INCIDENT_RELATED_QUESTION_IDS = Collections
			.unmodifiableSet(new HashSet<Integer>(Arrays.asList(8812, 8813, 8814, 8815, 8816, 8817)));
	protected static final Set<Integer> COHS_INCIDENT_RELATED_QUESTION_IDS = Collections
			.unmodifiableSet(new HashSet<Integer>(Arrays.asList(8841, 8842, 8843, 8844, 11119, 8845, 8846, 8847, 11117,
					11118)));

	private AuditData auditData = null;
	private String[] multiAnswer;
	private AnswerMap answerMap;
	private String mode;
	private boolean toggleVerify = false;

	// e-signature data
	private String eSignatureName = null;
	private String eSignatureTitle = null;

	@Autowired
	private AuditDataService auditDataService;
	@Autowired
	private ContractorAuditService contractorAuditService;
	@Autowired
	private AuditQuestionDAO questionDao = null;
	@Autowired
	private NaicsDAO naicsDAO;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private AuditBuilderFactory auditBuilderFactory;
    @Autowired
    private CaoSaveModel caoSaveModel;
    private boolean verifyButton;


    public String execute() throws Exception {
        if (auditData == null) {
            addActionError(getText("Missing Audit Data"));
            return BLANK;
        }

		AuditCatData catData;
		try {
			user = getUser();

			AuditData databaseCopy = loadAuditData(auditData);
			auditID = auditData.getAudit().getId();
			auditData = loadAuditDataQuestion(auditData);

			if (conAudit == null) {
				conAudit = contractorAuditService.findContractorAudit(auditID);

				if (conAudit == null) {
					addActionError(getText("Audit.error.AuditNotFound"));
					return SUCCESS;
				}

				try {
					checkContractorAuditPermissions(conAudit);
				} catch (NoRightsException e) {
					addActionError(getText("Audit.error.AuditNotFound"));
					return SUCCESS;
				}

				professionalLabel = getProfessionalLabelText(conAudit.getAuditType().getAssigneeLabel());
				calculateRefreshAudit();
				showUploadRequirementsBanner = getShowUploadRequirementsBanner(conAudit);
			}

			// todo: Revisit how we use databaseCopy throughout
			if ("reload".equals(button)) {
				if (auditData.getId() == 0 && databaseCopy != null) {
					auditData = databaseCopy;
				}
				answerMap = auditDataService.loadAnswerMap(auditData);
				return SUCCESS;
			}

            verifyButton = ("verify".equals(button));
			boolean commentChanged = false;
			boolean answerChanged = false;

			if (answerIsNew(databaseCopy)) {
				auditData = processAuditDataForNewAnswer(auditData);

				databaseCopy = auditData;
				AuditQuestion auditQuestion = auditDataService.findAuditQuestion(databaseCopy.getQuestion().getId());
				databaseCopy.setQuestion(auditQuestion);

				if (!processAndValidateBasedOnQuestionType(auditData, databaseCopy)) {
					return SUCCESS;
				}
				auditDataService.insertAuditData(auditData);
			} else {
				if (!processAndValidateBasedOnQuestionType(auditData, databaseCopy)) {
					return SUCCESS;
				}

				commentChanged = hasCommentChanged(databaseCopy);
				answerChanged = hasAnswerChanged(databaseCopy);

				if (commentChanged) {
					databaseCopy.setComment(auditData.getComment());
				}

				if (answerChanged) {
					changeAuditDataAnswer(databaseCopy);
				}

				if (verifyButton) {
					verifyAuditData(databaseCopy);
				}

				auditData = databaseCopy;
				loadCategoryIfNeeded();
			}

			answerMap = auditDataService.loadAnswerMap(auditData);

			auditData.setAuditColumns(permissions);

			int currentQuestionId = auditData.getQuestion().getId();

			checkNaicsQuestionAndValidity(currentQuestionId);

			if (auditData.getAnswer() != null && !auditData.getAnswer().isEmpty()) {
				if (!areAllHSEJobRoleQuestionsAnswered(currentQuestionId, auditData.getAudit().getContractorAccount())) {
					return SUCCESS;
				}
			}

			auditDataService.saveAuditData(auditData);
			// todo: Investigate. Barring a trigger, why would we need to resync from the db if we just saved? Ajax stuff?
			auditDataDAO.refresh(auditData); // needed for PICS-11673

			checkUniqueCode(conAudit);

			if (auditData.getAudit() != null) {
				ContractorAudit tempAudit = null;
				if (!auditDao.isContained(auditData.getAudit())) {
					tempAudit = conAudit;
				} else {
					tempAudit = auditData.getAudit();
				}

				ContractorAccount contractor = tempAudit.getContractorAccount();
				contractor.incrementRecalculation();
				if (tempAudit.getAuditType().isPicsPqf()) {
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

				saveContractorAccount(contractor);

				AuditQuestion checkRecalculateCategories = questionDao.find(currentQuestionId);
				if (checkRecalculateCategories.isRecalculateCategories()) {
					auditBuilderFactory.recalculateCategories(tempAudit);
					auditDao.save(tempAudit);
				}

				if (tempAudit.getAuditType().isRollback() && !toggleVerify && !commentChanged) {
					boolean updateAudit = false;
					for (ContractorAuditOperator cao : tempAudit.getOperators()) {
						Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
						for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
							operators.add(caop.getOperator());
						}

                        boolean isApplicable = auditBuilderFactory.isCategoryApplicable(auditData.getQuestion().getCategory(), auditData.getAudit(), cao);
						if (cao.getStatus().between(AuditStatus.Submitted, AuditStatus.Approved)
								&& isApplicable) {
                            AuditStatus newStatus = getRollbackStatus(tempAudit, cao);

							ContractorAuditOperatorWorkflow caow = cao
									.changeStatus(newStatus, permissions);
							if (caow != null) {
								caow.setNotes("Due to data change");
								caowDAO.save(caow);
								updateAudit = true;
							}

                            caoSaveModel.updateParentAuditOnCompleteIncomplete(tempAudit, newStatus);
						}
					}
					if (updateAudit) {
						auditDao.save(tempAudit);
					}
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

				try {
					catDataDao.save(catData);

					// Need to refresh the auditData to query up the required
					// questions off of it
					auditData = auditDataDAO.find(auditData.getId());

					answerMap = auditDataService.loadAnswerMap(auditData);
				} catch (Exception x) {
					throw new Exception("Error saving category. Please refresh the page and try again.");
				}
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
			return BLANK;
		}

		if (conAudit == null) {
			findConAudit();
		}

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
				contractor.incrementRecalculation(10);
			}

			// refresh auditData since it might be not a complete representation
			// this is okay at this time because auditData has a valid id
			// and setting the question will not conflict with database key
			// contraints
			auditData = auditDataDAO.find(auditData.getId());
			AuditQuestion auditDataQuestion = questionDao.find(auditData.getQuestion().getId());
			auditData.setQuestion(auditDataQuestion);

            recalculateAuditCatData(catData);
        }
		autoFillRelatedOshaIncidentsQuestions(auditData);
		return SUCCESS;
	}

    private void recalculateAuditCatData(AuditCatData catData) {
        if (checkDependentQuestions() || checkOtherRules()) {
            auditBuilderFactory.recalculateCategories(conAudit);
            auditPercentCalculator.percentCalculateComplete(conAudit, true);
            auditDao.save(conAudit);
        } else if (catData != null) {
            if (conAudit.getAuditType().isScoreable())
                auditPercentCalculator.percentCalculateComplete(conAudit, true);
            else
                auditPercentCalculator.updatePercentageCompleted(catData);
            catData.setAuditColumns();
            auditDao.save(catData);
        } else {
            addActionError("Error saving answer, please try again.");
        }
    }

    private AuditStatus getRollbackStatus(ContractorAudit tempAudit, ContractorAuditOperator cao) {
        AuditStatus newStatus = AuditStatus.Incomplete;

        if (tempAudit.getAuditType().getRollbackStatus() != null) {
            newStatus = tempAudit.getAuditType().getRollbackStatus();
        } else if (tempAudit.getAuditType().getClassType().isPolicy() && permissions.isAdmin()) {
            newStatus = AuditStatus.Resubmitted;
        } else if (tempAudit.getAuditType().getClassType().isPolicy() && cao.getStatus().after(AuditStatus.Submitted)) {
            newStatus = AuditStatus.Resubmitted;
        }
        return newStatus;
    }

    private BaseTable saveContractorAccount(ContractorAccount contractor) {
		return contractorAccountDao.save(contractor);   // todo: Move to a service.
	}

	private AuditData processAuditDataForNewAnswer(AuditData auditData) {
		ContractorAudit audit = contractorAuditService.findContractorAudit(auditData.getAudit().getId());
		auditData = setQuestionCategoryIfNecessary(auditData);
		auditData.setAudit(audit);
		return auditData;
	}

	private AuditData setQuestionCategoryIfNecessary(AuditData auditData) {
		AuditQuestion question = auditData.getQuestion();
		if (question.getCategory() == null) {
			AuditQuestion dataQuestion = auditDataService.findAuditQuestion(auditData.getQuestion().getId());
			auditData.getQuestion().setCategory(dataQuestion.getCategory());
		}
		return auditData;
	}

	private boolean answerIsNew(AuditData databaseCopy) {
		return databaseCopy == null;
	}

	private AuditData loadAuditData(AuditData auditData) throws Exception {
		if (auditData != null && auditData.getId() > 0) {
			return auditDataService.findAuditData(auditData.getId());
        } else if (auditData != null) {
            return auditDataService.findAuditDataByAuditAndQuestion(auditData);
        }

        return null;
	}

	private AuditData loadAuditDataQuestion(AuditData auditData) {
		// DA: todo: Look into why the auditQuestion is missing records as hinted by the existing comment below.
		// question might not be fully reloaded with related records
		AuditQuestion auditQuestion = auditDataService.findAuditQuestion(auditData.getQuestion().getId());
		auditData.setQuestion(auditQuestion);
		return auditData;
	}

	private void loadCategoryIfNeeded() {
		if (auditData.getQuestion().getCategory() == null) {
			AuditQuestion dataQuestion = auditDataService.findAuditQuestion(auditData.getQuestion().getId());
			// todo: Investigate changing AuditQuestion.category to eager fetch (the default), to avoid this.
			dataQuestion.setCategory(dataQuestion.getCategory());
			auditData.getQuestion().setCategory(dataQuestion.getCategory());
		}
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

        if (!isCanEditAudit()) {
            return;
        }

		if (isAudit && !isAnnualUpdate) {
			AuditQuestion question = auditDataService.findAuditQuestion(auditData.getQuestion().getId());
			if (question.getOkAnswer() != null && question.getOkAnswer().contains(auditData.getAnswer())
					&& (permissions.isAdmin() || permissions.isAuditor())) {
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

		if (newCopy.getAudit().hasCaoStatus(AuditStatus.Submitted)
				&& (permissions.isPicsEmployee() || permissions.isAuditor())) {
			newCopy.setWasChanged(YesNo.Yes);
		}

		newCopy.setAnswer(auditData.getAnswer());
	}

	private boolean hasAnswerChanged(AuditData newCopy) {
		if (auditData.getAnswer() != null) {
			if (newCopy.getAnswer() == null || !newCopy.getAnswer().equals(auditData.getAnswer())) {
				return true;
			}
		}
		return false;
	}

	private boolean hasCommentChanged(AuditData newCopy) {
		if (auditData.getComment() != null) {
			if (newCopy.getComment() == null || !newCopy.getComment().equals(auditData.getComment())) {
				return true;
			}
		}
		return false;
	}

	private boolean areAllHSEJobRoleQuestionsAnswered(int currentQuestionId, ContractorAccount contractor) {
		if (currentQuestionId == 3669) {
			boolean allInactive = true;
			for (JobRole jobRole : contractor.getJobRoles()) {
				if (jobRole.isActive()) {
					allInactive = false;
				}
			}

			if (contractor.getJobRoles().isEmpty() || allInactive) {
				addActionError(getText("EmployeeGUARD.Error.AtLeastOne.JobRole"));
				return false;
			}
		} else if (currentQuestionId == 3675) {
			for (JobRole role : contractor.getJobRoles()) {
				if (role.isActive() && role.getJobCompetencies().isEmpty()) {
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

					auditDataService.saveAuditData(auditData);
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

					auditDataService.saveAuditData(auditData);
				}
			}
		}

		if (recalcAudit) {
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
		}

	}

	private void checkUniqueCode(ContractorAudit tempAudit) {
        contractorAuditService.updateAuditEffectiveDateBasedOnAnswer(tempAudit, auditData);
        contractorAuditService.updateAuditExpirationDateBasedOnAnswer(tempAudit, auditData);
	}

	/**
	 * @return True if a rule that would be triggered from this question, false
	 *         otherwise
	 */
	private boolean checkOtherRules() {
		for (AuditCategoryRule acr : auditRuleDAO.findCategoryRulesByQuestion(auditData.getQuestion().getId())) {
			if (acr.isMatchingAnswer(auditData)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return True if a dependent question is in a different category, false
	 *         otherwise
	 */
	private boolean checkDependentQuestions() {
		for (AuditQuestion aq : auditData.getQuestion().getDependentQuestions()) {
			if (aq.getCategory() != auditData.getQuestion().getCategory()) {
				return true;
			}
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

	private boolean processAndValidateBasedOnQuestionType(AuditData auditData, AuditData databaseCopy) {

		String questionType = databaseCopy.getQuestion().getQuestionType();
		String answer = auditData.getAnswer();

		if ("ESignature".equals(questionType)) {
			boolean valid = processAndValidateESignature(auditData, databaseCopy, answer);
			if (!valid) {
				return false;
			}
		}

		if ("Tagit".equals(questionType) && "[]".equals(answer)) {
			auditData.setAnswer("");
			return true;
		}

		if ("Date".equals(questionType)) {
			return processAndValidateDate(auditData);
		}

		if ("Money".equals(questionType) || "Decimal Number".equals(questionType) || "Number".equals(questionType)
				|| "Percent".equals(questionType)) {
			boolean valid = processAndValidateNumeric(auditData, databaseCopy, questionType);
			if (!valid) {
				return false;
			}
		}

		if ("Check Box".equals(questionType)) {
			if (answer.equals("false")) {
				auditData.setAnswer("");
			}
		}

		// Null or blank answers are always OK
		if (Strings.isEmpty(answer)) {
			return true;
		}

		return true;
	}

	private boolean processAndValidateNumeric(AuditData auditData, AuditData databaseCopy, String questionType) {
        if (auditData.getAnswer() == null || auditData.getAnswer().equals("")) {
            return true;
        }

        AuditQuestion question = databaseCopy.getQuestion();
		String answer = auditData.getAnswer();

		answer = trimWhitespaceLeadingZerosAndAllCommas(answer);

		boolean hasBadChar = false;
		for (int i = 0; i < answer.length(); i++) {
			char c = answer.charAt(i);
			if (!Character.isDigit(c) && (c != '.') && (c != '-')) {
				hasBadChar = true;
			}
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
		} else if ("Percent".equals(questionType)) {
			format = new DecimalFormat("##0.00");
		} else {
			format = new DecimalFormat("#,##0");
		}

		try {
			BigDecimal value = new BigDecimal(answer);
			if (isInvalidNegativeNumber(value, question)) {
				addActionError(getText("Audit.message.InvalidNegativeNumber"));
				return false;
			} else if (isInvalidPercent(value, question)) {
				addActionError(getText("Audit.message.InvalidPercent"));
				return false;
			}
			auditData.setAnswer(format.format(value));
		} catch (Exception ignore) {
			addActionError(getText("Audit.message.InvalidFormat"));
			return false;
		}
		return true;
	}

	private boolean processAndValidateESignature(AuditData auditData, AuditData databaseCopy, String answer) {

		if (eSignatureName == null && eSignatureTitle == null) {
			setESignatureData(answer);
		}

		if (verifyButton) {
			setESignatureData(databaseCopy.getAnswer());
		}

		if (eSignatureName == null && eSignatureTitle == null) {
			auditData.setAnswer("");
			auditData.setComment("");
			return true;
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
        if (verifyButton) {
            auditData.setComment(databaseCopy.getComment());
        } else {
	    	auditData.setComment(getIP());
        }

		return true;
	}

	private boolean isInvalidPercent(BigDecimal value, AuditQuestion question) {
		if ("Percent".equals(question.getQuestionType()) && value != null) {
			if (value.floatValue() < 0f || value.floatValue() > 100f) {
				return true;
			}
		}
		return false;
	}

	private void setESignatureData(String response) {
		if (!Strings.isEmpty(response) && response.contains(" / ")) {
			String[] esig = response.split(" / ");
			eSignatureName = esig[0];
			eSignatureTitle = esig[1];
		}

	}

	private boolean processAndValidateDate(AuditData auditData) {
		String answer = auditData.getAnswer();
		if (answer == null || answer.length() == 0) {
			return true;
		}

		Date enteredDate = extractEnteredDate(answer);
		int enteredYear;

		if (enteredDate == null
				|| (enteredYear = DateBean.getYearFromDate(enteredDate)) < ANSWER_MIN_YEAR
				|| enteredYear > Calendar.getInstance().get(Calendar.YEAR) + VALID_YEARS_IN_FUTURE) {

			String msg = getText("Audit.message.InvalidDate");
			if (null != msg)
				msg = msg.replaceFirst("(\\b2000\\b|\\{\\d*\\}|#\\d+|%(\\d+\\$)?d)", Integer.toString(ANSWER_MIN_YEAR));
			addActionError(msg);
			return false;
		}

		SimpleDateFormat df = new SimpleDateFormat(supportedDatePatterns[0]);
		auditData.setAnswer(df.format(enteredDate));

		return true;
	}

	private static final String[] supportedDatePatterns = {"yyyy-MM-dd", "yyyy/MM/dd", "MM/dd/yyyy"};

	private static Date extractEnteredDate(String answer) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.setLenient(false);
		for (String pattern : supportedDatePatterns) {
			try {
				dateFormat.applyPattern(pattern);
				return dateFormat.parse(answer);
			} catch (java.text.ParseException e) {
				LOG.info("Could not parse answer date '#0' with pattern '#1': #2", answer, pattern, e.getMessage());
			}
		}
		return null;
	}

    private boolean isInvalidNegativeNumber(BigDecimal value, AuditQuestion question) {
		if (question.getId() == AuditQuestion.EMR && value.floatValue() < 0.0f) {
			return true;
		}

		return false;
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
		if (naics != null) {
			return true;
		}
		return false;
	}

	private String guessNaicsCode(String naics) {
		if (Strings.isEmpty(naics)) {
			return "0";
		}

		if (isValidNAICScode(naics)) {
			return naics;
		}

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

	public SortedMap<Integer, List<InsuranceCriteriaContractorOperator>> getInsuranceCriteriaMap(AuditQuestion question) {
		return InsuranceCriteriaDisplay.getInsuranceCriteriaMap(question, contractor);
	}
}
