package com.picsauditing.actions.audits;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class QuestionSelect extends PicsActionSupport {
	private String questionName;
	Set<AuditQuestion> questions;
	AuditQuestionDAO auditQuestionDAO = null;

	public QuestionSelect(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() throws Exception {
		String where = "question LIKE '%" + Utilities.escapeQuotes(questionName) + "%'";
		loadPermissions();
		if (permissions.isOperator() || permissions.isCorporate()) {
			where += " AND subCategory.category.auditType.id IN (" + Strings.implode(permissions.getCanSeeAudit(), ",") + ")";
		}
		questions = new TreeSet<AuditQuestion>();
		List<AuditQuestion> questionList = auditQuestionDAO.findWhere(where);
		for(AuditQuestion q : questionList)
			questions.add(q);
		return SUCCESS;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public Set<AuditQuestion> getQuestions() {
		return questions;
	}

}