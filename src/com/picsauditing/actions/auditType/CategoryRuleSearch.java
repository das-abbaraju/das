package com.picsauditing.actions.auditType;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;


@SuppressWarnings("serial")
public class CategoryRuleSearch extends AuditRuleSearch {
	
	public CategoryRuleSearch(AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCatDao, OperatorAccountDAO operator,
			OperatorTagDAO opTagDao) {
		super(auditTypeDao, auditCatDao, operator, opTagDao);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute() throws Exception{
		if (!forceLogin())
			return LOGIN;	
		if(!permissions.hasPermission(OpPerms.ManageCategoryRules))
			throw new NoRightsException(OpPerms.ManageCategoryRules, OpType.View);

		filter.setShowDependentAuditStatus(false);
		filter.setShowDependentAuditType(false);
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
	protected void addFilterToSQL() throws Exception {
		super.addFilterToSQL();
		if(filterOn(filter.getCatID()) && filter.getCatID()>0){
			report.addFilter(new SelectFilter("category", "ac.id = ?", String.valueOf(filter.getCatID())));
		}
	}
	
	@Override
	protected void checkFields() {
		super.checkFields();
		if(filter.getCatID()>0)
			filter.setCategory(auditCatDao.find(filter.getCatID()).getName());
	}
}
