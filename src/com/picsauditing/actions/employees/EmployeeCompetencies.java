package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.EmployeeRoleDAO;
import com.picsauditing.dao.JobRoleDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.EmployeeRole;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends AccountActionSupport {
	protected ContractorAccountDAO conDAO;
	protected EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected EmployeeRoleDAO erDAO;
	protected JobRoleDAO jobRoleDAO;
	protected OperatorCompetencyDAO opCompDAO;

	protected int conID;
	protected int employeeID = 0;
	protected int ecID;
	protected int jobRoleID = 0;
	protected boolean canEdit = false;
	protected boolean checked;

	protected int[] selectedCompetencies = null;
	protected ContractorAccount contractor;
	protected Employee employee = null;
	protected JobRole jobRole;
	protected List<Employee> employees;
	protected Map<Integer, List<EmployeeRole>> employeeRoles;
	protected DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public EmployeeCompetencies(ContractorAccountDAO conDAO, EmployeeDAO employeeDAO, EmployeeCompetencyDAO ecDAO,
			EmployeeRoleDAO erDAO, JobRoleDAO jobRoleDAO, OperatorCompetencyDAO opCompDAO) {
		this.conDAO = conDAO;
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
		this.erDAO = erDAO;
		this.jobRoleDAO = jobRoleDAO;
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
			jobRole = jobRoleDAO.find(jobRoleID);

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
	
	public JobRole getJobRole() {
		return jobRole;
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

			if (jobRoleID > 0) {
				employees = employeeDAO.findByJobRole(jobRoleID, conID);
			} else if (selectedCompetencies == null || selectedCompetencies.length == 0) {
				employees = getAllEmployees();
			} else {
				employees = employeeDAO.findByCompetencies(selectedCompetencies, conID);
			}
		}

		return employees;
	}
	
	public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
		List<EmployeeRole> roles = erDAO.findByContractor(conID);
		for (EmployeeRole role : roles) {
			if (!employees.contains(role.getEmployee()))
				employees.add(role.getEmployee());
		}

		return employees;
	}

	public List<OperatorCompetency> getCompetencies() {
		return opCompDAO.findByContractor(conID);
	}

	public List<OperatorCompetency> getCompetencies(Employee employee) {
		List<Integer> jobRoleIDs = new ArrayList<Integer>();
		
		for (EmployeeRole er : employee.getEmployeeRoles()) {
			jobRoleIDs.add(er.getJobRole().getId());
		}
		List<JobCompetency> jobCompetencies = opCompDAO.findByJobRoles(jobRoleIDs);
		
		List<OperatorCompetency> list = new ArrayList<OperatorCompetency>();
		for (JobCompetency jc : jobCompetencies) {
			if (!list.contains(jc.getCompetency()))
				list.add(jc.getCompetency());
		}
		
		return list;
	}
	
	public List<OperatorCompetency> getCompetenciesByJobRole() {
		return opCompDAO.findByJobRole(jobRoleID);
	}
	
	public List<JobRole> getJobRoles() {
		return jobRoleDAO.findJobRolesByAccount(conID, true);
	}
	
	public Map<Integer, List<EmployeeRole>> getEmployeeRolesByContractor() {
		if (employeeRoles == null)
			employeeRoles = erDAO.findEmployeeRolesByContractor(conID);
		
		return employeeRoles;
	}

	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> getMap() {
		if (map == null && conID > 0)
			map = opCompDAO.findEmployeeCompetencies(getEmployees(), getCompetencies());

		return map;
	}
}
