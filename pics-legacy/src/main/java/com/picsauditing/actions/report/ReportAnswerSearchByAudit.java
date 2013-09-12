package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportAnswerSearchByAudit extends ReportContractorAuditOperator {
	public List<AuditQuestion> auditQuestions = new ArrayList<AuditQuestion>();
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ContractorDetails);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql.addWhere("ca.auditTypeID = 81");
		
		if(getFilter().getQuestionIds() == null) {
			for(AuditQuestion auditQuestion : getFilter().getQuestionsByAuditList()) {
				auditQuestions.add(auditQuestion);
			}
		}
		else {
			for(int questionId : getFilter().getQuestionIds()) {
				AuditQuestion question = auditQuestionDao.find(questionId);
				auditQuestions.add(question);
			}	
		}
		
		
		for(AuditQuestion auditQuestion : auditQuestions) {
			int questionId = auditQuestion.getId();
			String join = "JOIN pqfdata q" + questionId + " on q"
			+ questionId + ".auditID = ca.id AND q" + questionId
			+ ".questionID = " + questionId;
			if(!Strings.isEmpty(getFilter().getAnswer())) {
				join += " AND q" + questionId
				+ ".answer LIKE '%" + getFilter().getAnswer() + "%'";
			}
			sql.addJoin(join);
			sql.addField("q" + questionId + ".answer AS answer" + questionId);
		}

		getFilter().setShowTaxID(false);
		getFilter().setShowLocation(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false); 
		getFilter().setShowQuestionAnswer(true);
		getFilter().setShowOperator(true);
		getFilter().setShowCaoOperator(false);
	}

	@Override
	protected void addExcelColumns() {
		super.addExcelColumns();
		int i = 30;
		for (AuditQuestion auditQuestion : auditQuestions) {
			excelSheet.addColumn(new ExcelColumn("answer"
					+ auditQuestion.getId(), auditQuestion.getColumnHeaderOrQuestion()), i);
			i++;
		}
	}

	public List<AuditQuestion> getAuditQuestions() {
		return auditQuestions;
	}

	public void setAuditQuestions(List<AuditQuestion> auditQuestions) {
		this.auditQuestions = auditQuestions;
	}
}
