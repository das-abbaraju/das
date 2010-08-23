package com.picsauditing.actions.auditType;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.search.SelectSQL;

public class AuditTypeRuleSearch extends AuditRuleSearch {

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
