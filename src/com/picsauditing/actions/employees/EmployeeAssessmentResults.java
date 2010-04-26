package com.picsauditing.actions.employees;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class EmployeeAssessmentResults extends AccountActionSupport {
	protected AssessmentResultDAO resultDAO;
	protected AssessmentTestDAO testDAO;
	protected EmployeeDAO employeeDAO;
	
	protected Employee employee = null;
	
	protected int employeeID;
	protected int resultID;
	
	public EmployeeAssessmentResults(AssessmentResultDAO resultDAO, AssessmentTestDAO testDAO, 
			EmployeeDAO employeeDAO) {
		this.resultDAO = resultDAO;
		this.testDAO = testDAO;
		this.employeeDAO = employeeDAO;
		
		// When we need more detailed notes about OQ
		// noteCategory = NoteCategory.OperatorQualification;
		// subHeading = "Assessments";
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
				
		// Check for basic view capabilities
		tryPermissions(OpPerms.ManageJobSites);
		
		// Load all of the employee information
		if (employee != null)
			employee = employeeDAO.find(employee.getId());
		
		if (button != null) {
			tryPermissions(OpPerms.ManageJobSites, OpType.Edit);
			
			if (button.startsWith("Generate")) {
				// Get a random bunch of employees
				// Get all tests for assessment center id 11069
				generateRandomResults();
			}
			
			if ("Remove".equalsIgnoreCase(button)) {
				if (resultID > 0) {
					AssessmentResult result = resultDAO.find(resultID);
					result.setExpirationDate(new Date());
					resultDAO.save(result);
				}
				
				if (getEmployeeID() > 0)
					return redirect("AssessmentResults.action?employee.id=" + getEmployeeID());
				
				return redirect("AssessmentResults.action");
			}
		}
		
		return SUCCESS;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public int getEmployeeID() {
		if (employee != null)
			return employee.getId();
		
		return employeeID;
	}
	
	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}
	
	public int getResultID() {
		return resultID;
	}
	
	public void setResultID(int resultID) {
		this.resultID = resultID;
	}
	
	private void generateRandomResults() {
		List<Employee> employees = employeeDAO.findRandom(10);
		
		for (Employee employee : employees) {
			AssessmentTest test = testDAO.findRandom();
			
			List<AssessmentResult> results = resultDAO.findByEmployee(employee.getId());
			while (results.size() > 0) {
				employee = employeeDAO.findRandom(1).get(0);
				results = resultDAO.findByEmployee(employee.getId());
			}
			
			Calendar cal = Calendar.getInstance();
			AssessmentResult result = new AssessmentResult();
			result.setAssessmentTest(test);
			result.setAuditColumns(permissions);
			result.setEffectiveDate(cal.getTime());
			cal.add(Calendar.MONTH, test.getMonthsToExpire());						
			result.setExpirationDate(cal.getTime());
			result.setEmployee(employee);
			
			resultDAO.save(result);
		}
	}
	
	public List<AssessmentResult> getAllResults() {
		// Order employees by employer name, last name
		List<AssessmentResult> allResults = resultDAO.findAll();
		
		return allResults;
	}
	
	public List<AssessmentResult> getEffective() {
		if (getEmployeeID() > 0)
			return resultDAO.findInEffect("employeeID = " + getEmployeeID());
		
		return resultDAO.findInEffect(null);
	}
	
	public List<AssessmentResult> getExpired() {
		if (getEmployeeID() > 0)
			return resultDAO.findExpired("employeeID = " + getEmployeeID());
		
		return resultDAO.findExpired(null);
	}
}