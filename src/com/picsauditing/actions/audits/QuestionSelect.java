package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class QuestionSelect extends PicsActionSupport {
	private String questionName;
	Set<AuditQuestion> questions;
	protected AuditQuestionDAO auditQuestionDAO = null;
	protected Set<AuditCategory> auditCategories = null;
	protected AuditDecisionTableDAO auditDecisionTableDAO;

	public QuestionSelect(AuditQuestionDAO auditQuestionDAO,
			AuditDecisionTableDAO auditDecisionTableDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditDecisionTableDAO = auditDecisionTableDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		questions = new TreeSet<AuditQuestion>();
		List<AuditQuestion> questionList = auditQuestionDAO.findByQuestion(
				questionName, permissions, getAuditCategories());
		questions.addAll(questionList);

		return SUCCESS;
	}

	private Set<AuditCategory> getAuditCategories() {
		if (auditCategories == null) {
			auditCategories = new HashSet<AuditCategory>();
			if (permissions.isOperatorCorporate()) {
				List<AuditCategory> auditCategoryList = auditDecisionTableDAO
						.getCategoriesByOperator(getOperatorAccount(),
								permissions);
				for (AuditCategory auditCategory : auditCategoryList) {
					auditCategories.add(auditCategory);
					auditCategories.addAll(auditCategory.getChildren());
				}
			}
			return auditCategories;
		}

		return auditCategories;
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

	public Map<AuditType, List<AuditQuestion>> getQuestionMap() {
		Map<AuditType, List<AuditQuestion>> questionMap = new TreeMap<AuditType, List<AuditQuestion>>();
		for (AuditQuestion q : questions) {
			if (questionMap.get(q.getAuditType()) == null)
				questionMap.put(q.getAuditType(),
						new ArrayList<AuditQuestion>());

			questionMap.get(q.getAuditType()).add(q);
		}

		return questionMap;
	}
}