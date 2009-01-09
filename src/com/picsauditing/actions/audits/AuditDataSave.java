package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;

public class AuditDataSave extends AuditActionSupport {
	private static final long serialVersionUID = 1103112846482868309L;
	private AuditData auditData = null;
	private AnswerMap answerMap;
	private AuditQuestionDAO questionDao = null;

	private int catDataID = 0;
	private AuditPercentCalculator auditPercentCalculator;

	private boolean toggleVerify = false;

	public AuditDataSave(ContractorAccountDAO accountDAO, AuditDataDAO dao, AuditCategoryDataDAO catDataDao,
			AuditPercentCalculator auditPercentCalculator, AuditQuestionDAO questionDao, ContractorAuditDAO auditDao,
			OshaAuditDAO oshaAuditDAO) {
		super(accountDAO, auditDao, catDataDao, dao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.questionDao = questionDao;
	}

	public String execute() throws Exception {
		if ("testTuple".equals(button)) {
			AuditQuestion question = questionDao.find(auditData.getQuestion().getId());
			auditData.setQuestion(question);
			
			List<Integer> questionIds = new ArrayList<Integer>();
			questionIds.add(auditData.getQuestion().getId());
			answerMap = auditDataDao.findAnswers(auditID, questionIds);
			return "tuple";
		}

		if ("removeTuple".equals(button)) {
			try {
				auditDataDao.remove(auditData.getId());
				addActionMessage("Successfully removed answer group");
			} catch (Exception e) {
				addActionError("Failed to remove answer group");
			}
			return BLANK;
		}
		
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
				
				if (auditData.getParentAnswer() != null && auditData.getParentAnswer().getId() == 0)
					auditData.setParentAnswer(null);
			} else {
				// update mode
				AuditData newCopy = auditDataDao.find(auditData.getId());
				
				if (auditData.getAnswer() != null) {
					// if answer is being set, then
					// we are not currently verifying
					if (auditData.getAnswer() == null 
							|| !newCopy.getAnswer().equals(auditData.getAnswer())) {

						if (!toggleVerify) {
							newCopy.setDateVerified(null);
						}

						newCopy.setAnswer(auditData.getAnswer());

						if (newCopy.getAudit().getAuditStatus().equals(AuditStatus.Submitted)) {
							newCopy.setWasChanged(YesNo.Yes);

							if (!toggleVerify) {
								if (newCopy.getQuestion().getOkAnswer().indexOf(auditData.getAnswer()) == -1) {
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
			auditData.setAuditColumns(getUser());
			auditData = auditDataDao.save(auditData);

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
				auditPercentCalculator.updatePercentageCompleted(catData);
				conAudit = auditDao.find(auditData.getAudit().getId());
				auditPercentCalculator.percentCalculateComplete(conAudit);
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
		
		if ("addTuple".equals(button)) {
			return "tuple";
		}

		return SUCCESS;
	}
	
	public String getMode() {
		// When we're adding a tuple, we call audit_cat_question via audit_cat_tuples
		// That page requires mode to be set
		// Since we're always in edit mode when we're adding tuples, I'm going to hard code this
		// We may need to pass it in though
		return "Edit";
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

}
