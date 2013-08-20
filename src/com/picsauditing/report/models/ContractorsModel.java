package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

public class ContractorsModel extends AbstractModel {

	public ContractorsModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractor = new ModelSpec(null, "Contractor");
		{
			ModelSpec account = contractor.join(ContractorTable.Account);
			account.alias = "Account";
			account.join(AccountTable.Contact);
			account.join(AccountTable.Naics);
			account.join(AccountTable.Country);
            account.join(AccountTable.LastLogin);
		}
		contractor.join(ContractorTable.PQF);

        ModelSpec general = contractor.join(ContractorTable.GeneralLiability);
        general.join(ContractorAuditTable.GLEachOccurrence);
        general.join(ContractorAuditTable.GLGeneralAggregate);

        ModelSpec auto = contractor.join(ContractorTable.AutoLiability);
        auto.join(ContractorAuditTable.ALCombinedSingle);

        ModelSpec workers = contractor.join(ContractorTable.WorkersComp);
        workers.join(ContractorAuditTable.WCEachAccident);

        ModelSpec excess = contractor.join(ContractorTable.ExcessLiability);
        excess.join(ContractorAuditTable.EXEachOccurrence);

        ModelSpec employer = contractor.join(ContractorTable.EmployerLiability);
        employer.join(ContractorAuditTable.ELLimit);

        ModelSpec publicProduct = contractor.join(ContractorTable.PublicProductLiability);
        publicProduct.join(ContractorAuditTable.PPLLimit);

        ModelSpec professional = contractor.join(ContractorTable.ProfessionalLiability);
        professional.join(ContractorAuditTable.PROLEachOccurrence);

        ModelSpec contractorTrade = contractor.join(ContractorTable.ContractorTrade);
        contractorTrade.alias = "ContractorTrade";
        ModelSpec directTrade = contractorTrade.join(ContractorTradeTable.Trade);
        directTrade.alias = "DirectTrade";
        ModelSpec trade = directTrade.join(TradeTable.Children);
        trade.alias = "Trade";

        if (permissions.isAdmin()) {
			ModelSpec welcomeCall = contractor.join(ContractorTable.WelcomeCall);
			welcomeCall.join(ContractorAuditTable.SingleCAO);
		}

