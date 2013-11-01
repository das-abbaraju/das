package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class QuestionSelect extends PicsActionSupport {
	@Autowired
	protected AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	@Autowired
	protected AuditCategoryRuleCache auditCategoryRuleCache;
	@Autowired
	protected AuditTypeRuleCache auditTypeRuleCache;

	private String questionName;
	Set<AuditQuestion> questions;
	protected Set<AuditCategory> auditCategories = null;

	public String execute() throws Exception {
		questions = new LinkedHashSet<AuditQuestion>();

		String where = "t.effectiveDate < NOW() AND t.expirationDate > NOW()";
		if (permissions.isOperatorCorporate()) {
			Set<Integer> operatorIDs = new HashSet<Integer>();
			operatorIDs.add(permissions.getAccountId());

			if (permissions.isOperator())
				operatorIDs.addAll(permissions.getCorporateParent());

			String auditCatRules = "WHERE include = 0 AND effectiveDate < NOW() AND expirationDate > NOW() "
					+ "AND ((opID IS NULL OR opID IN (" + Strings.implode(operatorIDs, ",") + "))"
					+ " AND catID in (t.id))";

			String auditTypeRules = "WHERE include = 1 AND effectiveDate < NOW() AND expirationDate > NOW() "
					+ "AND (opID IS NULL OR opID IN (" + Strings.implode(operatorIDs, ",")
					+ "))";
			String categoryClause = "SELECT catID FROM audit_category_rule r " + auditCatRules;
			String auditTypeClause = "SELECT auditTypeID FROM audit_type_rule r " + auditTypeRules;

			where += " AND t.categoryID NOT IN (" + categoryClause + ")";
			where += " AND (SELECT auditTypeID " + " FROM audit_category ac " + " WHERE t.categoryID = ac.id) IN (" + auditTypeClause + ")";
		}

		if (!Strings.isEmpty(questionName)) {
			List<AuditQuestion> questionList = auditQuestionDAO.findByTranslatableField(AuditQuestion.class, where, "name",
				"%" + StringUtils.trim(Strings.escapeQuotesAndSlashes(questionName)) + "%", null);
			Collections.sort(questionList, AuditQuestion.getComparator());
			questions.addAll(questionList);
		}

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