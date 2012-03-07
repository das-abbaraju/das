package com.picsauditing.report.tables;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.Renderer;

public class Contractor extends BaseTable {

	public Contractor() {
		super("contractor_info", "contractor", "c", "a.id = c.id AND a.type = 'Contractor'");
	}

	public Contractor(String prefix, String alias, String foreignKey) {
		super("contractor_info", prefix, alias, alias + ".id = " + foreignKey);
	}

	public Contractor(String alias, String foreignKey) {
		super("contractor_info", alias, alias, alias + ".id = " + foreignKey);
	}

	protected void addDefaultFields() {
		// TODO: We need to find a way to pass the parent prefix to here for us to use.
		QueryField contractorName = addField(prefix + "Name", "a.name", FilterType.AccountName).setSuggested();
		Renderer contractorNameLink = new Renderer("ContractorView.action?id={0}\">{1}", new String[] { "accountID",
				prefix + "Name" });
		contractorName.addRenderer(contractorNameLink);
	}

	public void addFields() {
		addField(prefix + "RiskLevel", alias + ".riskLevel", FilterType.LowMedHigh).setCategory(FieldCategory.Classification);
		addField(prefix + "SafetyRisk", alias + ".safetyRisk", FilterType.LowMedHigh).setCategory(FieldCategory.Classification);
		addField(prefix + "ProductRisk", alias + ".productRisk", FilterType.LowMedHigh).setCategory(FieldCategory.Classification);

		addField(prefix + "Score", alias + ".score", FilterType.Number);
		addField(prefix + "TRIRAverage", alias + ".trirAverage", FilterType.Number).setCategory(FieldCategory.SafetyStats);
		
		addField(prefix + "MembershipDate", alias + ".membershipDate", FilterType.Date).setCategory(FieldCategory.Billing);
		addField(prefix + "AccountLevel", alias + ".accountLevel", FilterType.String).setCategory(FieldCategory.Billing);
		addField(prefix + "Renew", alias + ".renew", FilterType.Boolean).setCategory(FieldCategory.Billing);
		addField(prefix + "PaymentExpires", alias + ".paymentExpires", FilterType.Date).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "PaymentMethod", alias + ".paymentMethod", FilterType.String).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "CreditCardOnFile", alias + ".ccOnFile", FilterType.Boolean).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "CreditCardExpiration", alias + ".ccExpiration", FilterType.Date).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "Balance", alias + ".balance", FilterType.Number).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "MustPay", alias + ".mustPay", FilterType.Boolean).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "PayingFacilities", alias + ".payingFacilities", FilterType.Number).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField(prefix + "LastUpgradeDate", alias + ".lastUpgradeDate", FilterType.Date).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
	}

	public void addJoins() {
		addLeftJoin(new User(prefix + "CustomerService", alias + ".welcomeAuditor_id"));
		addLeftJoin(new Account(prefix + "RequestedByOperator", alias + ".requestedByID"));

		// joinToFlagCriteriaContractor("contractorFlagCriteria", "c.id");
		// leftJoinToEmailQueue("contractorEmail", "c.id");
		// joinToGeneralContractor("contractorOperator", "subID", "c.id");
		// joinToContractorWatch("contractorWatch", "c.id");
	}
}
