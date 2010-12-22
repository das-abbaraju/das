package com.picsauditing.actions.rules;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class AuditCategoryRuleTableBuilder extends AuditRuleTableBuilder<AuditCategoryRule> {

	protected AuditCategoryRule comparisonRule;
	protected OperatorAccountDAO operatorDAO;

	public AuditCategoryRuleTableBuilder(AuditDecisionTableDAO ruleDAO, OperatorAccountDAO operatorDAO) {
		this.ruleDAO = ruleDAO;
		this.operatorDAO = operatorDAO;
		this.ruleType = "Category";
		this.urlPrefix = "Category";
	}

	@Override
	public void checkColumns(AuditCategoryRule rule) {
		super.checkColumns(rule);
		if (rule.getAuditCategory() != null)
			columnMap.put("auditCategory", true);
		if (rule.getRootCategory() != null)
			columnMap.put("rootCategory", true);
	}

	@Override
	public void findRules() {
		if ("lessGranular".equals(button)) {
			rules = ruleDAO.getLessGranular(ruleDAO.findAuditCategoryRule(id), date);
		} else if ("moreGranular".equals(button)) {
			rules = ruleDAO.getMoreGranular(ruleDAO.findAuditCategoryRule(id), date);
		} else if (comparisonRule != null) {
			Set<String> whereClauses = new LinkedHashSet<String>();
			whereClauses.add("(t.effectiveDate < NOW() AND t.expirationDate > NOW())");
			if (comparisonRule.getAuditType() != null) {
				whereClauses.add("t.auditType.id = " + comparisonRule.getAuditType().getId());
			}
			if (comparisonRule.getOperatorAccount() != null) {
				OperatorAccount operator = operatorDAO.find(comparisonRule.getOperatorAccount().getId());
				whereClauses.add("(t.operatorAccount IS NULL OR t.operatorAccount.id IN ("
						+ Strings.implode(operator.getOperatorHeirarchy()) + "))");
			}

			rules = (List<AuditCategoryRule>) ruleDAO.findWhere(AuditCategoryRule.class, Strings.implode(whereClauses,
					" AND "), 0);
			Collections.sort(rules);
			Collections.reverse(rules);
		} else if (id != null) {
			rules.add(ruleDAO.findAuditCategoryRule(id));
		}
	}

	public AuditCategoryRule getComparisonRule() {
		return comparisonRule;
	}

	public void setComparisonRule(AuditCategoryRule comparisonRule) {
		this.comparisonRule = comparisonRule;
	}
}
