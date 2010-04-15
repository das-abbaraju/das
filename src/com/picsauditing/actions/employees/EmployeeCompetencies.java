package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeCompetencyDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
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
	protected int employeeID;
	protected int competencyID;
	protected int ecID;
	protected boolean canEdit = false;
	protected boolean checked;
	
	protected ContractorAccount contractor;
	protected Employee employee = null;

	protected DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public EmployeeCompetencies(ContractorAccountDAO conDAO, EmployeeDAO employeeDAO,
			EmployeeCompetencyDAO ecDAO, OperatorCompetencyDAO opCompDAO) {
		this.conDAO = conDAO;
		this.employeeDAO = employeeDAO;
		this.ecDAO = ecDAO;
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
			throw new Exception("Missing conID");
		
		contractor = conDAO.find(conID);
		
		if (employeeID > 0)
			employee = employeeDAO.find(employeeID);
		
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
				} else
					addActionError("Missing employee competency ID");
			}
			
			if (button.equalsIgnoreCase("AddCompetency")) {
				if (competencyID > 0 && employeeID > 0) {
					OperatorCompetency competency = opCompDAO.find(competencyID);
					EmployeeCompetency ec = new EmployeeCompetency();
					ec.setCompetency(competency);
					ec.setEmployee(employee);
					ec.setSkilled(true);
					ec.setAuditColumns(permissions);
					ecDAO.save(ec);
					
					return redirect("EmployeeCompetencies.action?conID=" + conID + "&employeeID=" + employeeID);
				} else
					addActionError("Missing employee or competency ID");
			}
			
			if (button.equalsIgnoreCase("RemoveCompetency")) {
				if (ecID > 0) {
					EmployeeCompetency ec = ecDAO.find(ecID);
					ecDAO.remove(ec);
					return redirect("EmployeeCompetencies.action?conID=" + conID + "&employeeID=" + employeeID);
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
	
	public int getEmployeeID() {
		return employeeID;
	}
	
	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}
	
	public int getCompetencyID() {
		return competencyID;
	}
	
	public void setCompetencyID(int competencyID) {
		this.competencyID = competencyID;
	}
	
	public int getEcID() {
		return ecID;
	}
	
	public void setEcID(int ecID) {
		this.ecID = ecID;
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
