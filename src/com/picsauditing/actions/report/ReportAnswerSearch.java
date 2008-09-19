package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

public class ReportAnswerSearch extends ReportContractorAudits {
	protected List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
	protected AuditQuestionDAO auditQuestionDAO;

	public ReportAnswerSearch(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.ContractorDetails);
		List<AuditQuestion> newQuestions = new ArrayList<AuditQuestion>();
		int removeQuestion = -1;
		try {
			removeQuestion = Integer.parseInt(button);
		} catch (Exception e) {
		}
		sql.setPermissions(permissions);
		
		for (AuditQuestion question : questions) {
			if (question != null && removeQuestion != question.getQuestionID()) {
				AuditQuestion tempQuestion = auditQuestionDAO.find(question.getQuestionID());
				tempQuestion.setCriteria(question.getCriteria());
				tempQuestion.setAnswer(question.getAnswer());
				if (newQuestions.contains(tempQuestion))
					newQuestions.remove(tempQuestion); // remove the old first
				newQuestions.add(tempQuestion);
			}
		}
		questions = new ArrayList<AuditQuestion>();
		questions.addAll(newQuestions);
		for (AuditQuestion question : questions) {
			sql.addPQFQuestion(question.getQuestionID());
			if (question.getCriteria() != null && question.getCriteria().length() > 0)
				sql.addWhere("q" + question.getQuestionID() + ".answer " + question.getCriteria() + " '"
						+ question.getAnswer().getAnswer() + "'");
			this.orderBy = "a.name";
			this.run(sql);
		}

		return SUCCESS;
	}

	public void setQuestionSelect(String value) {
		// Do nothing
	}

	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

}
