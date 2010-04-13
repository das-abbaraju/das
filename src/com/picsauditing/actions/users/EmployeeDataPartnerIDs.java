package com.picsauditing.actions.users;

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
	
	protected int employeeID;
	
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
	
	public int getEmployeeID() {
		if (employee != null)
			return employee.getId();
		
		return employeeID;
	}
	
	public List<EmployeeAssessmentAuthorization> getDataPartnerIDs() {
		if (getEmployeeID() > 0)
			return eaaDAO.findByEmployee(getEmployeeID());
		
		return eaaDAO.findAll();
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