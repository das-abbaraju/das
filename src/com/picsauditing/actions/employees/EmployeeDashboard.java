package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeDashboard extends ContractorDocuments {
	@Autowired
	protected EmployeeDAO employeeDAO;
	
	private List<Employee> activeEmployees;
	private List<Integer> selectedEmployeeIds = new ArrayList<Integer>();
	private Integer selectedOperatorId;
	private HashSet<Integer> employeeGuardAddableIds = new HashSet<Integer>();
	private HashMap<Integer, List<OperatorAccount>> auditTypeOperators = new HashMap<Integer, List<OperatorAccount>>();

	@Override
	public String execute() throws Exception {
		subHeading = getText("EmployeeGUARD.Dashboard.title");
		return SUCCESS;
	}

	@Before
	public void startup() throws Exception {
		findContractor();
		loadActiveEmployees();
	}
	
	private OperatorAccount findOperator() {
		for (ContractorOperator operator:contractor.getNonCorporateOperators()) {
			if (operator.getOperatorAccount().getId() == selectedOperatorId) {
				return operator.getOperatorAccount();
			}
		}
		return null;
	}
	
	public boolean isCanAddAudits() {
		return (permissions.isAdmin() || permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit))
				&& (employeeGuardAddableIds.size() > 0);
	}
	
	public boolean isCanEditEmploy() {
		return permissions.isAdmin() || permissions.hasPermission(OpPerms.ContractorAdmin)
				|| permissions.hasPermission(OpPerms.ManageEmployees, OpType.Edit);
	}

	public boolean isCanEditJob() {
		return permissions.isAdmin() && (employeeGuardAddableIds.size() > 0);
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
	
	public List<Employee> getActiveEmployees() throws Exception{
		return activeEmployees;
	}

	public void setActiveEmployees(List<Employee> activeEmployees) {
		this.activeEmployees = activeEmployees;
	}
	
	public String getAuditName(ContractorAudit audit) {
		String auditName = "";
		
		if (!Strings.isEmpty(audit.getAuditFor()))
			auditName = audit.getAuditFor() + " ";
		
		auditName += getText(audit.getAuditType().getI18nKey("name"));
		
		if (audit.getEffectiveDateLabel() != null) {
			auditName += " '" + DateBean.format(audit.getEffectiveDateLabel(), "yy");
		}

		return auditName;
	}
}
