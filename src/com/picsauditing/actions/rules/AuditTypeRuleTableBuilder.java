package com.picsauditing.actions.rules;

import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditTypeRule;

@SuppressWarnings("serial")
public class AuditTypeRuleTableBuilder extends AuditRuleTableBuilder<AuditTypeRule> {

	public AuditTypeRuleTableBuilder(AuditDecisionTableDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
		this.ruleType = "Audit Type";
	}

	@Override
	public void findRules() {
		if ("lessGranular".equals(button)) {
			rules = ruleDAO.getLessGranular(ruleDAO.findAuditTypeRule(id), date);
		} else if ("moreGranular".equals(button)) {
			rules = ruleDAO.getMoreGranular(ruleDAO.findAuditTypeRule(id), date);
		} else if (id != null) {
			rules.add(ruleDAO.findAuditTypeRule(id));
		}
	}
}
