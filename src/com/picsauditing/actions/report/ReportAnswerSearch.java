package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class ReportAnswerSearch extends ReportAccount {
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

		for (AuditQuestion question : questions) {
			if (question != null && removeQuestion != question.getId()) {
				AuditQuestion tempQuestion = auditQuestionDAO.find(question.getId());
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
			sql.addAuditQuestion(question.getId(), question.getSubCategory().getCategory().getAuditType()
					.getAuditTypeID(), true);
			if (question.getCriteria() != null && question.getCriteria().length() > 0) {
				String qSearch = "q" + question.getId() + ".answer ";
				String qCriteria = question.getCriteria() + " '" + question.getAnswer().getAnswer() + "'";
				if (question.getCriteria().equals("Contains"))
					qCriteria = " LIKE '%" + question.getAnswer().getAnswer() + "%'";
				if (question.getCriteria().equals("Begins With"))
					qCriteria = " LIKE '" + question.getAnswer().getAnswer() + "%'";
				if (question.getCriteria().equals("Ends With"))
					qCriteria = " LIKE '%" + question.getAnswer().getAnswer() + "'";
				sql.addWhere(qSearch + qCriteria);
			}
		}

		if (questions.size() == 0)
			return SUCCESS;

		return super.execute();
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
