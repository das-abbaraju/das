package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.UserAccountRole;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorTable extends AbstractTable {

    public static final String Account = "Account";
    public static final String AccountUser = "AccountUser";
    public static final String CustomerService = "CustomerService";
    public static final String InsideSales = "InsideSales";
    public static final String RecommendedCSR = "RecommendedCSR";
    public static final String PQF = "PQF";
    public static final String Flag = "Flag";
    public static final String FlagCriteriaContractor = "FlagCriteriaContractor";
    public static final String RequestedBy = "RequestedBy";
    public static final String Watch = "Watch";
    public static final String Tag = "Tag";
    public static final String ContractorStatistics = "ContractorStatistics";
    public static final String ContractorTrade = "ContractorTrade";
    public static final String ContractorFee = "ContractorFee";
    public static final String WelcomeCall = "WelcomeCall";

    public ContractorTable() {
        super("contractor_info");
        addFields(ContractorAccount.class);

        addContractorFee(FeeClass.DocuGUARD);
        addContractorFee(FeeClass.AuditGUARD);
        addContractorFee(FeeClass.InsureGUARD);
        addContractorFee(FeeClass.EmployeeGUARD);
    }

    private void addContractorFee(FeeClass feeClass) {
        Field field = new Field(feeClass.toString(), "EXISTS(SELECT * FROM contractor_fee cf WHERE cf.conID = "
                + ReportOnClause.ToAlias + ".id AND cf.currentAmount > 0 AND cf.feeClass = '" + feeClass.toString()
                + "')", FieldType.Boolean);
        field.setCategory(FieldCategory.Billing);
        field.setImportance(FieldImportance.Average);
        field.setWidth(100);
        addField(field);
    }

    protected void addJoins() {
        addRequiredKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id", "id",
                ReportOnClause.ToAlias + ".type = 'Contractor'")));

        ReportForeignKey flagKey = addRequiredKey(new ReportForeignKey(Flag, new ContractorOperatorTable(),
                new ReportOnClause("id", "subID", ReportOnClause.ToAlias + ".genID = " + ReportOnClause.AccountID)));
        flagKey.setMinimumImportance(FieldImportance.Low);

//		ReportForeignKey oldCsrKey = addOptionalKey(new ReportForeignKey(CustomerService, new UserTable(),
//				new ReportOnClause("welcomeAuditor_id")));
//		oldCsrKey.setMinimumImportance(FieldImportance.Average);
//		oldCsrKey.setCategory(FieldCategory.CustomerService);

        ReportForeignKey csr = new ReportForeignKey(CustomerService, new AccountUserTable(), new ReportOnClause("id",
                "accountID", ReportOnClause.ToAlias + ".role = '" + UserAccountRole.PICSCustomerServiceRep + "' AND " +
                ReportOnClause.ToAlias + ".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()"));

        ReportForeignKey csrKey = addOptionalKey(csr);
        csrKey.setCategory(FieldCategory.CustomerService);
        csrKey.setMinimumImportance(FieldImportance.Average);

        ReportForeignKey insideSales = new ReportForeignKey(InsideSales, new AccountUserTable(), new ReportOnClause("id",
                "accountID", ReportOnClause.ToAlias + ".role = '" + UserAccountRole.PICSInsideSalesRep + "' AND " +
                ReportOnClause.ToAlias + ".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()"));
        ReportForeignKey insideSalesRep = addOptionalKey(insideSales);
        insideSalesRep.setCategory(FieldCategory.CustomerService);
        insideSalesRep.setMinimumImportance(FieldImportance.Average);

        ReportForeignKey pqfKey = addOptionalKey(new ReportForeignKey(PQF, new ContractorAuditTable(),
                new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = " + AuditType.PQF)));
        pqfKey.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey welcomeCallKey = addOptionalKey(new ReportForeignKey(WelcomeCall, new ContractorAuditTable(),
                new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = " + AuditType.WELCOME)));
        welcomeCallKey.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey requestedBy = addOptionalKey(new ReportForeignKey(RequestedBy, new AccountTable(),
                new ReportOnClause("requestedByID")));
        requestedBy.setMinimumImportance(FieldImportance.Required);
        requestedBy.setCategory(FieldCategory.RequestingClientSite);

        addOptionalKey(new ReportForeignKey(Watch, new ContractorWatchTable(), new ReportOnClause("id", "conID",
                ReportOnClause.ToAlias + ".userID = " + ReportOnClause.UserID)));

        addOptionalKey(new ReportForeignKey(Tag, new ContractorTagView(), new ReportOnClause("id", "conID",
                ReportOnClause.ToAlias + ".opID IN (" + ReportOnClause.VisibleAccountIDs + ")")));

        addRequiredKey(new ReportForeignKey(ContractorStatistics, new ContractorStatisticsView(), new ReportOnClause(
                "id", "conID")));

        addOptionalKey(new ReportForeignKey(ContractorTrade, new ContractorTradeTable(), new ReportOnClause("id",
                "conID")));

        ReportForeignKey recommendedCsrKey = addOptionalKey(new ReportForeignKey(RecommendedCSR, new UserTable(),
                new ReportOnClause("recommendedCsrID")));
        recommendedCsrKey.setMinimumImportance(FieldImportance.Required);
        recommendedCsrKey.setCategory(FieldCategory.CustomerService);

        addOptionalKey(new ReportForeignKey(FlagCriteriaContractor, new FlagCriteriaContractorTable(),
                new ReportOnClause("id", "conID")));

        addRequiredKey(new ReportForeignKey(ContractorFee, new ContractorFeeTable(), new ReportOnClause("id", "conID")));
    }
}