package com.picsauditing.actions.employees;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ManageEmployeeSites extends AccountActionSupport implements Preparable {
	protected EmployeeDAO employeeDAO;
	protected EmployeeSiteDAO employeeSiteDAO;
	protected AccountDAO accountDAO;
	protected ContractorOperatorDAO contractorOperatorDAO;
	protected ContractorAccountDAO contractorAccountDAO;
	
	protected Employee employee;
	protected List<EmployeeSite> employeeSites;
	protected List<OperatorAccount> operators;

	public ManageEmployeeSites(EmployeeDAO employeeDAO, EmployeeSiteDAO employeeSiteDAO, AccountDAO accountDAO,
			ContractorOperatorDAO contractorOperatorDAO, ContractorAccountDAO contractorAccountDAO) {
		this.employeeDAO = employeeDAO;
		this.employeeSiteDAO = employeeSiteDAO;
		this.accountDAO = accountDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	@Override
	public void prepare() throws Exception {
		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		if (employee == null && account == null) {
			account = accountDAO.find(permissions.getAccountId());
		}

		if (permissions.getAccountId() != employee.getAccount().getId())
			permissions.tryPermission(OpPerms.AllOperators);
		
		this.subHeading = employee.getDisplayName();
		
		if("Save".equals(button)){
//			if (role.getAccount() == null) {
//				role.setAccount(account);
//			}
//
//			if (Strings.isEmpty(role.getName())) {
//				addActionError("Name is required");
//				return SUCCESS;
//			}
//
//			jobRoleDAO.save(role);
		}
		
		if("Remove".equals(button)){
//			addActionMessage("Role " + role.getName() + " Successfully Deleted.");
//			jobRoleDAO.remove(role);
//			role = null;
//			
//			return redirect("ManageJobRoles.action?id=" + account.getId());
		}

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<EmployeeSite> getEmployeeSites() {
		if (employeeSites == null)
			employeeSites = employeeSiteDAO.findSitesByEmployee(employee);
		return employeeSites;
	}

	public void setEmployeeSites(List<EmployeeSite> employeeSites) {
		this.employeeSites = employeeSites;
	}

	public List<OperatorAccount> getOperatorsForContractor() {
//		contractorAccountDAO.
//		employee.contractorOperatorDAO.findByContractor(employee.getAccount().getId(), permissions);
		return null;
	}

	public List<OperatorAccount> getOperatorsForOperator() {
//		contractorAccountDAO.
//		employee.contractorOperatorDAO.findByContractor(employee.getAccount().getId(), permissions);
		return null;
	}

	public List<OperatorAccount> getOperatorsForCorporate() {
//		contractorAccountDAO.
//		employee.contractorOperatorDAO.findByContractor(employee.getAccount().getId(), permissions);
		return null;
	}

	
	public List<EmployeeSite> getSitesByOperator(OperatorAccount operator) {
		return employeeSiteDAO.findSitesByOperator(operator);
	}
}
