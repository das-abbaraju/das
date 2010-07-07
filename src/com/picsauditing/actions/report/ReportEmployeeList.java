package com.picsauditing.actions.report;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportEmployeeList extends ReportAccount {
	private Set<Integer> viewableContractors;
	
	public ReportEmployeeList() {
		skipPermissions = true;
		orderByDefault = "a.nameIndex, e.firstName, e.lastName";
	}
	
	@Override
	protected void buildQuery() {
		sql = new SelectAccount();
		
		if (permissions.isOperatorCorporate()) {
			// Add own employees 
			String joinString = "JOIN\n(SELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location " +
					"\nFROM employee e\nWHERE e.accountID = " + permissions.getAccountId();
			
			// Add own contractor's employees
			joinString += "\nUNION\nSELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location" +
					"\nFROM employee e\nJOIN generalcontractors gc ON gc.subID = e.accountID" +
					"\nAND gc.genID = " + permissions.getAccountId();
			
			if (permissions.isOperator()) {
				OperatorAccountDAO opDAO = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
				OperatorAccount op = opDAO.find(permissions.getAccountId());
				
				// Get siblings
				joinString += "\nUNION\nSELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location" +
						"\nFROM employee e" +
						"\nJOIN generalcontractors gc ON gc.subID = e.accountID AND gc.genID IN" +
						"\n(SELECT o.id FROM operators o WHERE o.parentID = " + op.getParent().getId() + ")";
				// Get parent
				joinString += "\nUNION\nSELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location" +
						"\nFROM employee e\nJOIN generalcontractors gc ON gc.subID = e.accountID AND gc.genID = " +
						op.getParent().getId();
			} else { // is Corporate
				joinString += "\nUNION\nSELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location" +
						"\nFROM employee e\nJOIN generalcontractors gc ON gc.subID = e.accountID AND gc.genID IN" +
						"\n(SELECT id FROM operators WHERE parentID = " + permissions.getAccountId() + ")";
				getFilter().setShowOperator(true);
			}
			
			joinString += ") e ON e.accountID = a.id";
			
			sql.addJoin(joinString);
			sql.addGroupBy("a.id, e.id");
		} else { // PICS Administrator - See ALL employees
			sql.addJoin("JOIN employee e ON e.accountID = a.id");
			getFilter().setShowOperator(true);
		}

		sql.addField("e.id AS employeeID");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("e.title");
		sql.addField("e.location");
		sql.addField("a.dbaName");
		
		addFilterToSQL();
	}
	
	@Override
	protected void addFilterToSQL() {
		ReportFilterContractor f = getFilter();
		
		if (filterOn(f.getStartsWith()))
			report.addFilter(new SelectFilter("startsWith", "a.nameIndex LIKE '?%'", f.getStartsWith()));
		
		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + accountName + "%' OR a.id = '" + accountName
					+ "'", accountName));
			sql.addField("a.dbaName");
		}
		
		String types = Strings.implodeForDB(f.getType(), ",");
		if (filterOn(types)) {
			sql.addWhere("a.type IN (" + types + ")");
			setFiltered(true);
		}

		if (filterOn(f.getOperator())) {
			String list = Strings.implode(f.getOperator(), ",");
			sql.addWhere("a.id IN (SELECT subID FROM generalcontractors WHERE genID IN (" + list + ") )");
			setFiltered(true);
		}

		if (filterOn(f.getTaxID(), ReportFilterContractor.DEFAULT_TAX_ID)) {
			if (!f.getTaxID().equals("- Employee Name -"))
				report.addFilter(new SelectFilter("taxID", "CONCAT(e.firstName, ' ', e.lastName) LIKE '%?%'",
						f.getTaxID()));
		}
	}
	
	@Override
	public String execute() throws Exception {
		// Turn off EVERYTHING or use ReportFilterEmployee....
		getFilter().setShowTrade(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowOfficeIn(false);
		//getFilter().setShowTaxID(false); // Using to find employee name
		getFilter().setShowRiskLevel(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowAddress(false);
		getFilter().setShowFlagStatus(false);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowWorkStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowOpertorTagName(false);
		getFilter().setShowConAuditor(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowStatus(false); // Always show active... for now
		getFilter().setShowType(true);
		
		return super.execute();
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		if (!permissions.isOperatorCorporate() && !permissions.isAdmin())
			throw new NoRightsException("Operator, Corporate or PICS Administrator");
	}
	
	public boolean canViewContractor(int conID) {
		// Only for Operators/Corporate
		if (viewableContractors == null || !viewableContractors.contains(conID)) {
			if (viewableContractors == null)
				viewableContractors = new HashSet<Integer>();
			
			ContractorOperatorDAO coDAO = (ContractorOperatorDAO) SpringUtils.getBean("ContractorOperatorDAO");
			ContractorOperator co = coDAO.find(conID, permissions.getAccountId());
			
			if (co != null) {
				viewableContractors.add(conID);
				return true;
			}
			
			if (permissions.isCorporate()) {
				List<ContractorOperator> all = coDAO.findWhere("operatorAccount.parent.id = " 
						+ permissions.getAccountId() + " AND contractorAccount.id = " + conID 
						+ " GROUP BY contractorAccount");
				
				for (ContractorOperator one : all) {
					viewableContractors.add(one.getContractorAccount().getId());
				}
			}
		}
		
		return viewableContractors.contains(conID);
	}
}