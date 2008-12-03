package com.picsauditing.actions.audits;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ReloadQuestion extends PicsActionSupport {
	private AuditQuestion question;

	private int auditID = 0;
	private int questionID = 0;
	private AuditDataDAO auditDataDAO;
	private AuditQuestionDAO auditQuestionDAO;

	public ReloadQuestion(AuditDataDAO dao, AuditQuestionDAO auditQuestionDAO) {
		this.auditDataDAO = dao;
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;

		AuditData data = auditDataDAO.findAnswerToQuestion(auditID, questionID);
		if (data == null)
			question = auditQuestionDAO.find(questionID);
		else {
			question = data.getQuestion();
			question.setAnswer(data);
		}

		return SUCCESS;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

}
