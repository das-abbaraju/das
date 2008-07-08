package com.picsauditing.actions.audits;

import java.util.Date;

import javax.persistence.NoResultException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.YesNo;

public class AuditDataSave extends PicsActionSupport {
	private AuditData auditData = null;
	private AuditDataDAO dao = null;
	private AuditQuestionDAO questionDao = null;

	private int catDataID = 0;
	private AuditCategoryDataDAO catDataDAO;
	private AuditPercentCalculator auditPercentCalculator;

	public AuditDataSave(AuditDataDAO dao, AuditCategoryDataDAO catDataDAO,
			AuditPercentCalculator auditPercentCalculator, AuditQuestionDAO questionDao) {
		this.dao = dao;
		this.catDataDAO = catDataDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.questionDao = questionDao;
	}

	public String execute() throws Exception {

		try {
			if (!forceLogin())
				return LOGIN;

			AuditData newCopy = null;

			try {
				newCopy = dao.findAnswerToQuestion(auditData.getAudit().getId(), auditData.getQuestion()
						.getQuestionID());
			} catch (NoResultException notReallyAProblem) {
			}

			if (newCopy == null) // insert mode
			{
				dao.save(auditData);
			} else // update mode
			{
				if (auditData.getAnswer() != null) // if answer is being set,
				// then we are not currently
				// verifying
				{
					if (auditData.getAnswer() == null || !newCopy.getAnswer().equals(auditData.getAnswer())) {
						newCopy.setDateVerified(null);
						newCopy.setIsCorrect(null);
						newCopy.setVerifiedAnswer(null);
						newCopy.setAnswer(auditData.getAnswer());

						if (newCopy.getAudit().getAuditStatus().equals(AuditStatus.Submitted)) {
							newCopy.setWasChanged(YesNo.Yes);

							AuditQuestion question = questionDao.find(auditData.getQuestion().getQuestionID());

							if (question.getOkAnswer().indexOf(auditData.getAnswer()) == -1) {
								newCopy.setDateVerified(null);
								newCopy.setAuditor(null);
							} else {
								newCopy.setDateVerified(new Date());
								newCopy.setAuditor(getUser());
							}
						}
					}
				} else // we were handed the verification parms instead of the
				// edit parms
				{
					if (auditData.getVerifiedAnswer() != null) {
						newCopy.setVerifiedAnswer(auditData.getVerifiedAnswer());
					}

					if (ActionContext.getContext().getParameters().get("auditData.isCorrect") != null) {
						if (auditData.getIsCorrect() != newCopy.getIsCorrect()) {
							if (auditData.isVerified()) {
								newCopy.setDateVerified(new Date());
							} else {
								newCopy.setDateVerified(null);
							}

							newCopy.setVerified(auditData.isVerified());
						}
					}
				}

				if (auditData.getComment() != null) {
					newCopy.setComment(auditData.getComment());
				}

				dao.save(newCopy);
			}

			// hook to calculation
			// read/update the ContractorAudit and AuditCatData
			if (catDataID > 0) {
				AuditCatData catData = catDataDAO.find(catDataID);
				auditPercentCalculator.updatePercentageCompleted(catData);
			}

			setMessage("Saved");
		} catch (Exception e) {
			e.printStackTrace();
			setMessage("An Error has Occurred");
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
}
