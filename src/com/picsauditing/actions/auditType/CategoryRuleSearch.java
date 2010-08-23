package com.picsauditing.actions.auditType;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;


@SuppressWarnings("serial")
public class CategoryRuleSearch extends AuditRuleSearch {
	
	@Override
	public String execute() throws Exception{
		if (!forceLogin())
			return LOGIN;	
		if(!permissions.hasPermission(OpPerms.ManageCategoryRules))
			throw new NoRightsException(OpPerms.ManageCategoryRules, OpType.View);

		sql =  new SelectSQL("audit_category_rule a_search");
		actionUrl = "CategoryRuleEditor.action?id=";
		filter.setShowCategory(true);
		return super.execute();
	}
	
	@Override
	public void buildQuery(){
		super.buildQuery();
		sql.addField("IFNULL(ac.name,'*') category");
		sql.addJoin("LEFT JOIN audit_category ac ON ac.id = a_search.catID");
	}
	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();
		if(filterOn(filter.getCategory())){
			report.addFilter(new SelectFilter("category", "ac.name = '?'", String.valueOf(filter.getCategory())));
		}
	}
}
