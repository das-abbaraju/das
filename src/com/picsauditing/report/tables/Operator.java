package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class Operator extends BaseTable {

	public Operator() {
		super("operators", "operator", "o", "a.id = o.id AND a.type in ('Operator','Corporate')");
	}

	public Operator(String prefix, String alias, String foreignKey) {
		super("operators", prefix, alias, alias + ".id = " + foreignKey);
	}

	public Operator(String alias, String foreignKey) {
		super("operators", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		// TODO: We need to find a way to pass the parent prefix/alias to here for us to use.
		QueryField operatorName;
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
		addLeftJoin(new User(prefix + "InsuranceRep", alias + ".insuranceAuditor_id"));
		addLeftJoin(new Account(prefix + "ParentOperator", alias + ".parentID"));
		addLeftJoin(new Account(prefix + "InheritedFlagCriteria", alias + ".inheritFlagCriteria"));
		addLeftJoin(new Account(prefix + "InheritedInsuranceCriteria", alias + ".inheritInsuranceCriteria"));
		addLeftJoin(new Account(prefix + "Reporting", alias + ".reportingID"));
	}
}
