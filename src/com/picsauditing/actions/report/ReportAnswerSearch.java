package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;

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
		//sql.setPermissions(permissions);
		
		Set<Integer> auditTypes = new HashSet<Integer>();
		
		for (AuditQuestion question : questions) {
			if (question != null && removeQuestion != question.getQuestionID()) {
				AuditQuestion tempQuestion = auditQuestionDAO.find(question.getQuestionID());
				tempQuestion.setCriteria(question.getCriteria());
				tempQuestion.setAnswer(question.getAnswer());
				if (newQuestions.contains(tempQuestion))
					newQuestions.remove(tempQuestion); // remove the old first
				newQuestions.add(tempQuestion);
				auditTypes.add(tempQuestion.getSubCategory().getCategory().getAuditType().getAuditTypeID());
			}
		}
		if (auditTypes.size() > 0)
			sql.addWhere("atype.auditTypeID IN ("+Strings.implode(auditTypes, ",")+")");
			
		
		questions = new ArrayList<AuditQuestion>();
		questions.addAll(newQuestions);
		for (AuditQuestion question : questions) {
			sql.addPQFQuestion(question.getQuestionID());
			if (question.getCriteria() != null && question.getCriteria().length() > 0) {
				String qSearch = "q" + question.getQuestionID() + ".answer ";
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

		if(questions.size() == 0)
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
