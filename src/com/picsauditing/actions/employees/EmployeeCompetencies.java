package com.picsauditing.actions.employees;

import java.util.List;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorCompetencyDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class EmployeeCompetencies extends AccountActionSupport {
	protected ContractorAccountDAO conDAO;
	protected OperatorCompetencyDAO opCompDAO;

	protected int conID;
	protected boolean canEdit = false;

	protected DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> map;

	public EmployeeCompetencies(ContractorAccountDAO conDAO, OperatorCompetencyDAO opCompDAO) {
		this.conDAO = conDAO;
		this.opCompDAO = opCompDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isOperatorCorporate()) {
			// Operators or Corporates should only view this page
			// for the contractors that they're over
		} else if (permissions.isContractor()) {
			// Contractors should view and edit the competencies
			canEdit = true;
			conID = permissions.getAccountId();
		}
		
		return SUCCESS;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}
	
	public boolean isCanEdit() {
		return canEdit;
	}
	
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	public List<OperatorCompetency> getCompetencies() {
		return opCompDAO.findByContractor(conID);
	}
	
	public DoubleMap<Employee, OperatorCompetency, EmployeeCompetency> getEmployeeCompetencies() {
		if (map == null && conID > 0) {
			List<Employee> employees = conDAO.find(conID).getEmployees();
			map = opCompDAO.findEmployeeCompetencies(employees, getCompetencies());
		}
		
		return map;
	}
}
