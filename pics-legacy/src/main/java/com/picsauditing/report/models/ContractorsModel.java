package com.picsauditing.report.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.SupplierDiversity;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.search.SelectCase;
import com.picsauditing.util.Strings;

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
		ModelSpec pqf = contractor.join(ContractorTable.PQF);

        if (permissions.hasPermission(OpPerms.ContractorLicenseReport)) {
            pqf.join(ContractorAuditTable.CALicenseNumber);
            pqf.join(ContractorAuditTable.CALicenseExpDate);
        }

        ModelSpec contractorTrade = contractor.join(ContractorTable.ContractorTrade);
        contractorTrade.alias = "ContractorTrade";
        contractorTrade.minimumImportance = FieldImportance.Average;
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

        Field contractorTrade = new Field("ContractorTrades", "(SELECT GROUP_CONCAT(tradeID ORDER BY tradeID SEPARATOR ', ') FROM contractor_trade ct " +
                " WHERE Contractor.id = ct.conID)", FieldType.String);
        contractorTrade.setFilterable(false);
        contractorTrade.setWidth(300);
        contractorTrade.setImportance(FieldImportance.Required);
        contractorTrade.setTranslationPrefixAndSuffix("Trade","name");
        contractorTrade.setSeparator(", ");
        fields.put(contractorTrade.getName().toUpperCase(), contractorTrade);

        Field numberOfEmployees = new Field("ContractorNumberOfEmployees", "(SELECT CONCAT('US ', GROUP_CONCAT(ca.auditFor, ': ', fullTime.answer ORDER BY ca.auditFor SEPARATOR ', ')) " +
                "FROM contractor_audit ca " +
                "JOIN pqfdata fullTime ON ca.id = fullTime.auditID AND fullTime.questionID IN (2447) " +
                "WHERE (ca.expiresDate >= NOW() OR ca.expiresDate IS NULL) " +
                "AND ca.conID = Contractor.id)", FieldType.String);
        numberOfEmployees.setWidth(300);
        numberOfEmployees.setImportance(FieldImportance.Required);
        fields.put(numberOfEmployees.getName().toUpperCase(), numberOfEmployees);

        Field selfPerformed = fields.get("ContractorTradeSelfPerformed".toUpperCase());
        selfPerformed.setVisible(false);
        Field manufacture = fields.get("ContractorTradeManufacture".toUpperCase());
        manufacture.setVisible(false);
        Field activityPercent = fields.get("ContractorTradeActivityPercent".toUpperCase());
        activityPercent.setVisible(false);

        Field accountManager = new Field("AccountManager","Account.id",FieldType.AccountUser);
        accountManager.setVisible(false);
        accountManager.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "JOIN account_user au ON au.accountID = co.opID " +
                "JOIN users u ON au.userID = u.id " +
                "WHERE u.id IN ");
        accountManager.setSuffixValue("");
        fields.put(accountManager.getName().toUpperCase(), accountManager);

        Field clientSite = new Field("ContractorWorksAtClientSite","Account.id",FieldType.Operator);
        clientSite.setVisible(false);
        clientSite.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "WHERE co.opID IN ");
        clientSite.setSuffixValue("");
        fields.put(clientSite.getName().toUpperCase(), clientSite);

        Field reportingClient = new Field("ContractorWorksForReportingClient","Account.id",FieldType.Operator);
        reportingClient.setVisible(false);
        reportingClient.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "JOIN operators o ON o.id = co.opID " +
                "WHERE o.reportingID IN ");
        reportingClient.setSuffixValue("");
        fields.put(reportingClient.getName().toUpperCase(), reportingClient);

        Field supplierDiversityFilter = new Field("ContractorSupplierDiversity","ContractorPQF.id",FieldType.SupplierDiversity);
        supplierDiversityFilter.setVisible(false);
        supplierDiversityFilter.setPrefixValue("SELECT pd.auditID " +
                "FROM pqfdata pd " +
                "WHERE (pd.answer = 'X' OR pd.answer = 'Yes') AND questionID IN ");
        supplierDiversityFilter.setSuffixValue("");
        supplierDiversityFilter.setRequiredJoin("ContractorPQF");
        fields.put(supplierDiversityFilter.getName().toUpperCase(), supplierDiversityFilter);

        SelectCase supplierDiversityCase = new SelectCase();
        List<Integer> supplierDiversityQuestions = new ArrayList<>();
        for (SupplierDiversity supplierDiversity : SupplierDiversity.values()) {
            supplierDiversityCase.addCondition("pd.questionID = " + supplierDiversity.getValue(), "'" + supplierDiversity + "'");
            supplierDiversityQuestions.add(supplierDiversity.getValue());
        }

        Field supplierDiversityColumn = new Field("ContractorSupplierDiversityColumn","(SELECT GROUP_CONCAT(" + supplierDiversityCase.toString() +
                " ORDER BY pd.questionID SEPARATOR ', ') " +
                "FROM contractor_audit ca " +
                "JOIN pqfdata pd ON ca.id = pd.auditID AND pd.questionID IN (" + Strings.implode(supplierDiversityQuestions) + ") AND (pd.answer = 'X' OR pd.answer = 'Yes') " +
                "WHERE Contractor.id = ca.conID)",FieldType.SupplierDiversity);
        supplierDiversityColumn.setFilterable(false);
        supplierDiversityColumn.setRequiredJoin("ContractorPQF");
        supplierDiversityColumn.setSeparator(", ");
        supplierDiversityColumn.setTranslationPrefixAndSuffix("Filters.status"," ");
        fields.put(supplierDiversityColumn.getName().toUpperCase(), supplierDiversityColumn);

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

        Field GLEachOccurrence = addInsuranceLimit("ContractorGeneralLiabilityGLEachOccurrenceAnswer",13,2074);
        fields.put(GLEachOccurrence.getName().toUpperCase(), GLEachOccurrence);
        Field GLGeneralAggregate = addInsuranceLimit("ContractorGeneralLiabilityGLGeneralAggregateAnswer",13,2079);
        fields.put(GLGeneralAggregate.getName().toUpperCase(), GLGeneralAggregate);
        Field ALCombinedSingle = addInsuranceLimit("ContractorAutoLiabilityALCombinedSingleAnswer",15,2155);
        fields.put(ALCombinedSingle.getName().toUpperCase(), ALCombinedSingle);
        Field WCEachAccident = addInsuranceLimit("ContractorWorkersCompWCEachAccidentAnswer",14,2149);
        fields.put(WCEachAccident.getName().toUpperCase(), WCEachAccident);
        Field EXEachOccurrence = addInsuranceLimit("ContractorExcessLiabilityEXEachOccurrenceAnswer",16,2161);
        fields.put(EXEachOccurrence.getName().toUpperCase(), EXEachOccurrence);
        Field ELLimit = addInsuranceLimit("ContractorEmployerLiabilityELLimitAnswer",23,14359);
        fields.put(ELLimit.getName().toUpperCase(), ELLimit);
        Field PPLLimit = addInsuranceLimit("ContractorPublicProductLiabilityPPLLimitAnswer",310,10230);
        fields.put(PPLLimit.getName().toUpperCase(), PPLLimit);
        Field PROILimit = addInsuranceLimit("ContractorProfessionalIndemnityPROILimitAnswer",378,11934);
        fields.put(PROILimit.getName().toUpperCase(), PROILimit);

        if (permissions.hasPermission(OpPerms.ContractorLicenseReport)) {
            Field CALicenseExpiration = fields.get("ContractorPQFCALicenseExpDateAnswer".toUpperCase());
            CALicenseExpiration.setType(FieldType.Date);
        }

       return fields;
	}

    private Field addInsuranceLimit(String name, int auditTypeID, int questionID) {
        Field insuranceLimit = new Field(name,
                "(SELECT REPLACE(pd.answer,',','') FROM pqfdata pd " +
                        " JOIN contractor_audit ca ON pd.auditID = ca.id " +
                        " WHERE pd.questionID = " + questionID +
                        " AND ca.conID = Contractor.id " +
                        " AND ca.auditTypeID = " + auditTypeID +
                        " AND (ca.expiresDate > NOW() OR ca.expiresDate IS NULL) " +
                        " LIMIT 1)", FieldType.Number);
        insuranceLimit.setImportance(FieldImportance.Required);
        return insuranceLimit;
    }
}
