package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class OperatorTable extends AbstractTable {
	// TODO: This needs to be rewritten as a whole.

	public OperatorTable() {
		super("operators", "operator", "o", "a.id = o.id AND a.type in ('Operator','Corporate')");
	}

	public OperatorTable(String prefix, String alias, String foreignKey) {
		super("operators", prefix, alias, alias + ".id = " + foreignKey);
	}

	public OperatorTable(String alias, String foreignKey) {
		super("operators", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		Field operatorName;
		operatorName = addField(prefix + "Name", "a.name", FilterType.AccountName);
		operatorName.setUrl("FacilitiesEdit.action?operator={accountID}");
		operatorName.setWidth(300);
		addField(prefix + "IsCorporate", alias + ".isCorporate", FilterType.Boolean);
		addField(prefix + "DoContractorsPay", alias + ".doContractorsPay", FilterType.Boolean);
		addField(prefix + "CanSeeInsurance", alias + ".canSeeInsurance", FilterType.Boolean);
		addField(prefix + "IsUserManualUploaded", alias + ".IsUserManualUploaded", FilterType.Boolean);
		addField(prefix + "VerifiedByPICS", alias + ".verifiedByPics", FilterType.Boolean);
		addField(prefix + "PrimaryCorporate", alias + ".primaryCorporate", FilterType.Boolean);
		addField(prefix + "AutoApproveInsurance", alias + ".autoApproveInsurance", FilterType.Boolean);
		addField(prefix + "ActivationFee", alias + ".activationFee", FilterType.Integer);
		addField(prefix + "RequiredTags", alias + ".requiredTags", FilterType.String);
		addField(prefix + "DiscountExpiration", alias + ".discountExpiration", FilterType.Date);
	}

	public void addJoins() {
		addLeftJoin(new UserTable(prefix + "InsuranceRep", alias + ".insuranceAuditor_id"));
		addLeftJoin(new AccountTable(prefix + "ParentOperator", alias + ".parentID"));
		addLeftJoin(new AccountTable(prefix + "InheritedFlagCriteria", alias + ".inheritFlagCriteria"));
		addLeftJoin(new AccountTable(prefix + "InheritedInsuranceCriteria", alias + ".inheritInsuranceCriteria"));
		addLeftJoin(new AccountTable(prefix + "Reporting", alias + ".reportingID"));
	}
}
