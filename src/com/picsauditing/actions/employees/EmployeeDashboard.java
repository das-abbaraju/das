package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.UserStatus;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmployeeDashboard extends ContractorDocuments {
	@Autowired
	protected EmployeeDAO employeeDAO;

	private int auditTypeID;
	private int year;

	private List<Employee> activeEmployees;
	private List<ContractorAudit> displayedAudits = new ArrayList<ContractorAudit>();

	private Set<AuditType> distinctAuditTypes;
	private Table<Integer, AuditType, Integer> auditsByYearAndType;

	@Before
	public void startup() throws Exception {
		findContractor();
		loadActiveEmployees();
		loadEmployeeGUARDAudits();

		subHeading = getText("EmployeeGUARD.Dashboard.title");
	}

	@Override
	public String execute() throws Exception {
		auditsByYearAndType = TreeBasedTable.create();
		distinctAuditTypes = new TreeSet<AuditType>();
		if (getEmployeeGuardAudits() != null) {
			for (ContractorAudit contractorAudit : getEmployeeGuardAudits()) {
				AuditType auditType = contractorAudit.getAuditType();

				if (auditType.isEmployeeSpecificAudit()) {
					Calendar effectiveLabel = Calendar.getInstance();
					effectiveLabel.setTime(contractorAudit.getEffectiveDateLabel());
					int year = effectiveLabel.get(Calendar.YEAR);

					Integer count = auditsByYearAndType.get(year, auditType);
					if (count == null) {
						count = 0;
					}
					count++;

					auditsByYearAndType.put(year, auditType, count);
					distinctAuditTypes.add(auditType);
				} else {
					displayedAudits.add(contractorAudit);
				}
			}
		}
		Collections.sort(displayedAudits, new YearsDescending());

		return SUCCESS;
	}

	public String employeeGUARDAudits() {
		if (auditTypeID > 0) {
			filterDisplayedAuditTypesBy(auditTypeID);
		}

		return SUCCESS;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<Employee> getActiveEmployees() throws Exception {
		return activeEmployees;
	}

	public List<ContractorAudit> getDisplayedAudits() {
		return displayedAudits;
	}

	public Set<AuditType> getDistinctAuditTypes() {
		return distinctAuditTypes;
	}

	public Table<Integer, AuditType, Integer> getAuditsByYearAndType() {
		return auditsByYearAndType;
	}

	public boolean isCanAddAudits() {
		return (permissions.isAdmin() || permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit));
	}

	public boolean isCanEditEmployees() {
		return permissions.isAdmin() || permissions.hasPermission(OpPerms.ContractorAdmin)
				|| permissions.hasPermission(OpPerms.ManageEmployees, OpType.Edit);
	}

	public boolean isCanEditJobRoles() {
		return (permissions.isAdmin() || permissions.hasPermission(OpPerms.DefineRoles, OpType.Edit));
	}

	public boolean isCanEditCompetencies() {
		return permissions.hasPermission(OpPerms.DefineCompetencies, OpType.Edit);
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

	public List<Integer> getYearsDescending() {
		List<Integer> yearsDescending = new ArrayList<Integer>();

		if (auditsByYearAndType != null) {
			yearsDescending.addAll(auditsByYearAndType.rowKeySet());
			Collections.reverse(yearsDescending);
		}

		return yearsDescending;
	}

	private void loadActiveEmployees() {
		if (activeEmployees == null) {
			activeEmployees = new ArrayList<Employee>();

			if (contractor == null) {
				return;
			}

			for (Employee employee : contractor.getEmployees()) {
				if (UserStatus.Active == employee.getStatus()) {
					activeEmployees.add(employee);
				}
			}

			Collections.sort(activeEmployees);
		}
	}

	private void loadEmployeeGUARDAudits() {
		if (getEmployeeGuardAudits() == null) {
			Set<ContractorAudit> auditList = getActiveAuditsStatuses().keySet();

			if (isContractorHasEmployeeGuard()
					&& (!permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorSafety))) {
				Iterator<ContractorAudit> iter = auditList.iterator();
				while (iter.hasNext()) {
					ContractorAudit audit = iter.next();
					if (audit.getAuditType().getClassType().isImEmployee() && audit.getOperators().size() > 0) {
						if (employeeGuardAudits == null) {
							employeeGuardAudits = new ArrayList<ContractorAudit>();
						}

						employeeGuardAudits.add(audit);
					}
				}
			}
		}
	}

	private void filterDisplayedAuditTypesBy(int auditTypeID) {
		for (ContractorAudit contractorAudit : getEmployeeGuardAudits()) {
			AuditType auditType = contractorAudit.getAuditType();

			Calendar effectiveLabel = Calendar.getInstance();
			effectiveLabel.setTime(contractorAudit.getEffectiveDateLabel());

			if (auditTypeID == auditType.getId()) {
				if (year == 0 || (year > 0 && effectiveLabel.get(Calendar.YEAR) == year)) {
					displayedAudits.add(contractorAudit);
				}
			}
		}

		Collections.sort(displayedAudits, new YearsDescending());
	}

	private class YearsDescending implements Comparator<ContractorAudit> {
		public int compare(ContractorAudit o1, ContractorAudit o2) {
			Calendar o1Cal = Calendar.getInstance();
			o1Cal.setTime(o1.getEffectiveDateLabel());

			Calendar o2Cal = Calendar.getInstance();
			o2Cal.setTime(o2.getEffectiveDateLabel());

			if (o1Cal.get(Calendar.YEAR) == o2Cal.get(Calendar.YEAR)) {
				o1.getAuditType().getName().compareTo(o2.getAuditType().getName());
			}

			return o2Cal.get(Calendar.YEAR) - o1Cal.get(Calendar.YEAR);
		}
	}
}
