package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Employee;

@SuppressWarnings("serial")
public class EmployeeDashboard extends ContractorDocuments {
	
	private List<Employee> activeEmployees;
	private List<Integer> selectedEmployeeIds = new ArrayList<Integer>();
	private Integer selectedOperator;
	private HashSet<Integer> employeeGUARDAddableIds = new HashSet<Integer>();

	@Override
	public String execute() throws Exception {
		subHeading = "EmployeeGUARD&trade; Dashboard";
		findContractor();
		loadActiveEmployees();
		
		auditTypeRuleCache.initialize(auditRuleDAO);
		
		Iterator<AuditType> iterator = getManuallyAddAudits().iterator();
		while (iterator.hasNext()) {
			AuditType auditType = iterator.next();
			if (auditType.getClassType().isIm()) {
				employeeGUARDAddableIds.add(auditType.getId());
			}
		}
		
		return SUCCESS;
	}

	@Before
	public void startup() throws Exception {
		findContractor();
		loadActiveEmployees();
	}
	
	public String addIntegrityManagementAudits() throws Exception {
		return SUCCESS;
	}
	
	public String addImplementationAuditPlusAudits() throws Exception {
		return SUCCESS;
	}
	
	public boolean isAuditTypeAddable(int auditTypeId) {
		return employeeGUARDAddableIds.contains(auditTypeId);
	}
	
	private void loadActiveEmployees() {
		if (activeEmployees == null) {
			activeEmployees = new ArrayList<Employee>();

			if (contractor == null) {
				return;
			}

			for (Employee employee : contractor.getEmployees()) {
				if (employee.isActive()) {
					activeEmployees.add(employee);
				}
			}

			Collections.sort(activeEmployees, new Comparator<Employee>() {
				@Override
				public int compare(Employee o1, Employee o2) {
					String o1Name = ((o1.getLastName() == null) ? " " : o1.getLastName()) + " "
							+ ((o1.getFirstName() == null) ? " " : o1.getFirstName()) + " "
							+ ((o1.getTitle() == null) ? " " : o1.getTitle());
					String o2Name = ((o2.getLastName() == null) ? " " : o2.getLastName()) + " "
							+ ((o2.getFirstName() == null) ? " " : o2.getFirstName()) + " "
							+ ((o2.getTitle() == null) ? " " : o2.getTitle());

					if (o1Name.compareTo(o2Name) == 0) {
						return o1.getId() - o2.getId();
					}

					return o1Name.compareTo(o2Name);
				}
			});

		}
	}

	public List<Employee> getActiveEmployees() {
		return activeEmployees;
	}

	public void setActiveEmployees(List<Employee> activeEmployees) {
		this.activeEmployees = activeEmployees;
	}

	public List<Integer> getSelectedEmployeeIds() {
		return selectedEmployeeIds;
	}

	public void setSelectedEmployeeIds(List<Integer> selectedEmployeeIds) {
		this.selectedEmployeeIds = selectedEmployeeIds;
	}

	public Integer getSelectedOperator() {
		return selectedOperator;
	}

	public void setSelectedOperator(Integer selectedOperator) {
		this.selectedOperator = selectedOperator;
	}
}
