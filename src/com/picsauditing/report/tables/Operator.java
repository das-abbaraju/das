package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.Renderer;

public class Operator extends BaseTable {

	public Operator() {
		super("operators", "o", "a.id = o.id AND a.type in ('Operator','Corporate')");
	}

	protected void addDefaultFields() {
		QueryField operatorName = addField("operatorName", "a.name", FilterType.AccountName).setSuggested();
		Renderer operatorNameLink = new Renderer("FacilitiesEdit.action?operator={0}&type={1}\">{2}", new String[] {
				"accountID", "accountType", "operatorName" });
		operatorName.addRenderer(operatorNameLink);
	}

	public void addFields() {
		addField("operatorIsCorporate", "o.isCorporate", FilterType.Boolean);
		addField("operatorDoContractorsPay", "o.doContractorsPay", FilterType.Boolean);
		addField("operatorCanSeeInsurance", "o.canSeeInsurance", FilterType.Boolean);
		addField("operatorIsUserManualUploaded", "o.IsUserManualUploaded", FilterType.Boolean);
		addField("operatorVerifiedByPICS", "o.verifiedByPics", FilterType.Boolean);
		addField("operatorPrimaryCorporate", "o.primaryCorporate", FilterType.Boolean);
		addField("operatorAutoApproveInsurance", "o.autoApproveInsurance", FilterType.Boolean);
		addField("operatorActivationFee", "o.activationFee", FilterType.Number);
		addField("operatorRequiredTags", "o.requiredTags", FilterType.String);
		addField("operatorDiscountExpiration", "o.discountExpiration", FilterType.Date);
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("insuranceRep", "o.insuranceAuditor_id"));
		addLeftJoin(new Account("parentOperator", "o.parentID"));
		addLeftJoin(new Account("inheritedFlagCriteria", "o.inheritFlagCriteria"));
		addLeftJoin(new Account("inheritedInsuranceCriteria", "o.inheritInsuranceCriteria"));
		addLeftJoin(new Account("reporting", "o.reportingID"));
	}
}
