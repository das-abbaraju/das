package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

public class QuestionSelect extends PicsActionSupport {
	private String questionName;
	List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
	AuditQuestionDAO auditQuestionDAO = null;

	public QuestionSelect(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() throws Exception {
		questions = auditQuestionDAO.findWhere("question LIKE '%" + Utilities.escapeQuotes(questionName) + "%'");
		return SUCCESS;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public List<AuditQuestion> getQuestions() {
		return questions;
	}

}