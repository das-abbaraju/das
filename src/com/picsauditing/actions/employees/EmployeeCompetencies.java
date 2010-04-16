package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends AccountActionSupport {
	protected ContractorAccountDAO conDAO;
	protected EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected EmployeeRoleDAO erDAO;
	protected OperatorCompetencyDAO opCompDAO;

	protected int conID;
	protected int employeeID;
	protected int ecID;
	protected int jobRoleID;
	protected boolean canEdit = false;
	protected boolean checked;

	protected ContractorAccount contractor;
	protected Employee employee = null;
	protected List<Employee> employees;
	protected List<EmployeeRole> roles = null;
	protected int[] selectedCompetencies;

	protected DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public EmployeeCompetencies(ContractorAccountDAO conDAO, EmployeeDAO employeeDAO, EmployeeCompetencyDAO ecDAO,
			EmployeeRoleDAO erDAO, OperatorCompetencyDAO opCompDAO) {
		this.conDAO = conDAO;
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
		this.erDAO = erDAO;
		this.opCompDAO = opCompDAO;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor()) {
			// Contractors should view and edit the competencies
			canEdit = true;
			conID = permissions.getAccountId();
		}

		if (permissions.isAdmin())
			canEdit = true;

		if (conID == 0)
			throw new RecordNotFoundException("Missing conID");

		contractor = conDAO.find(conID);

		if (employeeID > 0)
			employee = employeeDAO.find(employeeID);

		if (jobRoleID > 0)
			roles = erDAO.findByJobRole(jobRoleID);

		if (button != null) {
			if (button.equalsIgnoreCase("AddSkill")) {
				// A checkbox has been checked
				if (ecID > 0) {
					EmployeeCompetency ec = ecDAO.find(ecID);
					ec.setSkilled(true);
					ec.setAuditColumns(permissions);
					ecDAO.save(ec);
					json.put("title", "Added Skill");
					json.put("msg", "Successfully added " + ec.getCompetency().getLabel() + " skill to "
							+ ec.getEmployee().getDisplayName());
					return JSON;
				} else
					addActionError("Missing employee competency ID");
			}

			if (button.equalsIgnoreCase("RemoveSkill")) {
				// A checkbox has been unchecked
				if (ecID > 0) {
					EmployeeCompetency ec = ecDAO.find(ecID);
					ec.setSkilled(false);
					ec.setAuditColumns(permissions);
					ecDAO.save(ec);
					json.put("title", "Removed Skill");
					json.put("msg", "Successfully removed " + ec.getCompetency().getLabel() + " skill from "
							+ ec.getEmployee().getDisplayName());
					return JSON;
				} else
					addActionError("Missing employee competency ID");
			}

			if (button.equalsIgnoreCase("Update List")) {
				if (selectedCompetencies == null || selectedCompetencies.length == 0)
					addActionError("Please select competencies");
			}
		}

		return SUCCESS;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

	public int getEcID() {
		return ecID;
	}

	public void setEcID(int ecID) {
		this.ecID = ecID;
	}

	public int getJobRoleID() {
		return jobRoleID;
	}

	public void setJobRoleID(int jobRoleID) {
		this.jobRoleID = jobRoleID;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public Employee getEmployee() {
		return employee;
	}

	public List<EmployeeRole> getRoles() {
		return roles;
	}

	public int[] getSelectedCompetencies() {
		return selectedCompetencies;
	}

	public void setSelectedCompetencies(int[] selectedCompetencies) {
		this.selectedCompetencies = selectedCompetencies;
	}

	public List<OperatorCompetency> getSelectedOC() {
		if (selectedCompetencies != null)
			return opCompDAO.findWhere("id IN (0," + Strings.implode(selectedCompetencies) + ")");

		return null;
	}

	public List<Employee> getEmployees() {
		// Find ALL employees or just the ones with roles?
		// return conDAO.find(conID).getEmployees();
		if (employees == null) {
			employees = new ArrayList<Employee>();

			if (selectedCompetencies == null || selectedCompetencies.length == 0) {
				List<EmployeeRole> roles = erDAO.findByContractor(conID);
				for (EmployeeRole role : roles) {
					employees.add(role.getEmployee());
				}
			} else {
				employees = employeeDAO.findByCompetencies(selectedCompetencies);
			}
		}

		return employees;
	}

	public List<OperatorCompetency> getCompetencies() {
		return opCompDAO.findByContractor(conID);
	}

	public List<EmployeeCompetency> getCompetencies(Employee employee) {
		return ecDAO.findByEmployee(employee.getId());
	}

	public List<EmployeeRole> getEmployeeRoles(int employeeID) {
		return erDAO.findByEmployee(employeeID);
	}

	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> getMap() {
		if (map == null && conID > 0) {
			map = opCompDAO.findEmployeeCompetencies(getEmployees(), getCompetencies());
		}

		return map;
	}
}
