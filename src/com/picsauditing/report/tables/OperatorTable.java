package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class OperatorTable extends AbstractTable {

	public OperatorTable(String parentPrefix, String parentAlias) {
		super("operators", "operator", "o", parentAlias + ".id = o.id AND " + parentAlias
				+ ".type IN ('Operator','Corporate')");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	public OperatorTable(String prefix, String alias, String foreignKey) {
		super("operators", prefix, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.OperatorAccount.class);

		addField(prefix + "ID", alias + ".id", FilterType.Integer).setCategory(FieldCategory.ClientSitePreferences).setWidth(80);
		addField(prefix + "IsCorporate", alias + ".isCorporate", FilterType.Integer).setCategory(FieldCategory.ClientSitePreferences).setWidth(80);

		Field operatorName;
		operatorName = addField(prefix + "Name", "a.name", FilterType.AccountName).setCategory(FieldCategory.ClientSitePreferences);
		operatorName.setUrl("FacilitiesEdit.action?operator={" + prefix + "ID}");
		operatorName.setWidth(300);
	}

	public void addJoins() {
	}
}
