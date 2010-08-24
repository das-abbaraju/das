package com.picsauditing.actions.auditType;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
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
}
