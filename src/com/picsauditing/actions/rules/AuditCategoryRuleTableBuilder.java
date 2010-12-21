package com.picsauditing.actions.rules;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;

@SuppressWarnings("serial")
public class AuditCategoryRuleTableBuilder extends AuditRuleTableBuilder<AuditCategoryRule> {

	public AuditCategoryRuleTableBuilder(AuditDecisionTableDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
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
		} else if (id != null) {
			rules.add(ruleDAO.findAuditCategoryRule(id));
		}
	}
}
