package com.picsauditing.actions.rules;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class CategoryRuleSearch extends AuditRuleSearch {

	@Override
	@RequiredPermission(value = OpPerms.ManageCategoryRules)
	public String execute() throws Exception {
		categoryRule = true;
		sql = new SelectSQL("audit_category_rule a_search");
		return super.execute();
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addJoin("LEFT JOIN audit_type daty ON daty.id = a_search.dependentAuditTypeID");
		sql.addField("IFNULL(daty.id, '*') dependentAuditType");
		sql.addField("IFNULL(a_search.dependentAuditStatus, '*') dependentAuditStatus");
	}
}
