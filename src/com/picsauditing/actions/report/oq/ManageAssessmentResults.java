package com.picsauditing.actions.report.oq;

import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class ManageAssessmentResults extends PicsActionSupport {
	private AccountDAO accountDAO;
	private AssessmentResultDAO resultDAO;
	private AssessmentTestDAO testDAO;
	private EmployeeDAO employeeDAO;
	
	private int id;
	private int companyID;
	private int employeeID;
	private int resultID;
	private int testID;
	private Account center;
	private AssessmentResult result;
	private String company;
	private String subHeading = "Manage Assessment Results";
	
	public ManageAssessmentResults(AccountDAO accountDAO, AssessmentResultDAO resultDAO, 
			AssessmentTestDAO testDAO, EmployeeDAO employeeDAO) {
		this.accountDAO = accountDAO;
		this.resultDAO = resultDAO;
		this.testDAO = testDAO;
		this.employeeDAO = employeeDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if (!permissions.isAdmin() && !permissions.isAssessment())
			throw new NoRightsException("Administrator or Assessment Center");
		
		if (permissions.isAssessment())
			id = permissions.getAccountId();
		
		if (id > 0)
			center = accountDAO.find(id);
		else
			addActionError("Missing Assessment Center ID");
		
		if (button != null) {
			if (button.equals("Employee")) {
				if (companyID > 0){
					if(resultID > 0){
						result = resultDAO.find(resultID);
						employeeID = result.getEmployee().getId();
					}
					return "getEmployees";
				}
				else
					addActionError("Missing company ID");
				
				return SUCCESS;
			}
			
			if (button.equals("Load")) {
				if (resultID > 0) {
					result = resultDAO.find(resultID);
					testID = result.getAssessmentTest().getId();
					employeeID = result.getEmployee().getId();
					companyID = result.getEmployee().getAccount().getId();
					company = result.getEmployee().getAccount().getName();
				}
				else
					result = new AssessmentResult();
				
				return SUCCESS;
			}
			
			if (button.equals("Save")) {
				result.setAuditColumns(permissions);
				
				if (testID > 0)
					result.setAssessmentTest(testDAO.find(testID));
				else
					addActionError("Missing Assessment Test");
				
				if (employeeID > 0)
					result.setEmployee(employeeDAO.find(employeeID));
				else if (result.getEmployee() == null)
					addActionError("Missing Employee");
				
				if (getActionErrors().size() == 0) {
					resultDAO.save(result);
					result = new AssessmentResult();
				}
			}
			
			if (button.equals("Remove")) {
				if (resultID > 0) {
					result = resultDAO.find(resultID);
					
					if (result != null)
						resultDAO.remove(result);
					else {
						addActionError("Could not find Assessment Result");
						return SUCCESS;
					}
					
					return redirect("ManageAssessmentResults.action" + 
							(permissions.isAssessment() ? "" : "?id=" + id));
				} else
					addActionError("Missing Assessment Result ID");
			}
		}
		
		return SUCCESS;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCompanyID() {
		return companyID;
	}
	
	public void setCompanyID(int companyID) {
		this.companyID = companyID;
	}
	
	public int getEmployeeID() {
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
	
	public int getTestID() {
		return testID;
	}
	
	public void setTestID(int testID) {
		this.testID = testID;
	}
	
	public Account getCenter() {
		return center;
	}
	
	public AssessmentResult getResult() {
		return result;
	}
	
	public void setResult(AssessmentResult result) {
		this.result = result;
	}
	
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getSubHeading() {
		return subHeading;
	}
	
	// LISTS
	public List<AssessmentResult> getResults() {
		return resultDAO.findByAssessmentCenter(id);
	}
	
	public List<AssessmentTest> getTests() {
		return testDAO.findByAssessmentCenter(id);
	}
	
	public List<Employee> getEmployees() {
		// How do we want to do this? By company?
		return employeeDAO.findByAccount(accountDAO.find(companyID));
	}
}