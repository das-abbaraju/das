package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.EmployeeSiteDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.JobSiteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.EmployeeSite;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.JobSite;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageEmployees extends AccountActionSupport implements Preparable {

	private AccountDAO accountDAO;
	private EmployeeDAO employeeDAO;
	private JobRoleDAO roleDAO;
	private EmployeeRoleDAO employeeRoleDAO;
	private JobSiteDAO jobSiteDAO;
	private EmployeeSiteDAO employeeSiteDAO;
	private OperatorAccountDAO operatorAccountDAO;

	protected List<OperatorAccount> operators;
	protected List<Employee> employees;

	protected Employee employee;
	protected String ssn;

	protected int roleID;
	protected int siteID;
	protected int operatorID;
	Set<JobRole> unusedJobRoles;

	public ManageEmployees(AccountDAO accountDAO, EmployeeDAO employeeDAO, JobRoleDAO roleDAO,
			EmployeeRoleDAO employeeRoleDAO, EmployeeSiteDAO employeeSiteDAO, OperatorAccountDAO operatorAccountDAO,
			JobSiteDAO jobSiteDAO) {
		this.accountDAO = accountDAO;
		this.employeeDAO = employeeDAO;
		this.roleDAO = roleDAO;
		this.employeeRoleDAO = employeeRoleDAO;
		this.jobSiteDAO = jobSiteDAO;
		this.employeeSiteDAO = employeeSiteDAO;
		this.operatorAccountDAO = operatorAccountDAO;
	}

	@Override
	public void prepare() throws Exception {
		int employeeID = getParameter("employee.id");
		if (employeeID > 0) {
			employee = employeeDAO.find(employeeID);
		}

		if (employee != null) {
			account = employee.getAccount();
		} else {
			int accountID = getParameter("id");
			if (accountID > 0)
				account = accountDAO.find(accountID);
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

		if (account == null)
			throw new RecordNotFoundException("Account " + id + " not found");

		if (permissions.getAccountId() != account.getId())
			permissions.tryPermission(OpPerms.AllOperators);

		this.subHeading = account.getName();

		if ("Add".equals(button)) {
			employee = new Employee();

			return SUCCESS;
		}

		if ("Save".equals(button)) {
			if (employee.getAccount() == null) {
				employee.setAccount(account);
			}

			if (ssn != null) {
				if (ssn.length() == 9)
					employee.setSsn(ssn);
				else if (!ssn.matches("X{5}\\d{4}"))
					addActionError("Invalid social security number entered.");
			}

			employee.setAuditColumns(permissions);

			employeeDAO.save(employee);
		}

		if ("Delete".equals(button)) {
			employeeDAO.remove(employee);
			addActionMessage("Employee " + employee.getDisplayName() + " Successfully Deleted.");
			employee = null;
		}

		if ("addRole".equals(button)) {
			JobRole jobRole = roleDAO.find(roleID);

			if (employee != null && jobRole != null) {
				EmployeeRole e = new EmployeeRole();
				e.setEmployee(employee);
				e.setJobRole(jobRole);
				e.setAuditColumns(permissions);

				if (!employee.getEmployeeRoles().contains(e)) {
					employee.getEmployeeRoles().add(e);
					employeeRoleDAO.save(e);
				} else
					addActionError("Employee already has " + jobRole.getName() + " as a Job Role");
			}

			return SUCCESS;
		}

		if ("removeRole".equals(button)) {
			if (employee != null && roleID > 0) {
				EmployeeRole e = employeeRoleDAO.findByEmployeeAndJobRole(employee.getId(), roleID);

				if (e != null) {
					employee.getEmployeeRoles().remove(e);
					employeeRoleDAO.remove(e);
				} else {
					JobRole jobRole = roleDAO.find(roleID);
					addActionError("Employee does not have " + jobRole.getName() + " as a Job Role");
				}
			}

			return SUCCESS;
		}

		if ("addSite".equals(button)) {
			JobSite jobSite = jobSiteDAO.find(siteID);
			OperatorAccount operator = operatorAccountDAO.find(operatorID);

			if (employee != null && operator != null) {
				EmployeeSite es = new EmployeeSite();
				es.setEmployee(employee);
				es.setOperator(operator);
				es.setAuditColumns(permissions);

				if (jobSite != null)
					es.setJobSite(jobSite);

				employeeSiteDAO.save(es);
			}

			return "sites";
		}

		if ("removeSite".equals(button)) {
			if (employee != null && operatorID > 0) {
				EmployeeSite es = employeeSiteDAO.findByEmployeeAndOperator(employee.getId(), operatorID);

				if (es != null) {
					employee.getEmployeeSites().remove(es);
					employeeSiteDAO.remove(es);
				}
			}

			return "sites";
		}

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getSsn() {
		return Strings.maskSSN(employee.getSsn());
	}

	public void setSsn(String ssn) {
		ssn = ssn.replaceAll("[^X0-9]", "");
		if (ssn.length() <= 9)
			this.ssn = ssn;
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public int getSiteID() {
		return siteID;
	}

	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}

	public int getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(int operatorID) {
		this.operatorID = operatorID;
	}

	public Set<JobRole> getUnusedJobRoles() {
		if (unusedJobRoles == null) {
			unusedJobRoles = new LinkedHashSet<JobRole>(account.getJobRoles());

			for (EmployeeRole employeeRole : employee.getEmployeeRoles()) {
				if (unusedJobRoles.contains(employeeRole.getJobRole()))
					unusedJobRoles.remove(employeeRole.getJobRole());
			}
		}

		return unusedJobRoles;
	}

	public List<OperatorAccount> getOperators() {
		// if contractor employee, return site list of non-corporate sites
		if (employee.getAccount() instanceof ContractorAccount) {
			ContractorAccount contractor = (ContractorAccount) employee.getAccount();

			List<OperatorAccount> returnList = new ArrayList<OperatorAccount>();
			for (ContractorOperator co : contractor.getNonCorporateOperators())
				returnList.add(co.getOperatorAccount());
			
			// trimming return list by used entries
			for(EmployeeSite used : employee.getEmployeeSites())
				if(returnList.contains(used.getOperator()))
					returnList.remove(used.getOperator());
			
			Collections.sort(returnList);
			return returnList;
			// if operator employee return list of self, and if corporate, child
			// facilities
		} else if (employee.getAccount() instanceof OperatorAccount) {
			OperatorAccount operator = (OperatorAccount) employee.getAccount();
			List<OperatorAccount> returnList = new ArrayList<OperatorAccount>();

			if (operator.isCorporate()) {
				returnList.add(operator); // adding self
				for (Facility facility : operator.getCorporateFacilities())
					returnList.add(facility.getOperator());
			} else {
				// just add self
				returnList.add(operator);
			}
			
			Collections.sort(returnList);
			return returnList;
		} else {
			return null;
		}
	}
}
