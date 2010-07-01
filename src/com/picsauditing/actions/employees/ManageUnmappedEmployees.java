package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.AssessmentTestDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AssessmentResult;
import com.picsauditing.jpa.entities.AssessmentResultStage;
import com.picsauditing.jpa.entities.AssessmentTest;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class ManageUnmappedEmployees extends AccountActionSupport {
	private AccountDAO accountDAO;
	private AssessmentTestDAO testDAO;
	private EmployeeDAO employeeDAO;

	protected int employeeID;
	protected int stageID;
	
	public ManageUnmappedEmployees(AccountDAO accountDAO, AssessmentTestDAO testDAO, EmployeeDAO employeeDAO) {
		this.accountDAO = accountDAO;
		this.employeeDAO = employeeDAO;
		this.testDAO = testDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			permissions.tryPermission(OpPerms.ContractorAdmin);
		else
			permissions.tryPermission(OpPerms.EditUsers);

		if (account == null)
			account = accountDAO.find(permissions.getAccountId());

		if (account == null)
			throw new RecordNotFoundException("Account " + id + " not found");

		if (permissions.getAccountId() != account.getId())
			permissions.tryPermission(OpPerms.AllOperators);

		this.subHeading = "Employee Mapping";
		
		if (button != null) {
			if ("Save".equals(button)) {
				if (employeeID > 0 && stageID > 0) {
					String matchedName = null;
					List<AssessmentResultStage> staged = testDAO.findStagedByAccount(account.getId());
					List<AssessmentTest> tests = null;
					
					for (AssessmentResultStage stage : staged) {
						if (stage.getId() == stageID) {
							matchedName = stage.getFirstName() + " " + stage.getLastName();
							tests = testDAO.findWhere("qualificationType = '" + stage.getQualificationType() + 
									"' AND qualificationMethod = '" + stage.getQualificationMethod() +
									"' AND description = '" + stage.getDescription() + "'");
							
							break;
						}
					}
					
					for (AssessmentResultStage stage : staged) {
						if ((stage.getFirstName() + " " + stage.getLastName()).equals(matchedName)) {
							if (tests != null && tests.size() == 1) {
								Employee employee = new Employee();
								employee.setId(employeeID);
	
								AssessmentResult result = new AssessmentResult();
								result.setAuditColumns(permissions);
								result.setEmployee(employee);
								result.setAssessmentTest(tests.get(0));
								result.setEffectiveDate(stage.getQualificationDate());
								result.setExpirationDate(DateBean.addMonths(stage.getQualificationDate(),
										tests.get(0).getMonthsToExpire()));
								testDAO.save(result);
								
								stage.setPicsEmployee(employee);
								testDAO.save(stage);
							}
						}
					}
				}
			}
			
			if ("Add New Employee".equals(button)) {
				if (stageID > 0) {
					List<AssessmentResultStage> staged = testDAO.findStagedByAccount(account.getId());
					Employee employee = null;
					
					for (AssessmentResultStage stage : staged) {
						if (stage.getId() == stageID) {
							if (employee == null) {
								employee = new Employee();
								employee.setAuditColumns(permissions);
								employee.setFirstName(stage.getFirstName());
								employee.setLastName(stage.getLastName());
								employee.setAccount(account);
								employee = (Employee) employeeDAO.save(employee);
							}
							
							stage.setPicsEmployee(employee);
							testDAO.save(stage);
						}
						
						if (employee != null && stage.getFirstName().equals(employee.getFirstName()) 
								&& stage.getLastName().equals(employee.getLastName())) {
							stage.setPicsEmployee(employee);
						}
					}
				} else
					addActionError("Missing Assessment Result");
			}
		}
		
		return SUCCESS;
	}
	
	public int getEmployeeID() {
		return employeeID;
	}
	
	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}
	
	public int getStageID() {
		return stageID;
	}
	
	public void setStageID(int stageID) {
		this.stageID = stageID;
	}
	
	// Lists
	public List<Employee> getEmployees() {
		return employeeDAO.findByAccount(account);
	}
	
	public Map<String, List<AssessmentResultStage>> getUnmapped() {
		List<AssessmentResultStage> staged = testDAO.findStagedByAccount(account.getId());
		Map<String, List<AssessmentResultStage>> map = new HashMap<String, List<AssessmentResultStage>>();
		 
		for (AssessmentResultStage stage : staged) {
			if (stage.getPicsEmployee() == null) {
				String displayName = stage.getFirstName() + " " + stage.getLastName();
				if (map.get(displayName) == null)
					map.put(displayName, new ArrayList<AssessmentResultStage>());
				
				map.get(displayName).add(stage);
			}
		}
		
		return map;
	}
}
