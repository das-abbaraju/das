package com.picsauditing.actions.rules;

import java.util.List;

import com.picsauditing.actions.rules.AuditRuleTableBuilder;
import com.picsauditing.jpa.entities.AuditCategoryRule;

@SuppressWarnings("serial")
public class AuditCatRulesChangesAjax extends
		AuditRuleTableBuilder<AuditCategoryRule> {

	private String lastRelease;
	
	public AuditCatRulesChangesAjax() {
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
		rules = (List<AuditCategoryRule>) dao.findWhere(
				AuditCategoryRule.class, "updateDate > '" + lastRelease + "'",
				0, "updateDate");
	}

	public String getLastRelease() {
		return lastRelease;
	}

	public void setLastRelease(String releaseDate) {
		this.lastRelease = releaseDate;
	}
	public String getUrlPrefix() {
		return "Category";
	}
}
