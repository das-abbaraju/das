package com.picsauditing.actions.rules;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditTypeRule;

@SuppressWarnings("serial")
public class AuditTypeRuleTableBuilder extends AuditRuleTableBuilder<AuditTypeRule> {

	public AuditTypeRuleTableBuilder(AuditDecisionTableDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	@Override
	public void findRules() {
		if ("lessGranular".equals(button)) {
			rules = ruleDAO.getLessGranular(ruleDAO.findAuditTypeRule(ruleID), date);
		} else if ("moreGranular".equals(button)) {
			rules = ruleDAO.getMoreGranular(ruleDAO.findAuditTypeRule(ruleID), date);
		}
	}
}
