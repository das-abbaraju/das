package com.picsauditing.actions.report;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAnswerSearch extends ReportAccount {
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	
	protected List<AuditQuestion> questions = new ArrayList<AuditQuestion>();
	protected int removeQuestionId;

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ContractorDetails);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		List<AuditQuestion> newQuestions = new ArrayList<AuditQuestion>();
		for (AuditQuestion question : questions) {
			if (question != null && removeQuestionId != question.getId()) {
				AuditQuestion tempQuestion = auditQuestionDAO.find(question.getId());
				tempQuestion.setCriteria(question.getCriteria());
				tempQuestion.setCriteriaAnswer(question.getCriteriaAnswer());
				if (newQuestions.contains(tempQuestion))
					newQuestions.remove(tempQuestion); // remove the old first
				newQuestions.add(tempQuestion);
			}
		}

		questions = new ArrayList<AuditQuestion>();
		questions.addAll(newQuestions);
		for (AuditQuestion question : questions) {
			sql.addAuditQuestion(question.getId(), question.getCategory().getAuditType()
					.getId(), true);
			if (question.getCriteria() != null && question.getCriteria().length() > 0) {
				String qSearch = "q" + question.getId() + ".answer ";
				String qCriteria = question.getCriteria() + " '" + question.getCriteriaAnswer() + "'";
				if (question.getCriteria().equals("Contains"))
					qCriteria = " LIKE '%" + question.getCriteriaAnswer() + "%'";
				if (question.getCriteria().equals("Begins With"))
					qCriteria = " LIKE '" + question.getCriteriaAnswer() + "%'";
				if (question.getCriteria().equals("Ends With"))
					qCriteria = " LIKE '%" + question.getCriteriaAnswer() + "'";
				sql.addWhere(qSearch + qCriteria);
			}
		}
	}

	@Override
	public void run(SelectSQL sql) throws SQLException, IOException {
		run(sql, new ArrayList<SelectSQL>());
	}

	@Override
	public void run(SelectSQL sql, List<SelectSQL> unionSql) throws SQLException, IOException {
		if (questions.size() > 0)
			super.run(sql, unionSql);
	}
	
	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		int i = 30;
		for(AuditQuestion auditQuestion : questions) {
			excelSheet.addColumn(new ExcelColumn("answer"+auditQuestion.getId(), auditQuestion.getName().toString()), i);
			i++;
		}
	}

	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	public int getRemoveQuestionId() {
		return removeQuestionId;
	}

	public void setRemoveQuestionId(int removeQuestionId) {
		this.removeQuestionId = removeQuestionId;
	}

}
