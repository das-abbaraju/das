package com.picsauditing.actions.audits;

import javax.persistence.NoResultException;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ReloadQuestion extends PicsActionSupport {
	private AuditData answer;

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


		int questionID = answer.getQuestion().getId();

		try {
			// Try to find the previous version using the passed in auditData
			// record
			int parentAnswerID = answer.getParentAnswer().getId();

			answer = auditDataDAO.findAnswerToQuestion(auditID, questionID, parentAnswerID);
		} catch (NoResultException notReallyAProblem) {
		}
		
		if (answer == null) {
			answer = new AuditData();
			AuditQuestion question = auditQuestionDAO.find(questionID);
			if (question == null) {
				addActionError("Failed to find question");
				return BLANK;
			}
			answer.setQuestion(question);
		}

		return SUCCESS;
	}

	public AuditData getAnswer() {
		return answer;
	}
	
	public void setAnswer(AuditData answer) {
		this.answer = answer;
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
	
	public String getMode() {
		return "Edit";
	}
}
