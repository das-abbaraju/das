package com.picsauditing.actions.operators;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class ManageAssessmentResults extends AccountActionSupport {
	protected AssessmentResultDAO resultDAO;
	protected AssessmentTestDAO testDAO;
	protected EmployeeDAO employeeDAO;
	
	protected int resultID;
	
	public ManageAssessmentResults(AssessmentResultDAO resultDAO, AssessmentTestDAO testDAO, 
			EmployeeDAO employeeDAO) {
		this.resultDAO = resultDAO;
		this.testDAO = testDAO;
		this.employeeDAO = employeeDAO;
		
		subHeading = "Manage Assessment Results";
		// When we need more detailed notes about OQ
		// noteCategory = NoteCategory.OperatorQualification;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
				
		// Check for basic view capabilities
		tryPermissions(OpPerms.ManageJobSites);
		
		if (button != null) {
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
				
				return redirect("ManageAssessmentResults.action");
			}
		}
		
		return SUCCESS;
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
			if (resultDAO.findByEmployee(employee.getId()).size() > 0)
				continue;
			
			AssessmentTest test = testDAO.findRandom();
			
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
		return resultDAO.findInEffect(null);
	}
	
	public List<AssessmentResult> getExpired() {
		return resultDAO.findExpired(null);
	}
}