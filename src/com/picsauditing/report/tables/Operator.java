package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.Renderer;

public class Operator extends BaseTable {

	public Operator() {
		super("operators", "operator", "o", "a.id = o.id AND a.type in ('Operator','Corporate')");
	}

	protected void addDefaultFields() {
		// TODO: We need to find a way to pass the parent prefix to here for us to use.
		QueryField operatorName = addField(prefix + "Name", "a.name", FilterType.AccountName).setSuggested();
		Renderer operatorNameLink = new Renderer("FacilitiesEdit.action?operator={0}&type={1}\">{2}", new String[] {
				"accountID", "accountType", prefix + "Name" });
		operatorName.addRenderer(operatorNameLink);
	}

	public void addFields() {
		addField(prefix + "IsCorporate", alias + ".isCorporate", FilterType.Boolean);
		addField(prefix + "DoContractorsPay", alias + ".doContractorsPay", FilterType.Boolean);
		addField(prefix + "CanSeeInsurance", alias + ".canSeeInsurance", FilterType.Boolean);
		addField(prefix + "IsUserManualUploaded", alias + ".IsUserManualUploaded", FilterType.Boolean);
		addField(prefix + "VerifiedByPICS", alias + ".verifiedByPics", FilterType.Boolean);
		addField(prefix + "PrimaryCorporate", alias + ".primaryCorporate", FilterType.Boolean);
		addField(prefix + "AutoApproveInsurance", alias + ".autoApproveInsurance", FilterType.Boolean);
		addField(prefix + "ActivationFee", alias + ".activationFee", FilterType.Number);
		addField(prefix + "RequiredTags", alias + ".requiredTags", FilterType.String);
		addField(prefix + "DiscountExpiration", alias + ".discountExpiration", FilterType.Date);
	}

	public void addJoins() {
		addLeftJoin(new User("insuranceRep", "o.insuranceAuditor_id"));
		addLeftJoin(new Account("parentOperator", "o.parentID"));
		addLeftJoin(new Account("inheritedFlagCriteria", "o.inheritFlagCriteria"));
		addLeftJoin(new Account("inheritedInsuranceCriteria", "o.inheritInsuranceCriteria"));
		addLeftJoin(new Account("reporting", "o.reportingID"));
	}
}
