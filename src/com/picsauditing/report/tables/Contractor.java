package com.picsauditing.report.tables;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.Renderer;

public class Contractor extends BaseTable {

	public Contractor() {
		super("contractor_info", "c", "a.id = c.id AND a.type = 'Contractor'");
	}

	protected void addDefaultFields() {
		QueryField contractorName = addField("contractorName", "a.name", FilterType.AccountName).setSuggested();
		Renderer contractorNameLink = new Renderer("ContractorView.action?id={0}\">{1}", new String[] { "accountID",
				"contractorName" });
		contractorName.addRenderer(contractorNameLink);
	}

	public void addFields() {
		addField("contractorRiskLevel", "c.riskLevel", FilterType.LowMedHigh).setCategory(FieldCategory.Classification);
		addField("contractorSafetyRisk", "c.safetyRisk", FilterType.LowMedHigh).setCategory(FieldCategory.Classification);
		addField("contractorProductRisk", "c.productRisk", FilterType.LowMedHigh).setCategory(FieldCategory.Classification);

		addField("contractorScore", "c.score", FilterType.Number);
		addField("contractorTRIRAverage", "c.trirAverage", FilterType.Number).setCategory(FieldCategory.SafetyStats);
		
		addField("contractorMembershipDate", "c.membershipDate", FilterType.Date).setCategory(FieldCategory.Billing);
		addField("contractorAccountLevel", "c.accountLevel", FilterType.String).setCategory(FieldCategory.Billing);
		addField("contractorRenew", "c.renew", FilterType.Boolean).setCategory(FieldCategory.Billing);
		addField("contractorPaymentExpires", "c.paymentExpires", FilterType.Date).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorPaymentMethod", "c.paymentMethod", FilterType.String).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorCreditCardOnFile", "c.ccOnFile", FilterType.Boolean).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorCreditCardExpiration", "c.ccExpiration", FilterType.Date).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorBalance", "c.balance", FilterType.Number).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorMustPay", "c.mustPay", FilterType.Boolean).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorPayingFacilities", "c.payingFacilities", FilterType.Number).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
		addField("contractorLastUpgradeDate", "c.lastUpgradeDate", FilterType.Date).setCategory(FieldCategory.Billing).requirePermission(OpPerms.Billing);
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("customerService", "c.welcomeAuditor_id"));
		addLeftJoin(new JoinAccount("requestedByOperator", "c.requestedByID"));

		// joinToFlagCriteriaContractor("contractorFlagCriteria", "c.id");
		// leftJoinToEmailQueue("contractorEmail", "c.id");
		// joinToGeneralContractor("contractorOperator", "subID", "c.id");
		// joinToContractorWatch("contractorWatch", "c.id");
	}
}
