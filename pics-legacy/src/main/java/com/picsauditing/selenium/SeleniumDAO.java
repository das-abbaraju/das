package com.picsauditing.selenium;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;

public class SeleniumDAO {

	private static final String TEST_ACCOUNT_NAME_DELIMITER = "Selenium";
	
	private EmployeeDeleter employeeDeleter = new EmployeeDeleter();
	private UserDeleter userDeleter = new UserDeleter();
	private AccountDeleter accountDeleter = new AccountDeleter();
    private AuditCategoryRuleDeleter auditCategoryRuleDeleter = new AuditCategoryRuleDeleter();
    private AuditTypeRuleDeleter auditTypeRuleDeleter = new AuditTypeRuleDeleter();
	
	public void delete(List<SeleniumDeletable> deletables) throws Exception {
		if (CollectionUtils.isEmpty(deletables)) return;
        deleteFromAuditTypeRules(deletables);
        deleteFromAuditCategoryRules(deletables);
//		deleteFromAccounts(deletables);
        deleteFromContractors(deletables);
        deleteFromClientSites(deletables);
		deleteFromUsers(deletables);
		deleteFromEmployees(deletables);
	}

    private void deleteFromAuditTypeRules(List<SeleniumDeletable> deletables) throws SQLException {
        List<Integer> IDs = getAccountIDNumbersFrom(deletables);
        if (IDs.isEmpty()) return;
        auditTypeRuleDeleter.setIds(IDs).execute();
    }

    private void deleteFromAuditCategoryRules(List<SeleniumDeletable> deletables) throws Exception {
        List<Integer> IDs = getAccountIDNumbersFrom(deletables);
        if (IDs.isEmpty()) return;
        auditCategoryRuleDeleter.setIds(IDs).execute();
    }

    private void deleteFromEmployees(List<SeleniumDeletable> deletables) throws Exception {
		List<Integer> IDs = getEmployeeIDNumbersFrom(deletables);
		if (IDs.isEmpty()) return;
		employeeDeleter.setIds(IDs).execute();
	}

	private void deleteFromUsers(List<SeleniumDeletable> deletables) throws Exception {
		List<Integer> IDs = getUserIDNumbersFrom(deletables);
		if (IDs.isEmpty()) return;
		userDeleter.setIds(IDs).execute();
	}

	private void deleteFromAccounts(List<SeleniumDeletable> deletables) throws Exception {
		List<Integer> IDs = getAccountIDNumbersFrom(deletables);
		if (IDs.isEmpty()) return;
		accountDeleter.setIds(IDs).execute();
	}

    private void deleteFromClientSites(List<SeleniumDeletable> deletables) throws Exception {
        List<Integer> IDs = getClientSiteIDNumbersFrom(deletables);
        if (IDs.isEmpty()) return;
        accountDeleter.setIds(IDs).execute();
    }

    private void deleteFromContractors(List<SeleniumDeletable> deletables) throws Exception {
        List<Integer> IDs = getContractorIDNumbersFrom(deletables);
        if (IDs.isEmpty()) return;
        accountDeleter.setIds(IDs).execute();
    }

    public static List<Integer> getClientSiteIDNumbersFrom(List<SeleniumDeletable> deletable) {
        return getIDNumbers(deletable, "isClientSite");
    }

    public static List<Integer> getContractorIDNumbersFrom(List<SeleniumDeletable> deletable) {
        return getIDNumbers(deletable, "isContractor");
    }

    public static List<Integer> getAccountIDNumbersFrom(List<SeleniumDeletable> deletable) {
		return getIDNumbers(deletable, "isAnAccount");
	}

	public static List<Integer> getUserIDNumbersFrom(List<SeleniumDeletable> deletable) {
		return getIDNumbers(deletable, "isUser");
	}

	public static List<Integer> getEmployeeIDNumbersFrom(List<SeleniumDeletable> deletable) {
		return getIDNumbers(deletable, "isAnEmployee");
	}

	private static List<Integer> getIDNumbers(List<SeleniumDeletable> deletables, String testMethod) {
		List<Integer> IDs = new ArrayList<>();
		for (SeleniumDeletable deleteMe : deletables) {
			try {
				if ((Boolean) SeleniumDeletable.class.getDeclaredMethod(testMethod).invoke(deleteMe))
					IDs.add(deleteMe.getID());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return IDs;
	}

	public List<SeleniumDeletable> availableTestingReferences() {
		List<SeleniumDeletable> deletables = new ArrayList<>();
		deletables.addAll(availableTestingAccounts());
		deletables.addAll(availableTestingEmployees());
		deletables.addAll(availableTestingUsers());
		return deletables;
	}

	private List<SeleniumDeletable> availableTestingAccounts() {
		ArrayList<SeleniumDeletable> accounts = new ArrayList<>();
		for (BasicDynaBean row : basicInformationFetch("accounts", "name", "id", "name", "type")) {
			Account account = new Account();
			account.setId((Integer) row.get("id"));
			account.setName(row.get("name").toString());
			account.setType(row.get("type").toString());
			accounts.add(new SeleniumWrapper(account));
		}
		return accounts;
	}

	private List<SeleniumDeletable> availableTestingEmployees() {
		ArrayList<SeleniumDeletable> employees = new ArrayList<>();
		for (BasicDynaBean row : basicInformationFetch("employee", "firstName", "id", "firstName", "lastName")) {
			Employee emp = new Employee();
			emp.setFirstName(row.get("firstName").toString());
			emp.setLastName(row.get("lastName").toString());
			emp.setId((Integer) row.get("id"));
			employees.add(new SeleniumWrapper(emp));
		}
		return employees;
	}

	private List<SeleniumDeletable> availableTestingUsers() {
		ArrayList<SeleniumDeletable> users = new ArrayList<>();
		for (BasicDynaBean row : basicInformationFetch("users", "name", "id", "name")) {
			User user = new User();
			user.setId((Integer) row.get("id"));
			user.setName(row.get("name").toString());
			users.add(new SeleniumWrapper(user));
		}
		return users;
	}

	private List<BasicDynaBean> basicInformationFetch(String table, String whereColumn, String... resultColumns) {
		try {
			Database db = new Database();
			SelectSQL SQL = new SelectSQL(table);
			for (String column : resultColumns)
				SQL.addField(column);
			SQL.addWhere(whereColumn + " LIKE '" + TEST_ACCOUNT_NAME_DELIMITER + "%'");
			return db.select(SQL.toString(), false);
		} catch (SQLException e) {
			System.out.println("Error in SeleniumDAO!!");
			return new ArrayList<>();
		}
	}
}
