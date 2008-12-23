package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

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

public class AuditDataSave extends AuditActionSupport {
	private static final long serialVersionUID = 1103112846482868309L;
	private AuditData auditData = null;
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

		if (catDataID == 0) {
			addActionError("Missing catDataID");
			return SUCCESS;
		}
		try {
			if (!forceLogin())
				return LOGIN;

			AuditData newCopy = null;

			try {
				newCopy = auditDataDao.findAnswerToQuestion(auditData.getAudit().getId(), auditData.getQuestion()
						.getId());
			} catch (NoResultException notReallyAProblem) {
			}

			if (newCopy == null) {
				// insert mode
				auditData.setAuditColumns(getUser());
				auditDataDao.save(auditData);
			} else {
				// update mode
				if (auditData.getAnswer() != null) {
					// if answer is being set,
					// then we are not currently
					// verifying
					if (auditData.getAnswer() == null || !newCopy.getAnswer().equals(auditData.getAnswer())) {

						if (!toggleVerify) {
							newCopy.setDateVerified(null);
						}

						newCopy.setAnswer(auditData.getAnswer());

						if (newCopy.getAudit().getAuditStatus().equals(AuditStatus.Submitted)) {
							newCopy.setWasChanged(YesNo.Yes);

							AuditQuestion question = questionDao.find(auditData.getQuestion().getId());

							if (!toggleVerify) {
								if (question.getOkAnswer().indexOf(auditData.getAnswer()) == -1) {
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
				// we were handed the verification parms instead of the
				// edit parms

				if (toggleVerify == true) {

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
				if (newCopy.getCreationDate() == null)
					newCopy.setCreationDate(new Date());
				if (newCopy.getCreatedBy() == null)
					newCopy.setCreatedBy(this.getUser());
				newCopy.setUpdateDate(new Date());
				newCopy.setUpdatedBy(this.getUser());

				auditDataDao.save(newCopy);
			}

			// hook to calculation
			// read/update the ContractorAudit and AuditCatData
			AuditCatData catData = null;

			if (catDataID > 0) {
				catData = catDataDao.find(catDataID);
			} else if (toggleVerify) {
				List<AuditCatData> catDatas = catDataDao.findAllAuditCatData(auditData.getAudit().getId(), newCopy
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

			auditData = newCopy;
			output = "Saved";
		} catch (Exception e) {
			e.printStackTrace();
			output = "An Error has Occurred";
		}

		return SUCCESS;
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
