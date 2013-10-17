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
    public static final String ContractorOperator = "ContractorOperator";
    public static final String CustomerService = "CustomerService";
    public static final String LastContactedByInsideSales = "LastContactedByInsideSales";
    public static final String InsideSales = "InsideSales";
    public static final String RecommendedCSR = "RecommendedCSR";
    public static final String Flag = "Flag";
    public static final String FlagCriteriaContractor = "FlagCriteriaContractor";
    public static final String RequestedBy = "RequestedBy";
    public static final String Watch = "Watch";
    public static final String SingleTag = "SingleTag";
    public static final String Tag = "Tag";
    public static final String ContractorStatistics = "ContractorStatistics";
    public static final String ContractorTrade = "ContractorTrade";
    public static final String ContractorFee = "ContractorFee";

    public static final String CemexPostEval = "CemexPostEval";
    public static final String PQF = "PQF";
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
        field.setImportance(FieldImportance.Average);
        field.setWidth(100);
        addField(field);
    }

    protected void addJoins() {
        ReportForeignKey account = new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id", "id",
                ReportOnClause.ToAlias + ".type = 'Contractor'"));
        account.setMinimumImportance(FieldImportance.Low);
        addRequiredKey(account);

        ReportForeignKey flagKey = addRequiredKey(new ReportForeignKey(Flag, new ContractorOperatorTable(),
                new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".opID = " + ReportOnClause.AccountID)));
        flagKey.setMinimumImportance(FieldImportance.Low);

        ReportForeignKey contractorOperator = addRequiredKey(new ReportForeignKey(ContractorOperator, new ContractorOperatorTable(),
                new ReportOnClause("id", "subID")));
        contractorOperator.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey csr = new ReportForeignKey(CustomerService, new AccountUserTable(), new ReportOnClause("id",
                "accountID", ReportOnClause.ToAlias + ".role = '" + UserAccountRole.PICSCustomerServiceRep + "' AND " +
                ReportOnClause.ToAlias + ".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()"));

        ReportForeignKey csrKey = addOptionalKey(csr);
        csrKey.setMinimumImportance(FieldImportance.Average);

        addOptionalKey(new ReportForeignKey(LastContactedByInsideSales, new UserTable(), new ReportOnClause("lastContactedByInsideSales")));

        ReportForeignKey insideSales = new ReportForeignKey(InsideSales, new AccountUserTable(), new ReportOnClause("id",
                "accountID", ReportOnClause.ToAlias + ".role = '" + UserAccountRole.PICSInsideSalesRep + "' AND " +
                ReportOnClause.ToAlias + ".startDate < NOW() AND " + ReportOnClause.ToAlias + ".endDate >= NOW()"));
        ReportForeignKey insideSalesRep = addOptionalKey(insideSales);
        insideSalesRep.setMinimumImportance(FieldImportance.Average);

        ReportForeignKey cemex = new ReportForeignKey(CemexPostEval, new ContractorAuditTable(),
                new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = 456"));
        cemex.setMinimumImportance(FieldImportance.Average);
        addRequiredKey(cemex);

        addOptionalKey(new ReportForeignKey(PQF, new ContractorAuditTable(),
                new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = " + AuditType.PQF)));

        addOptionalKey(new ReportForeignKey(WelcomeCall, new ContractorAuditTable(),
                new ReportOnClause("id", "conID", ReportOnClause.ToAlias + ".auditTypeID = " + AuditType.WELCOME)));

        addOptionalKey(new ReportForeignKey(RequestedBy, new AccountTable(),
                new ReportOnClause("requestedByID")));

        ReportForeignKey watch = new ReportForeignKey(Watch, new ContractorWatchTable(), new ReportOnClause("id", "conID",
                ReportOnClause.ToAlias + ".userID = " + ReportOnClause.UserID));
        watch.setMinimumImportance(FieldImportance.Average);
        addOptionalKey(watch);

        ReportForeignKey tag = new ReportForeignKey(SingleTag, new ContractorTagTable(), new ReportOnClause("id", "conID","(("
                + ReportOnClause.ToAlias + ".tagID IN (SELECT id FROM operator_tag WHERE opID IN (SELECT corporateID FROM facilities WHERE opID IN ({CURRENT_ACCOUNTID}) " +
                "OR corporateID IN ({CURRENT_ACCOUNTID}) OR {CURRENT_ACCOUNTID} = 1100 OR opID = {CURRENT_ACCOUNTID}))))"));
        addOptionalKey(tag);

        ReportForeignKey tagView = new ReportForeignKey(Tag, new ContractorTagView(), new ReportOnClause("id", "conID"));
        tagView.setMinimumImportance(FieldImportance.Average);
        addOptionalKey(tagView);

        addRequiredKey(new ReportForeignKey(ContractorStatistics, new ContractorStatisticsView(), new ReportOnClause(
                "id", "conID"))).setMinimumImportance(FieldImportance.Average);

        ReportForeignKey trade = new ReportForeignKey(ContractorTrade, new ContractorTradeTable(), new ReportOnClause("id",
                "conID"));
        addOptionalKey(trade);

        addOptionalKey(new ReportForeignKey(RecommendedCSR, new UserTable(),
                new ReportOnClause("recommendedCsrID")));

        ReportForeignKey flagCriteriaCon = new ReportForeignKey(FlagCriteriaContractor, new FlagCriteriaContractorTable(),
                new ReportOnClause("id", "conID"));
        flagCriteriaCon.setMinimumImportance(FieldImportance.Low);
        addOptionalKey(flagCriteriaCon);

        addRequiredKey(new ReportForeignKey(ContractorFee, new ContractorFeeTable(), new ReportOnClause("id", "conID")));
    }
}