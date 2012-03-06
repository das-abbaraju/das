package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ContractorRegistrationRequest;
import com.picsauditing.util.PermissionQueryBuilder;

public class QueryRegistrationRequest extends ModelBase {
	public QueryRegistrationRequest() {
		super();
		from = new ContractorRegistrationRequest();
		from.addFields();
		from.addJoins();

		defaultSort = "crr.name";
	}

	@Override
	public String getWhereClause(Permissions permissions) {
		PermissionQueryBuilder builder = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		return builder.toString();
	}
}
