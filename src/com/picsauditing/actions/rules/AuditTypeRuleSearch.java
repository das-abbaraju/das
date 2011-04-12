package com.picsauditing.actions.rules;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class AuditTypeRuleSearch extends AuditRuleSearch {

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.ManageAuditTypeRules);

		sql = new SelectSQL("audit_type_rule a_search");
		return super.execute();
	}

	@Override
	public void buildQuery() {
		sql.addJoin("LEFT JOIN audit_type daty ON daty.id = a_search.dependentAuditTypeID");
		sql.addField("IFNULL(daty.id, '*') dependentAuditType");
		sql.addField("IFNULL(a_search.dependentAuditStatus, '*') dependentAuditStatus");
		super.buildQuery();
	}

}
