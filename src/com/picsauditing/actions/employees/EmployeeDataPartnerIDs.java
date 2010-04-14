package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentResultDAO;
import com.picsauditing.dao.EmployeeAssessmentAuthorizationDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeAssessmentAuthorization;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeDataPartnerIDs extends AccountActionSupport {
	protected AccountDAO accountDAO;
	protected AssessmentResultDAO resultDAO;
	protected EmployeeAssessmentAuthorizationDAO eaaDAO;
	protected EmployeeDAO employeeDAO;
	
	protected Employee employee = null;
	
	protected int centerID;
	protected int employeeID;
	protected int eaaID;
	protected String membershipID;
	protected String authorizationKey;
	
	public EmployeeDataPartnerIDs(AccountDAO accountDAO, AssessmentResultDAO resultDAO,
			EmployeeAssessmentAuthorizationDAO eaaDAO, EmployeeDAO employeeDAO) {
		this.accountDAO = accountDAO;
		this.resultDAO = resultDAO;
		this.eaaDAO = eaaDAO;
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
		
		if (employee != null)
			employee = employeeDAO.find(employee.getId());
		
		if (button != null) {
			if (button.startsWith("Generate"))
				generateData();
			
			if (button.equalsIgnoreCase("Remove")) {
				if (eaaID > 0) {
					EmployeeAssessmentAuthorization eaa = eaaDAO.find(eaaID);
					eaaDAO.remove(eaa);
				}
			}
			
			if (button.equalsIgnoreCase("Save")) {
				if (employeeID == 0 && employee == null)
					addActionError("Please select employee");
				if (centerID == 0)
					addActionError("Please select assessment center");
				if (getActionErrors().size() > 0)
					return SUCCESS;
				
				EmployeeAssessmentAuthorization eaa = new EmployeeAssessmentAuthorization();
				
				if (employee != null)
					eaa.setEmployee(employeeDAO.find(getEmployeeID()));
				else
					eaa.setEmployee(employeeDAO.find(employeeID));
				
				eaa.setAssessmentCenter(accountDAO.find(centerID));
				
				if (!Strings.isEmpty(membershipID))
					eaa.setMembershipID(membershipID);
				if (!Strings.isEmpty(authorizationKey))
					eaa.setAuthorizationKey(authorizationKey);
				
				eaa.setAuditColumns(permissions);
				eaaDAO.save(eaa);
				
				if (employee != null)
					return redirect("EmployeeDataPartnerIDs.action?employee.id=" + getEmployeeID());
			}
		}
		
		return SUCCESS;
	}
	
	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.ManageJobSites, OpType.Edit);
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public int getCenterID() {
		return centerID;
	}
	
	public void setCenterID(int centerID) {
		this.centerID = centerID;
	}
	
	public int getEmployeeID() {
		if (employee != null)
			return employee.getId();
		
		return employeeID;
	}
	
	public int getEaaID() {
		return eaaID;
	}
	
	public void setEaaID(int eaaID) {
		this.eaaID = eaaID;
	}
	
	public String getMembershipID() {
		return membershipID;
	}
	
	public void setMembershipID(String membershipID) {
		this.membershipID = membershipID;
	}
	
	public String getAuthorizationKey() {
		return authorizationKey;
	}
	
	public void setAuthorizationKey(String authorizationKey) {
		this.authorizationKey = authorizationKey;
	}
	
	public List<EmployeeAssessmentAuthorization> getDataPartnerIDs() {
		if (getEmployeeID() > 0)
			return eaaDAO.findByEmployee(getEmployeeID());
		
		return eaaDAO.findAll();
	}
	
	public List<Account> getAssessmentCenters() {
		return accountDAO.findWhere("type = 'Assessment'");
	}
	
	public List<Employee> getEmployees() {
		return employeeDAO.findAll();
	}
	
	private void generateData() {
		List<Employee> employees = employeeDAO.findRandom(10);
		
		for (Employee e : employees) {
			List<EmployeeAssessmentAuthorization> eaas = eaaDAO.findByEmployee(e.getId());
			Account center = accountDAO.findRandomAssessmentCenter();
			
			if (eaas.size() > 0) {
				List<Account> assessmentCenters = new ArrayList<Account>();
				
				for (EmployeeAssessmentAuthorization eaa : eaas) {
					assessmentCenters.add(eaa.getAssessmentCenter());
				}
				
				while (assessmentCenters.contains(center)) {
					center = accountDAO.findRandomAssessmentCenter();
				}
			}
			
			EmployeeAssessmentAuthorization eaa = new EmployeeAssessmentAuthorization();
			eaa.setEmployee(e);
			eaa.setAssessmentCenter(center);
			eaa.setAuditColumns(permissions);
			eaa.setMembershipID("" + center.getId() + "-" + (System.currentTimeMillis() % 100000));
			eaa.setAuthorizationKey(Strings.hash("" + System.currentTimeMillis()).substring(0, 8).toUpperCase());
			eaaDAO.save(eaa);
		}
	}
}