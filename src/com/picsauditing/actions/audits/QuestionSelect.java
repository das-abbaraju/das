package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class QuestionSelect extends PicsActionSupport {

	private String questionName;
	Set<AuditQuestion> questions;
	protected AuditQuestionDAO auditQuestionDAO = null;
	protected Set<AuditCategory> auditCategories = null;
	protected AuditDecisionTableDAO auditDecisionTableDAO;
	protected AuditCategoryRuleCache auditCategoryRuleCache;
	protected AuditTypeRuleCache auditTypeRuleCache;

	public QuestionSelect(AuditQuestionDAO auditQuestionDAO, AuditDecisionTableDAO auditDecisionTableDAO,
			AuditCategoryRuleCache auditCategoryRuleCache, AuditTypeRuleCache auditTypeRuleCache) {
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditDecisionTableDAO = auditDecisionTableDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		questions = new LinkedHashSet<AuditQuestion>();

		String where = "t.name LIKE '%" + Utilities.escapeQuotes(questionName) + "%' "
				+ "AND t.effectiveDate < NOW() AND t.expirationDate > NOW()";
		if (permissions.isOperatorCorporate()) {
			Set<Integer> operatorIDs = new HashSet<Integer>();
			operatorIDs.add(permissions.getAccountId());

			if (permissions.isOperator())
				operatorIDs.addAll(permissions.getCorporateParent());

			String whereRules = "WHERE include = 1 AND effectiveDate < NOW() AND expirationDate > NOW() "
					+ "AND (operatorAccount IS NULL OR operatorAccount.id IN (" + Strings.implode(operatorIDs, ",")
					+ "))";
			String categoryClause = "SELECT auditCategory FROM AuditCategoryRule r " + whereRules;
			String auditTypeClause = "SELECT auditType FROM AuditTypeRule r " + whereRules;

			where += " AND t.category IN (" + categoryClause + ")";
			where += " AND t.category.auditType IN (" + auditTypeClause + ")";
		}

		List<AuditQuestion> questionList = auditQuestionDAO.findWhere(where);

		Collections.sort(questionList, AuditQuestion.getComparator());
		questions.addAll(questionList);

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

	public Map<AuditType, List<AuditQuestion>> getQuestionMap() {
		Map<AuditType, List<AuditQuestion>> questionMap = new TreeMap<AuditType, List<AuditQuestion>>();
		for (AuditQuestion q : questions) {
			if (questionMap.get(q.getAuditType()) == null)
				questionMap.put(q.getAuditType(), new ArrayList<AuditQuestion>());

			questionMap.get(q.getAuditType()).add(q);
		}

		return questionMap;
	}
}