package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ManageEmployees extends AccountActionSupport {

	private AccountDAO accountDAO;
	private OperatorAccountDAO operatorDAO;
	private EmployeeDAO employeeDAO;

	protected int accountID;
	protected Account account;

	protected List<OperatorAccount> operators;
	protected List<Employee> employees;

	protected int employeeID;
	protected Employee employee;

	public ManageEmployees(AccountDAO accountDAO, OperatorAccountDAO operatorDAO, EmployeeDAO employeeDAO) {
		this.accountDAO = accountDAO;
		this.operatorDAO = operatorDAO;
		this.employeeDAO = employeeDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		employee = employeeDAO.find(employeeID);
		if (employee != null)
			account = employee.getAccount();

		if (account == null && accountID > 0)
			account = accountDAO.find(accountID);
		else if (account == null) {
			account = accountDAO.find(permissions.getAccountId());
		}
		accountID = account.getId();

		if (permissions.getAccountId() != accountID)
			permissions.tryPermission(OpPerms.AllOperators);

		this.subHeading = account.getName();

		if (button != null) {

		}

		return SUCCESS;
	}

	public List<OperatorAccount> getOperators() {
		if (operators == null) {
			operators = operatorDAO.findWhere(true, "");
		}

		return operators;
	}

	public List<Employee> getEmployees() {
		if (employees == null) {
			employees = employeeDAO.findByAccount(account.getId());
		}

		return employees;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountId) {
		this.accountID = accountId;
	}

	@Override
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public Employee getEmployee() {
		return employee;
	}

}
