package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.SubscriptionTable;
import com.picsauditing.report.tables.UserTable;

import java.util.List;
import java.util.Map;

public class SubscriptionsModel extends AbstractModel {

	public SubscriptionsModel(Permissions permissions) {
		super(permissions, new SubscriptionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec subscription = new ModelSpec(null, "Subscription");
        subscription.join(SubscriptionTable.Report);

        ModelSpec user = subscription.join(SubscriptionTable.User);
        user.join(UserTable.Account);

		return subscription;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

		return permissionQueryBuilder.buildWhereClause();
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("SubscriptionUserAccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={SubscriptionUserAccountID}");

       return fields;
	}
}
