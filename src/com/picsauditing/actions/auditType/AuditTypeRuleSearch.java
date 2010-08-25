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
public class AuditTypeRuleSearch extends AuditRuleSearch {

	public AuditTypeRuleSearch(AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCatDao, OperatorAccountDAO operator,
			OperatorTagDAO opTagDao) {
		super(auditTypeDao, auditCatDao, operator, opTagDao);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String execute() throws Exception{
		if (!forceLogin())
			return LOGIN;	
		if(!permissions.hasPermission(OpPerms.ManageAuditTypeRules))
			throw new NoRightsException(OpPerms.ManageAuditTypeRules, OpType.View);
		

		sql =  new SelectSQL("audit_type_rule a_search");
		actionUrl = "AuditTypeRuleEditor.action?id=";
		filter.setShowCategory(false);
		return super.execute();
	}
	
	@Override
	public void buildQuery(){
		sql.addField("IFNULL(a_search.dependentAuditStatus, '*') dependentAuditStatus");
		sql.addField("IFNULL(daty.auditName, '*') dependentAuditType");
		sql.addJoin("LEFT JOIN audit_type daty ON daty.id = a_search.dependentAuditTypeID");
		super.buildQuery();
	}
	
	@Override
	protected void addFilterToSQL() throws Exception {
		super.addFilterToSQL();
		if(filterOn(filter.getDependentAuditStatus()) && filter.getDependentAuditStatus()>0){
			report.addFilter(new SelectFilter("auditStatus", "a_search.dependentAuditStatus = ?", String.valueOf(filter.getDependentAuditStatus())));
		}
	}
}