		if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
			flag.join(ContractorOperatorTable.ForcedByUser);
		}

		ModelSpec csr = contractor.join(ContractorTable.CustomerService);
        csr.minimumImportance = FieldImportance.Required;
        ModelSpec csrUser = csr.join(AccountUserTable.User);
        csrUser.minimumImportance = FieldImportance.Required;

        ModelSpec insideSales = contractor.join(ContractorTable.InsideSales);
        insideSales.minimumImportance = FieldImportance.Required;
        ModelSpec insideSalesUser = insideSales.join(AccountUserTable.User);
        insideSalesUser.minimumImportance = FieldImportance.Required;

        contractor.join(ContractorTable.RecommendedCSR);
		contractor.join(ContractorTable.Watch);
		contractor.join(ContractorTable.Tag);

		return contractor;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
		permissionQueryBuilder.setContractorOperatorAlias("ContractorFlag");

		Filter accountStatusFilter = getValidAccountStatusFilter(filters);

		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.isVisibleTo(permissions)) {
					permissionQueryBuilder.addVisibleStatus(filterStatus);
				}
			}
		}

		return permissionQueryBuilder.buildWhereClause();
	}

	private Filter getValidAccountStatusFilter(List<Filter> filters) {
		for (Filter filter : filters) {
			if (filter.getName().equalsIgnoreCase("AccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        if (permissions.isOperatorCorporate()) {
            Field flagColor = fields.get("ContractorFlagFlagColor".toUpperCase());
            flagColor.setUrl("ContractorFlag.action?id={AccountID}");
        }

        Field accountManager = new Field("AccountManager","Account.id",FieldType.AccountUser);
        accountManager.setVisible(false);
        accountManager.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "JOIN account_user au ON au.accountID = co.genID " +
                "JOIN users u ON au.userID = u.id " +
                "WHERE u.id IN ");
        accountManager.setSuffixValue("");
        fields.put(accountManager.getName().toUpperCase(), accountManager);

        Field clientSite = new Field("ContractorWorksAtClientSite","Account.id",FieldType.Operator);
        clientSite.setVisible(false);
        clientSite.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "WHERE co.genID IN ");
        clientSite.setSuffixValue("");
        fields.put(clientSite.getName().toUpperCase(), clientSite);

        Field reportingClient = new Field("ContractorWorksForReportingClient","Account.id",FieldType.Operator);
        reportingClient.setVisible(false);
        reportingClient.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "JOIN operators o ON o.id = co.genID " +
                "WHERE o.reportingID IN ");
        reportingClient.setSuffixValue("");
        fields.put(reportingClient.getName().toUpperCase(), reportingClient);

        Field supplierDiversity = new Field("ContractorSupplierDiversity","ContractorPQF.id",FieldType.SupplierDiversity);
        supplierDiversity.setVisible(false);
        supplierDiversity.setPrefixValue("SELECT pd.auditID " +
                "FROM pqfdata pd " +
                "WHERE (pd.answer = 'X' OR pd.answer = 'Yes') AND questionID IN ");
        supplierDiversity.setSuffixValue("");
        supplierDiversity.setRequiredJoin("ContractorPQF");
        fields.put(supplierDiversity.getName().toUpperCase(), supplierDiversity);

        Field worksPeriodicIn = new Field("ContractorWorksPeriodicIn","ContractorPQF.id",FieldType.CountrySubdivision);
        worksPeriodicIn.setVisible(false);
        worksPeriodicIn.setPrefixValue("SELECT pd.auditID " +
                "FROM pqfdata pd " +
                "JOIN audit_question aq ON pd.questionID = aq.id AND pd.answer = 'YesPeriodicWithoutOffice' " +
                "WHERE aq.uniqueCode IN ");
        worksPeriodicIn.setSuffixValue("");
        worksPeriodicIn.setRequiredJoin("ContractorPQF");
        fields.put(worksPeriodicIn.getName().toUpperCase(), worksPeriodicIn);

        Field worksPermanentIn = new Field("ContractorWorksPermanentIn","ContractorPQF.id",FieldType.CountrySubdivision);
        worksPermanentIn.setVisible(false);
        worksPermanentIn.setPrefixValue("SELECT pd.auditID " +
                "FROM pqfdata pd " +
                "JOIN audit_question aq ON pd.questionID = aq.id AND pd.answer = 'Yes' " +
                "WHERE aq.uniqueCode IN ");
        worksPermanentIn.setSuffixValue("");
        worksPermanentIn.setRequiredJoin("ContractorPQF");
        fields.put(worksPermanentIn.getName().toUpperCase(), worksPermanentIn);

        Field officeIn = new Field("ContractorOfficeIn","ContractorPQF.id",FieldType.CountrySubdivision);
        officeIn.setVisible(false);
        officeIn.setPrefixValue("SELECT pd.auditID " +
                "FROM pqfdata pd " +
                "JOIN audit_question aq ON pd.questionID = aq.id AND pd.answer = 'YesWithOffice' " +
                "WHERE aq.uniqueCode IN ");
        officeIn.setSuffixValue("");
        officeIn.setRequiredJoin("ContractorPQF");
        fields.put(officeIn.getName().toUpperCase(), officeIn);

        Field GLEachOccurrence = fields.get("ContractorGeneralLiabilityGLEachOccurrenceAnswer".toUpperCase());
        GLEachOccurrence.setType(FieldType.Number);
        GLEachOccurrence.setDatabaseColumnName("REPLACE(" + GLEachOccurrence.getDatabaseColumnName() + ",',','')");

        Field GLGeneralAggregate = fields.get("ContractorGeneralLiabilityGLGeneralAggregateAnswer".toUpperCase());
        GLGeneralAggregate.setType(FieldType.Number);
        GLGeneralAggregate.setDatabaseColumnName("REPLACE(" + GLGeneralAggregate.getDatabaseColumnName() + ",',','')");

        Field ALCombinedSingle = fields.get("ContractorAutoLiabilityALCombinedSingleAnswer".toUpperCase());
        ALCombinedSingle.setType(FieldType.Number);
        ALCombinedSingle.setDatabaseColumnName("REPLACE(" + ALCombinedSingle.getDatabaseColumnName() + ",',','')");

        Field WCEachAccident = fields.get("ContractorWorkersCompWCEachAccidentAnswer".toUpperCase());
        WCEachAccident.setType(FieldType.Number);
        WCEachAccident.setDatabaseColumnName("REPLACE(" + WCEachAccident.getDatabaseColumnName() + ",',','')");

        Field EXEachOccurrence = fields.get("ContractorExcessLiabilityEXEachOccurrenceAnswer".toUpperCase());
        EXEachOccurrence.setType(FieldType.Number);
        EXEachOccurrence.setDatabaseColumnName("REPLACE(" + EXEachOccurrence.getDatabaseColumnName() + ",',','')");

        return fields;
	}
}