package com.picsauditing.report.tables;

import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.Renderer;
import com.picsauditing.report.fieldtypes.FilterType;

public class Contractor extends BaseTable {

	public Contractor() {
		super("contractor_info", "c", "a.id = c.id AND a.type = 'Contractor'");
	}

	protected void addDefaultFields() {
		QueryField contractorName = addField("contractorName", "a.name", FilterType.AccountName);
		Renderer contractorNameLink = new Renderer("ContractorView.action?id={0}\">{1}", new String[] { "accountID",
				"contractorName" });
		contractorName.addRenderer(contractorNameLink);
	}

	public void addFields() {
		addField("contractorRiskLevel", "c.riskLevel", FilterType.LowMedHigh);
		addField("contractorSafetyRisk", "c.safetyRisk", FilterType.LowMedHigh);
		addField("contractorProductRisk", "c.productRisk", FilterType.LowMedHigh);

		addField("contractorScore", "c.score", FilterType.Number);
		addField("contractorPaymentExpires", "c.paymentExpires", FilterType.Date);
		addField("contractorPaymentMethod", "c.paymentMethod", FilterType.String);
		addField("contractorCreditCardOnFile", "c.ccOnFile", FilterType.Boolean);
		addField("contractorCreditCardExpiration", "c.ccExpiration", FilterType.Date);
		addField("contractorBalance", "c.balance", FilterType.Number);
		addField("contractorAccountLevel", "c.accountLevel", FilterType.String);
		addField("contractorRenew", "c.renew", FilterType.Boolean);
		addField("contractorMustPay", "c.mustPay", FilterType.Boolean);
		addField("contractorPayingFacilities", "c.payingFacilities", FilterType.Number);
		addField("contractorMembershipDate", "c.membershipDate", FilterType.Date);
		addField("contractorLastUpgradeDate", "c.lastUpgradeDate", FilterType.Date);
		addField("contractorTRIRAverage", "c.trirAverage", FilterType.Number);
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("customerService", "c.welcomeAuditor_id"));
		addLeftJoin(new Account("requestedByOperator", "c.requestedByID"));

		// joinToFlagCriteriaContractor("contractorFlagCriteria", "c.id");
		// leftJoinToEmailQueue("contractorEmail", "c.id");
		// joinToGeneralContractor("contractorOperator", "subID", "c.id");
		// joinToContractorWatch("contractorWatch", "c.id");
	}
}
