package com.picsauditing.actions.audits;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

public class AuditDataSave extends AuditActionSupport {

	private static final long serialVersionUID = 1103112846482868309L;
	private AuditData auditData = null;
	private String[] multiAnswer;
	private AnswerMap answerMap;
	private AuditQuestionDAO questionDao = null;
	private NaicsDAO naicsDAO;
	private AuditBuilderController auditBuilder;
	private String mode;
	private boolean toggleVerify = false;

	private AuditPercentCalculator auditPercentCalculator;
	private AuditDecisionTableDAO auditRuleDAO;

	public AuditDataSave(ContractorAccountDAO accountDAO, AuditDataDAO dao, AuditCategoryDataDAO catDataDao,
			AuditQuestionDAO questionDao, ContractorAuditDAO auditDao, CertificateDAO certificateDao,
			OshaAuditDAO oshaAuditDAO, NaicsDAO naicsDAO, AuditBuilderController auditBuilder,
			AuditDecisionTableDAO auditRuleDAO, AuditPercentCalculator auditPercentCalculator,
			AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDAO, auditDao, catDataDao, dao, certificateDao, auditCategoryRuleCache);
		this.auditRuleDAO = auditRuleDAO;
		this.questionDao = questionDao;
		this.naicsDAO = naicsDAO;
		this.auditBuilder = auditBuilder;
		this.auditPercentCalculator = auditPercentCalculator;
	}

	public String execute() throws Exception {

		if (getCategoryID() == 0) {
			addActionError("Missing categoryID");
			return BLANK;
		}

		AuditCatData catData;
		try {
			if (!forceLogin())
				return LOGIN;

			getUser();
			AuditData newCopy = null;
			if (auditData.getId() > 0) {
				newCopy = auditDataDao.find(auditData.getId());
			} else {
				if (auditData.getAudit() == null)
					throw new Exception("Missing Audit");
				if (auditData.getQuestion() == null)
					throw new Exception("Missing Question");
				newCopy = auditDataDao.findAnswerToQuestion(auditData.getAudit().getId(), auditData.getQuestion()
						.getId());
			}

			loadAnswerMap();
			if (newCopy == null) {
				// insert mode
				AuditQuestion question = questionDao.find(auditData.getQuestion().getId());
				auditData.setQuestion(question);
				ContractorAudit audit = auditDao.find(auditData.getAudit().getId());
				auditData.setAudit(audit);
				if (!checkAnswerFormat(auditData, null))
					return SUCCESS;
			} else {
				// update mode
				if (auditData.getAnswer() != null) {
					// if answer is being set, then
					// we are not currently verifying
					if (newCopy.getAnswer() == null || !newCopy.getAnswer().equals(auditData.getAnswer())
							|| (!Utilities.isEmptyArray(multiAnswer) || newCopy.getAnswer() != null)) {

						if (!checkAnswerFormat(auditData, newCopy)) {
							auditData = newCopy;
							return SUCCESS;
						}

						if (!toggleVerify) {
							newCopy.setDateVerified(null);
						}

						newCopy.setAnswer(auditData.getAnswer());
						if (newCopy.getAudit().getAuditType().getWorkFlow().isHasSubmittedStep()
								&& permissions.isPicsEmployee()) {
							if (newCopy.getAudit().hasCaoStatus(AuditStatus.Submitted)) {
								newCopy.setWasChanged(YesNo.Yes);

								if (!toggleVerify) {
									if (newCopy.isRequirementOpen()) {
										newCopy.setDateVerified(null);
										newCopy.setAuditor(null);
									} else {
										newCopy.setDateVerified(new Date());
										newCopy.setAuditor(getUser());
									}
								}
							}
						}
					}
				}
				// we were handed the verification parms
				// instead of the edit parms

				if (toggleVerify) {

					if (newCopy.isVerified()) {
						newCopy.setDateVerified(null);
						newCopy.setAuditor(null);
					} else {
						newCopy.setDateVerified(new Date());
						newCopy.setAuditor(getUser());
					}
				}

				if (auditData.getComment() != null) {
					newCopy.setComment(auditData.getComment());
				}

				auditData = newCopy;
			}

			auditID = auditData.getAudit().getId();
			// Load Dependent questions
			auditData.getQuestion().getDependentRequired();
			auditData.getQuestion().getDependentVisible();
			auditData.setAuditColumns(permissions);
			if ("reload".equals(button)) {
				loadAnswerMap();
				return SUCCESS;
			}
			if (auditData.getQuestion().getId() == 57) {
				if ("0".equals(guessNaicsCode(auditData.getAnswer()))) {
					addActionError("This is not a valid 2007 NAICS code");
				}
			}
			auditDataDao.save(auditData);

			if (auditData.getAudit() != null) {
				ContractorAudit tempAudit = null;
				if (!auditDao.isContained(auditData.getAudit())) {
					findConAudit();
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
							//addActionError("Setting your current NAICS code to " + guess);
						}
					}
				}
				accountDao.save(contractor);

				if ("policyExpirationDate".equals(auditData.getQuestion().getUniqueCode())
						&& !StringUtils.isEmpty(auditData.getAnswer())) {
					Date expiresDate = DateBean.parseDate(auditData.getAnswer());
					if (!DateBean.isNullDate(expiresDate))
						tempAudit.setExpiresDate(expiresDate);
					// In case the answer is not a valid date we add 1 year
					// to the policy's creation date.
					if (tempAudit.getExpiresDate() == null) {
						tempAudit.setExpiresDate(DateBean.addMonths(tempAudit.getCreationDate(), 12));
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

				if (auditData.getQuestion().isRecalculateCategories())
					auditBuilder.fillAuditCategories(auditData);

				// Stop concurrent modification exception
				if (tempAudit.getAuditType().isAnnualAddendum()) {
					boolean updateAudit = false;
					for (ContractorAuditOperator cao : tempAudit.getOperators()) {
						if (cao.getStatus().equals(AuditStatus.Complete)) {
							cao.changeStatus(AuditStatus.Resubmitted, permissions);
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
				auditData = auditDataDao.find(auditData.getId());

				loadAnswerMap();
			}

		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e.getMessage());
			return BLANK;
		}

		if (conAudit == null)
			findConAudit();
		
		if(toggleVerify){
			auditPercentCalculator.percentCalculateComplete(conAudit, true);
			auditDao.save(catData);
		}
		
		// check dependent questions, see if not in same cat
		// check rules to see if other cats get triggered now
		// if either true then run FAC
		if (!contractor.getStatus().isPending()) {
			if (checkDependentQuestions() || checkOtherRules()) {
				auditBuilder.fillAuditCategories(auditData);
				auditPercentCalculator.percentCalculateComplete(conAudit, true);
				auditDao.save(conAudit);
			} else if (catData != null) {
				auditPercentCalculator.updatePercentageCompleted(catData);
				catData.setAuditColumns();
				auditDao.save(catData);
			} else
				addActionError("Error saving answer, please try again.");
		}
		return SUCCESS;
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

	private void loadAnswerMap() {
		List<Integer> questionIds = new ArrayList<Integer>();
		questionIds.add(auditData.getQuestion().getId());
		if (auditData.getQuestion().getRequiredQuestion() != null)
			questionIds.add(auditData.getQuestion().getRequiredQuestion().getId());
		if (auditData.getQuestion().getVisibleQuestion() != null)
			questionIds.add(auditData.getQuestion().getVisibleQuestion().getId());
		answerMap = auditDataDao.findAnswers(auditID, questionIds);
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

	private boolean checkAnswerFormat(AuditData auditData, AuditData databaseCopy) {

		if (databaseCopy == null)
			databaseCopy = auditData;
		String questionType = databaseCopy.getQuestion().getQuestionType();
		String answer = auditData.getAnswer();

		// Clean-up for service questions
		if ("Service".equals(questionType)) {
			if (multiAnswer != null)
				answer = Joiner.on(" ").skipNulls().join(multiAnswer);
			else
				answer = "";
			auditData.setAnswer(answer);
		}

		// Null or blank answers are always OK
		if (Strings.isEmpty(answer))
			return true;

		if ("Money".equals(questionType) || "Decimal Number".equals(questionType) || "Number".equals(questionType)) {
			// Strip the commas, just in case they are in the wrong place
			// We add them back in later
			answer = answer.trim().replace(",", "");

			boolean hasBadChar = false;
			for (int i = 0; i < answer.length(); i++) {
				char c = answer.charAt(i);
				if (!Character.isDigit(c) && (c != '.') && (c != '-'))
					hasBadChar = true;
			}

			if (hasBadChar) {
				addActionError("The answer must be a number.");
				return false;
			}

			if ("Number".equals(questionType)) {
				auditData.setAnswer(answer);
				return true;
			}

			NumberFormat format = new DecimalFormat("#,##0");
			if ("Decimal Number".equals(questionType))
				format = new DecimalFormat("#,##0.000");
			BigDecimal value = new BigDecimal(answer);
			auditData.setAnswer(format.format(value));
		}

		if ("Date".equals(questionType)) {
			SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
			Date newDate = DateBean.parseDate(answer);

			if (newDate == null) {
				addActionError("Invalid Date Format");
				return false;
			} else if (newDate.after(DateBean.parseDate("9999-12-31"))) {
				addActionError("Date Out Of Range");
				return false;
			} else
				auditData.setAnswer(s.format(newDate));
		}

		if ("Check Box".equals(questionType)) {
			if (answer.equals("false"))
				auditData.setAnswer("");
		}

		return true;
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

}