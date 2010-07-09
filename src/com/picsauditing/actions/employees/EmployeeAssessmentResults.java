package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class EmployeeAssessmentResults extends AccountActionSupport {
	protected AccountDAO accountDAO;
	protected AssessmentResultDAO resultDAO;
	protected AssessmentTestDAO testDAO;
	protected EmployeeDAO employeeDAO;
	
	protected Account assessmentCenter = null;
	protected ContractorAccount contractor = null;
	protected Date effectiveDate;
	protected Employee employee = null;
	protected String date;
	protected List<Date> history;
	protected List<AssessmentResult> effective;
	protected List<AssessmentResult> expired;
	
	protected int centerID;
	protected int employeeID;
	protected int resultID;
	protected boolean showHeader = false;
	
	public EmployeeAssessmentResults(AccountDAO accountDAO, AssessmentResultDAO resultDAO, 
			AssessmentTestDAO testDAO, EmployeeDAO employeeDAO) {
		this.accountDAO = accountDAO;
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
		if (employeeID > 0)
			employee = employeeDAO.find(employeeID);
		
		if (employee != null) {
			contractor = (ContractorAccount) employee.getAccount();
			subHeading += " for " + employee.getDisplayName();
		}
		else {
			int conID = id > 0 ? id : permissions.getAccountId();
			contractor = (ContractorAccount) accountDAO.find(conID);
		}
		
		if (centerID > 0) {
			assessmentCenter = accountDAO.find(centerID);
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
				return redirect("EmployeeAssessmentResults.action?id=" + id + "&employee.id=" + getEmployeeID());
			
			return redirect("EmployeeAssessmentResults.action?id=" + id);
		}
		
		return SUCCESS;
	}
	
	public boolean isCanEdit() {
		if ((permissions.isContractor() || permissions.isAdmin()) 
				&& (date == null || date.equals(DateBean.format(new Date(), "yyyy-MM-dd"))))
			return true;

		return false;
	}
	
	public Account getAssessmentCenter() {
		return assessmentCenter;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public ContractorAccount getContractor() {
		return contractor;
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
				effectiveDate = DateBean.parseDate(date);
			} catch (Exception e) {
			}
		}
		
		if (effectiveDate == null)
			effectiveDate = new Date();
		
		return effectiveDate;
	}
	
	public int getCenterID() {
		return centerID;
	}
	
	public void setCenterID(int centerID) {
		this.centerID = centerID;
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
		List<AssessmentTest> randomTests = testDAO.findRandom(5);
		int count = 0;
		
		if (employee == null) {
			Map<Employee, List<AssessmentResult>> map = resultDAO.findByAccount(contractor);
			
			for (Employee employee : map.keySet()) {
				List<AssessmentTest> existingTests = new ArrayList<AssessmentTest>();
				for (AssessmentResult ar : map.get(employee)) {
					existingTests.add(ar.getAssessmentTest());
				}
				
				randomTests.removeAll(existingTests);
				
				Calendar cal = Calendar.getInstance();
				AssessmentResult result = new AssessmentResult();
				result.setAssessmentTest(randomTests.get(count % randomTests.size()));
				result.setAuditColumns(permissions);
				result.setEffectiveDate(cal.getTime());
				cal.add(Calendar.MONTH, randomTests.get(count % randomTests.size()).getMonthsToExpire());						
				result.setExpirationDate(cal.getTime());
				result.setEmployee(employee);
				
				count++;
				resultDAO.save(result);
			}
		} else {
			// Generate results just for this employee?
			for (AssessmentResult result : employee.getAssessmentResults()) {
				randomTests.remove(result.getAssessmentTest());
			}
			
			for (AssessmentTest test : randomTests) {
				Calendar cal = Calendar.getInstance();
				AssessmentResult result = new AssessmentResult();
				result.setAssessmentTest(test);
				result.setAuditColumns(permissions);
				result.setEffectiveDate(cal.getTime());
				cal.add(Calendar.MONTH, test.getMonthsToExpire());						
				result.setExpirationDate(cal.getTime());
				result.setEmployee(employee);
				
				count++;
				resultDAO.save(result);
			}
		}
	}
	
	public List<AssessmentResult> getEffective() {
		if (effective == null) {
			Date date = getEffectiveDate();
			
			if (getEmployeeID() > 0)
				effective = resultDAO.findInEffect("a.employee.id = " + getEmployeeID(), date);
			else
				effective = resultDAO.findInEffect("a.employee.account.id = " + contractor.getId(), date);
			
			if (centerID > 0) {
				List<AssessmentResult> list = new ArrayList<AssessmentResult>();
				for (AssessmentResult result : effective) {
					if (result.getAssessmentTest().getAssessmentCenter().equals(assessmentCenter))
						list.add(result);
				}
				
				effective = list;
			}
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
			
			if (centerID > 0) {
				List<AssessmentResult> list = new ArrayList<AssessmentResult>();
				for (AssessmentResult result : expired) {
					if (result.getAssessmentTest().getAssessmentCenter().equals(assessmentCenter))
						list.add(result);
				}
				
				expired = list;
			}
		}
		
		return expired;
	}
	
	public List<Account> getAllAssessmentCenters() {
		return accountDAO.findWhere("a.type = 'Assessment'");
	}
}