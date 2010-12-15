package com.picsauditing.actions.rules;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;

@SuppressWarnings("serial")
public class AuditCategoryRuleTableBuilder extends AuditRuleTableBuilder<AuditCategoryRule> {

	public AuditCategoryRuleTableBuilder(AuditDecisionTableDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Override
	public void findRules() {
		if ("lessGranular".equals(button)) {
			rules = ruleDAO.getLessGranular(ruleDAO.findAuditCategoryRule(ruleID), date);
		} else if ("moreGranular".equals(button)) {
			rules = ruleDAO.getMoreGranular(ruleDAO.findAuditCategoryRule(ruleID), date);
		}
	}
}
