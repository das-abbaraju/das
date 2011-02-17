package com.picsauditing.actions.rules;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class CategoryRuleSearch extends AuditRuleSearch {

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		tryPermissions(OpPerms.ManageCategoryRules);

		categoryRule = true;
		sql = new SelectSQL("audit_category_rule a_search");
		return super.execute();
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addJoin("LEFT JOIN audit_category ac ON ac.id = a_search.catID");
		sql.addField("ac.name category");
	}

	@Override
	protected void addFilterToSQL() throws Exception {
		super.addFilterToSQL();
		if (filterOn(filter.getCategory())) {
			sql.addWhere("ac.name LIKE '%" + Utilities.escapeQuotes(filter.getCategory()) + "%'");
		}
	}
}
