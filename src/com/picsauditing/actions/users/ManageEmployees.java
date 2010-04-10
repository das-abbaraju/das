package com.picsauditing.actions.users;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountEmployee;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

public class ManageEmployees extends AccountActionSupport implements Preparable {

	private AccountDAO accountDAO;
	private OperatorAccountDAO operatorDAO;
	private EmployeeDAO employeeDAO;

	protected Account account;

	protected List<OperatorAccount> operators;
	protected List<Employee> employees;

	protected Employee employee;
	protected AccountEmployee accountEmployee;

	private String ssn;

	public ManageEmployees(AccountDAO accountDAO, OperatorAccountDAO operatorDAO, EmployeeDAO employeeDAO) {
		this.accountDAO = accountDAO;
		this.operatorDAO = operatorDAO;
		this.employeeDAO = employeeDAO;
	}

	@Override
	public void prepare() throws Exception {

		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}

		int accountID = getParameter("account.id");
		if (accountID > 0)
			account = accountDAO.find(accountID);

		if (employee != null) {
			for (AccountEmployee accountEmployee : employee.getAccounts()) {
				if (accountEmployee.getAccount().equals(account))
					this.accountEmployee = accountEmployee;
			}
		}

	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.DevelopmentEnvironment);

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		if (account == null) {
			account = accountDAO.find(permissions.getAccountId());
		}

		if (permissions.getAccountId() != account.getId())
			permissions.tryPermission(OpPerms.AllOperators);

		this.subHeading = account.getName();

		if ("Add".equals(button)) {
			employee = new Employee();

			return SUCCESS;
		}

		if ("Save".equals(button)) {
			if (accountEmployee != null && !employee.getAccounts().contains(accountEmployee)) {
				accountEmployee.setEmployee(employee);
				accountEmployee.setAccount(account);
				employee.getAccounts().add(accountEmployee);
			}

			if (ssn != null)
				employee.setSsn(ssn);

			employeeDAO.save(employee);
		}

		if ("Delete".equals(button)) {
			employeeDAO.remove(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully Deleted.");
			employee = null;
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
			employees = employeeDAO.findByAccount(account);
		}

		return employees;
	}

	@Override
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public AccountEmployee getAccountEmployee() {
		return accountEmployee;
	}

	public void setAccountEmployee(AccountEmployee accountEmployee) {
		this.accountEmployee = accountEmployee;
	}

	public String getSsn() {
		if (ssn == null)
			ssn = Strings.maskSSN(employee.getSsn());
		return ssn;
	}

	public void setSsn(String ssn) {
		ssn = ssn.replaceAll("[^0-9]", "");
		if (ssn.length() == 9)
			this.ssn = ssn;
	}
}
