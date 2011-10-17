package com.picsauditing.actions.rules;

import java.util.List;

import com.picsauditing.actions.rules.AuditRuleTableBuilder;
import com.picsauditing.jpa.entities.AuditTypeRule;

@SuppressWarnings("serial")
public class AuditTypeRulesChangesAjax extends
		AuditRuleTableBuilder<AuditTypeRule> {

	private String lastRelease;

	@SuppressWarnings("unchecked")
	@Override
	public void findRules() {
		rules = (List<AuditTypeRule>) dao.findWhere(AuditTypeRule.class,
				"updateDate > '" + lastRelease + "'", 0, "updateDate");
	}

	public String getLastRelease() {
		return lastRelease;
	}

	public void setLastRelease(String releaseDate) {
		this.lastRelease = releaseDate;
	}
	public String getUrlPrefix() {
		return "AuditType";
	}
}
