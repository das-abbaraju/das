package com.picsauditing.actions.rules;

import com.picsauditing.PICS.Utilities;
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
	}

	@Override
	protected void addFilterToSQL() throws Exception {
		super.addFilterToSQL();
		if (filterOn(filter.getCategory())) {
			sql.addWhere("ac.name LIKE '%" + Utilities.escapeQuotes(filter.getCategory()) + "%'");
		}
	}
}
