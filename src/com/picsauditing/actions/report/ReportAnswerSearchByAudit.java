package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAnswerSearchByAudit extends ReportAccount {
	protected List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
	protected AuditQuestionDAO auditQuestionDAO;
	protected String removeQuestionId;

	public ReportAnswerSearchByAudit(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ContractorDetails);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addWhere("ca.auditTypeID = 81");
		sql.addField("ca.auditStatus");
		sql.addField("ca.completedDate");
		List<AuditQuestion> newQuestions = new ArrayList<AuditQuestion>();
		int removeQuestion = -1;
		try {
			if (!Strings.isEmpty(removeQuestionId))
				removeQuestion = Integer.parseInt(removeQuestionId);
		} catch (Exception e) {
		}

		for (AuditQuestion question : questions) {
			if (question != null && removeQuestion != question.getId()) {
				AuditQuestion tempQuestion = auditQuestionDAO.find(question
						.getId());
				tempQuestion.setCriteriaAnswer(question.getCriteriaAnswer());
				if (newQuestions.contains(tempQuestion))
					newQuestions.remove(tempQuestion); // remove the old first
				newQuestions.add(tempQuestion);
			}
		}

		questions = new ArrayList<AuditQuestion>();
		questions.addAll(newQuestions);

		if (questions.size() == 0) {
			for (AuditQuestion auditQuestion : getQuestionsByAudit()) {
				if (auditQuestion.getQuestionType().contains("Yes/No")) 
					auditQuestion.setCriteriaAnswer("No");
				else
					auditQuestion.setCriteriaAnswer("");
				questions.add(auditQuestion);
			}
		}

		for (AuditQuestion question : questions) {
			int questionID = question.getId();
			String join = "LEFT JOIN pqfdata q" + questionID + " on q"
					+ questionID + ".auditID = ca.id AND q" + questionID
					+ ".questionID = " + questionID + " AND q" + questionID
					+ ".answer LIKE '%" + question.getCriteriaAnswer() + "%'";
			sql.addJoin(join);
			sql.addField("q" + questionID + ".answer AS answer" + questionID);
		}
	}

	@Override
	public void run(SelectSQL sql) throws SQLException, IOException {
		if (questions.size() > 0)
			super.run(sql);
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		int i = 30;
		for (AuditQuestion auditQuestion : questions) {
			excelSheet.addColumn(new ExcelColumn("answer"
					+ auditQuestion.getId(), auditQuestion.getQuestion()), i);
			i++;
		}
	}

	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	public String getRemoveQuestionId() {
		return removeQuestionId;
	}

	public void setRemoveQuestionId(String removeQuestionId) {
		this.removeQuestionId = removeQuestionId;
	}

	public List<AuditQuestion> getQuestionsByAudit() {
		return auditQuestionDAO
				.findWhere("t.subCategory.category.auditType.id = 81 AND t.isVisible = 'Yes'");
	}
}
