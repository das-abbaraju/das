package com.picsauditing.actions.audits;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;

public class AuditDataSave extends AuditActionSupport {
	private static final long serialVersionUID = 1103112846482868309L;
	private AuditData auditData = null;
	private AnswerMap answerMap;
	private AuditQuestionDAO questionDao = null;
	private NaicsDAO naicsDAO;
	private int catDataID = 0;
	private AuditPercentCalculator auditPercentCalculator;
	private AuditBuilder auditBuilder;
	private String mode;

	private boolean toggleVerify = false;

	public AuditDataSave(ContractorAccountDAO accountDAO, AuditDataDAO dao, AuditCategoryDataDAO catDataDao,
			AuditPercentCalculator auditPercentCalculator, AuditQuestionDAO questionDao, ContractorAuditDAO auditDao,
			OshaAuditDAO oshaAuditDAO, NaicsDAO naicsDAO, AuditBuilder auditBuilder) {
		super(accountDAO, auditDao, catDataDao, dao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.questionDao = questionDao;
		this.naicsDAO = naicsDAO;
		this.auditBuilder = auditBuilder;
	}

	public String execute() throws Exception {

		if (catDataID == 0) {
			addActionError("Missing catDataID");
			return BLANK;
		}

		try {
			if (!forceLogin())
				return LOGIN;

			getUser();
			if (auditData.getId() == 0) {
				// insert mode
				AuditQuestion question = questionDao.find(auditData.getQuestion().getId());
				auditData.setQuestion(question);
				if (!checkAnswerFormat(auditData, null))
					return SUCCESS;
			} else {
				// update mode
				AuditData newCopy = auditDataDao.find(auditData.getId());
				if (auditData.getAnswer() != null) {
					// if answer is being set, then
					// we are not currently verifying
					if (auditData.getAnswer() == null || newCopy.getAnswer() == null
							|| !newCopy.getAnswer().equals(auditData.getAnswer())) {

						if (!checkAnswerFormat(auditData, newCopy)) {
							auditData = newCopy;
							return SUCCESS;
						}

						if (!toggleVerify) {
							newCopy.setDateVerified(null);
						}

						newCopy.setAnswer(auditData.getAnswer());
						if (newCopy.getAudit().getAuditType().isHasRequirements()
								&& newCopy.getAudit().getAuditStatus().equals(AuditStatus.Submitted)
								&& permissions.isPicsEmployee()) {
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
			auditData.setAuditColumns(permissions);
			if ("reload".equals(button)) {
				return SUCCESS;
			}
			if (auditData.getQuestion().getId() == 57) {
				if (!isValidNAICScode(auditData.getAnswer())) {
					addActionError("This is not a valid 2007 NAICS code");
				}
			}
			auditData = auditDataDao.save(auditData);

			if (auditData.getAudit() != null) {
				ContractorAudit tempAudit = null;
				if (!auditDao.isContained(auditData.getAudit())) {
					findConAudit();
					tempAudit = conAudit;
				} else
					tempAudit = auditData.getAudit();

				ContractorAccount contractor = tempAudit.getContractorAccount();
				contractor.setNeedsRecalculation(true);
				if (tempAudit.getAuditType().isPqf()) {
					if (auditData.getQuestion().getId() == 57) {
						if (isValidNAICScode(auditData.getAnswer())) {
							contractor.setNaics(new Naics());
							contractor.getNaics().setCode(auditData.getAnswer());
							contractor.setNaicsValid(true);
						} else {
							String guess = guessNaicsCode(auditData.getAnswer());
							contractor.setNaics(new Naics());
							contractor.setNaicsValid(false);
							contractor.getNaics().setCode(guess);
							addActionError("Setting your current NAICS code to " + guess);
						}
					}
				}
				accountDao.save(contractor);

				if (tempAudit.getAuditType() != null && tempAudit.getAuditType().getClassType().isPolicy()) {

					if ("policyExpirationDate".equals(auditData.getQuestion().getUniqueCode())
							&& !StringUtils.isEmpty(auditData.getAnswer())) {
						Date expiresDate = DateBean.parseDate(auditData.getAnswer());
						if(!DateBean.isNullDate(expiresDate))
							tempAudit.setExpiresDate(expiresDate);
						// In case the answer is not a valid date we add 1 year to the policy's creation date.
						if(tempAudit.getExpiresDate() == null) {
							tempAudit.setExpiresDate(DateBean.addMonths(tempAudit.getCreationDate(), 12));
						}
					}
					if ("policyEffectiveDate".equals(auditData.getQuestion().getUniqueCode())
							&& !StringUtils.isEmpty(auditData.getAnswer())) {
						Date creationDate = DateBean.parseDate(auditData.getAnswer());
						if(!DateBean.isNullDate(creationDate))
							tempAudit.setCreationDate(creationDate);
					}

					auditDao.save(tempAudit);
				}
				if (tempAudit.getAuditType().isAnnualAddendum()) {
					if (auditData.getQuestion().getSubCategory().getCategory().getId() == AuditCategory.GENERAL_INFORMATION) {
						auditBuilder.fillAuditCategories(tempAudit, true);
					}
					if (tempAudit.getAuditStatus().isActive()) {
						tempAudit.changeStatus(AuditStatus.Resubmitted, getUser());
						auditDao.save(tempAudit);
					}
				}
			}

			// hook to calculation read/update
			// the ContractorAudit and AuditCatData
			AuditCatData catData = null;

			if (catDataID > 0) {
				catData = catDataDao.find(catDataID);
			} else if (toggleVerify) {
				List<AuditCatData> catDatas = catDataDao.findAllAuditCatData(auditData.getAudit().getId(), auditData
						.getQuestion().getSubCategory().getCategory().getId());

				if (catDatas != null && catDatas.size() != 0) {
					catData = catDatas.get(0);
				}
			}

			if (catData != null) {
				auditPercentCalculator.updatePercentageCompleted(catData, permissions);
				conAudit = auditDao.find(auditData.getAudit().getId());
				auditPercentCalculator.percentCalculateComplete(conAudit, permissions);
			}

			List<Integer> questionIds = new ArrayList<Integer>();
			questionIds.add(auditData.getQuestion().getId());
			if (auditData.getQuestion().getIsRequired().equals("Depends"))
				questionIds.add(auditData.getQuestion().getDependsOnQuestion().getId());
			answerMap = auditDataDao.findAnswers(auditID, questionIds);

		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e.getMessage());
			return BLANK;
		}

		return SUCCESS;
	}

	public String getMode() {
		// When we're adding a tuple, we call audit_cat_question via
		// audit_cat_tuples
		// That page requires mode to be set
		// Since we're always in edit mode when we're adding tuples, I'm going
		// to hard code this
		// We may need to pass it in though
		if ("Verify".equals(mode))
			return "Verify";
		return "Edit";
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

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
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

	public boolean checkAnswerFormat(AuditData auditData, AuditData databaseCopy) {
		// Null or blank answers are always OK
		String answer = auditData.getAnswer();
		if (Strings.isEmpty(answer))
			return true;

		if (databaseCopy == null)
			databaseCopy = auditData;
		String questionType = databaseCopy.getQuestion().getQuestionType();

		if ("Money".equals(questionType) || "Decimal Number".equals(questionType)) {
			// Strip the commas, just in case they are in the wrong place
			// We add them back in later
			answer = answer.replace(",", "");

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

			NumberFormat format = new DecimalFormat("#,##0");
			if ("Decimal Number".equals(questionType))
				format = new DecimalFormat("#,##0.000");
			BigDecimal value = new BigDecimal(answer);
			auditData.setAnswer(format.format(value));
		}

		if ("Date".equals(databaseCopy.getQuestion().getQuestionType())) {
			SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
			Date newDate = DateBean.parseDate(answer);

			if (newDate == null) {
				addActionError("Invalid Date Format");
				return false;
			} else
				auditData.setAnswer(s.format(newDate));
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
