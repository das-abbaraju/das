package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends AccountActionSupport {
	protected ContractorAccountDAO conDAO;
	protected EmployeeDAO employeeDAO;
	protected EmployeeCompetencyDAO ecDAO;
	protected OperatorCompetencyDAO opCompDAO;

	protected int conID;
	protected int competencyID;
	protected int employeeID;
	protected int employeeCompetencyID;
	protected boolean canEdit = false;
	protected boolean checked;
	
	protected Employee employee = null;
	protected OperatorCompetency competency = null;

	protected DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public EmployeeCompetencies(ContractorAccountDAO conDAO, EmployeeDAO employeeDAO,
			EmployeeCompetencyDAO ecDAO, OperatorCompetencyDAO opCompDAO) {
		this.conDAO = conDAO;
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
		this.opCompDAO = opCompDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isOperatorCorporate()) {
			// Operators or Corporates should only view this page
			// for the contractors that they're over
			if (conID == 0)
				addActionError("Please select a contractor to view this page");
		} else if (permissions.isContractor()) {
			// Contractors should view and edit the competencies
			canEdit = true;
			conID = permissions.getAccountId();
		}
		
		if (competencyID > 0 && employeeID == 0)
			competency = opCompDAO.find(competencyID);
		else if (employeeID > 0 && competencyID == 0)
			employee = employeeDAO.find(employeeID);
		
		if (button != null) {
			if (button.equalsIgnoreCase("Save")) {
				// A checkbox has been checked or unchecked.
				if (employeeCompetencyID > 0) {
					EmployeeCompetency ec = ecDAO.find(employeeCompetencyID);
					ec.setSkilled(checked);
					ecDAO.save(ec);
				} else
					addActionError("Missing employee competency ID");
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
	
	public int getCompetencyID() {
		return competencyID;
	}
	
	public void setCompetencyID(int competencyID) {
		this.competencyID = competencyID;
	}
	
	public int getEmployeeID() {
		return employeeID;
	}
	
	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}
	
	public int getEmployeeCompetencyID() {
		return employeeCompetencyID;
	}
	
	public void setEmployeeCompetencyID(int employeeCompetencyID) {
		this.employeeCompetencyID = employeeCompetencyID;
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
	
	public Employee getEmployee() {
		return employee;
	}
	
	public OperatorCompetency getCompetency() {
		return competency;
	}
	
	public List<Employee> getEmployees() {
		return conDAO.find(conID).getEmployees();
	}
	
	public List<OperatorCompetency> getCompetencies() {
		return opCompDAO.findByContractor(conID);
	}
	
	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> getMap() {
		if (map == null && conID > 0) {
			map = opCompDAO.findEmployeeCompetencies(getEmployees(), getCompetencies());
		}
		
		return map;
	}
}
