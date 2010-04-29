package com.picsauditing.actions.employees;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class EmployeeAssessmentResults extends AccountActionSupport {
	protected AssessmentResultDAO resultDAO;
	protected AssessmentTestDAO testDAO;
	protected EmployeeDAO employeeDAO;
	
	protected Employee employee = null;
	protected ContractorAccount contractor = null;
	protected List<Date> history;
	protected Date effectiveDate;
	protected String date;
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	protected List<AssessmentResult> effective;
	protected List<AssessmentResult> expired;
	
	protected int employeeID;
	protected int resultID;
	protected boolean showHeader = false;
	
	public EmployeeAssessmentResults(AssessmentResultDAO resultDAO, AssessmentTestDAO testDAO, 
			EmployeeDAO employeeDAO) {
		this.resultDAO = resultDAO;
		this.testDAO = testDAO;
		this.employeeDAO = employeeDAO;
		subHeading = "Assessment Results";
		
		// When we need more detailed notes about OQ
		// noteCategory = NoteCategory.OperatorQualification;
		// subHeading = "Assessments";
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (id == 0)
			throw new Exception("Missing contractor id");
		
		// Load all of the employee information
		if (employee != null)
			employee = employeeDAO.find(employee.getId());
		else if (employeeID > 0)
			employee = employeeDAO.find(employeeID);
		
		if (employee != null) {
			contractor = (ContractorAccount) employee.getAccount();
			subHeading += " for " + employee.getDisplayName();
		}
		else {
			ContractorAccountDAO conDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
			int conID = getId() > 0 ? getId() : permissions.getAccountId();
			contractor = conDAO.find(conID);
		}
		
		if (button != null && isCanEdit()) {
			if (button.startsWith("Generate")) {
				// Get a random bunch of employees
				// Get all tests for assessment center id 11069
				generateRandomResults();
			}
			
			if ("Remove".equalsIgnoreCase(button)) {
				if (resultID > 0) {
					AssessmentResult result = resultDAO.find(resultID);
					result.expire();
					resultDAO.save(result);
				}
			}
			
			if (getEmployeeID() > 0)
				return redirect("EmployeeAssessmentResults.action?id=" + getId() + "&employee.id=" + getEmployeeID());
			
			return redirect("EmployeeAssessmentResults.action?id=" + getId());
		}
		
		return SUCCESS;
	}
	
	public boolean isCanEdit() {
		if ((permissions.isContractor() || permissions.isAdmin()) 
				&& (date == null || date.equals(sdf.format(new Date()))))
			return true;

		return false;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public ContractorAccount getContractor() {
		return contractor;
	}
	
	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}
	
	public List<Date> getHistory() {
		if (history == null) {
			if (employeeID > 0)
				history = resultDAO.findHistory("a.employee.id = " + employeeID);
			else if (permissions.isAdmin())
				history = resultDAO.findHistory(null);
			else
				history = resultDAO.findHistory("a.employee.account.id = " + contractor.getId());
		}
			
		return history;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public Date getEffectiveDate() {
		if (date != null) {
			try {
				effectiveDate = sdf.parse(date);
			} catch (Exception e) {
			}
		}
		
		if (effectiveDate == null)
			effectiveDate = new Date();
		
		return effectiveDate;
	}
	
	public int getEmployeeID() {
		if (employee != null)
			employeeID = employee.getId();
		
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
	
	public boolean isShowHeader() {
		if (permissions.isContractor())
			showHeader = true;
		
		return showHeader;
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
	
	public List<AssessmentResult> getEffective() {
		if (effective == null) {
			Date date = getEffectiveDate();
			
			if (getEmployeeID() > 0)
				effective = resultDAO.findInEffect("a.employee.id = " + getEmployeeID(), date);
			else
				effective = resultDAO.findInEffect("a.employee.account.id = " + contractor.getId(), date);
		}
		
		return effective;
	}
	
	public List<AssessmentResult> getExpired() {
		if (expired == null) {
			Date date = getEffectiveDate();
			
			if (getEmployeeID() > 0)
				expired = resultDAO.findExpired("a.employee.id = " + getEmployeeID(), date);
			else
				expired = resultDAO.findExpired("a.employee.account.id = " + contractor.getId(), date);
		}
		
		return expired;
	}
}