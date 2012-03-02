package com.picsauditing.actions.employees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.NoteCategory;
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
		subHeading = "EmployeeGUARD&trade; Dashboard";
		return SUCCESS;
	}

	@Before
	public void startup() throws Exception {
		findContractor();
		loadActiveEmployees();
		auditTypeRuleCache.initialize(auditRuleDAO);
		loadEmployeeGuardAudits();
		loadAuditTypeOperators();
	}
	
	public String addIntegrityManagementAudits() throws Exception {
		return createAudit(AuditType.INTEGRITYMANAGEMENT);
	}
	
	public String addImplementationAuditPlusAudits() throws Exception {
		return createAudit(AuditType.IMPLEMENTATIONAUDITPLUS);
	}
	
	private String createAudit(int auditTypeId) throws Exception {
		List<Employee> selectedEmployees = findSelectedEmployees();
		OperatorAccount operator = findOperator();
		
		for (Employee employee:selectedEmployees) {
			ContractorAudit conAudit = new ContractorAudit();
			conAudit.setAuditType(auditTypeDAO.find(auditTypeId));
			
			if (operator == null || operator.getId() == 0) {
				if (permissions.isOperatorCorporate())
					selectedOperatorId = permissions.getAccountId();
				else {
					addActionError("You must select an operator.");
					return SUCCESS;
				}
			}
			conAudit.setRequestingOpAccount(new OperatorAccount());
			conAudit.getRequestingOpAccount().setId(selectedOperatorId);
			conAudit.setEmployee(employee);

			ContractorAuditOperator cao = new ContractorAuditOperator();
			cao.setAudit(conAudit);
			cao.setOperator(conAudit.getRequestingOpAccount());
			cao.setAuditColumns(permissions);
			// This is almost always Pending
			AuditStatus firstStatus = conAudit.getAuditType().getWorkFlow().getFirstStep().getNewStatus();
			ContractorAuditOperatorWorkflow caow = cao.changeStatus(firstStatus, null);
			if (caow != null) {
				caow.setNotes(getTextParameterized("AuditOverride.ManuallyChangingStatus", cao.getOperator().getName()));
				caoDAO.save(caow);
			}

			conAudit.getOperators().add(cao);
			conAudit.setLastRecalculation(null);

			if (!Strings.isEmpty(auditFor))
				conAudit.setAuditFor(auditFor);

			conAudit.setManuallyAdded(true);
			conAudit.setAuditColumns(permissions);
			conAudit.setContractorAccount(contractor);
			conAudit.setAuditFor(employee.getDisplayName() + " / " + employee.getTitle());
			
			auditDao.save(conAudit);
			employee.getAudits().add(conAudit);
			employeeDAO.save(employee);

			addNote(conAudit.getContractorAccount(), "Added " + conAudit.getAuditType().getName().toString()
					+ " manually", NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));

			// TODO want to run cron but not redirect since we may be doing mulptiple audits
			this.redirect("ContractorCron.action?conID=" + id
						+ "&button=Run&steps=AuditBuilder&redirectUrl=EmployeeDashboard.action?id=" + contractor.getId());
		}

		return SUCCESS;
	}
	
	private List<Employee> findSelectedEmployees() {
		List<Employee> selectedEmployees = new ArrayList<Employee>();
		
		for (Employee employee:activeEmployees) {
			if (selectedEmployeeIds.contains(employee.getId())) {
				selectedEmployees.add(employee);
			}
		}
		return selectedEmployees;
	}
	
	private OperatorAccount findOperator() {
		for (ContractorOperator operator:contractor.getNonCorporateOperators()) {
			if (operator.getOperatorAccount().getId() == selectedOperatorId) {
				return operator.getOperatorAccount();
			}
		}
		return null;
	}
	
	public boolean isAuditTypeAddable(int auditTypeId) {
		return employeeGuardAddableIds.contains(auditTypeId);
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
	
	private void loadEmployeeGuardAudits() {
		Iterator<AuditType> iterator = getManuallyAddAudits().iterator();
		while (iterator.hasNext()) {
			AuditType auditType = iterator.next();
			if (isEmployeeGaurdAuditType(auditType)) {
				if (permissions.isAdmin() || permissions.isContractor() || permissions.canSeeAudit(auditType))
					employeeGuardAddableIds.add(auditType.getId());
			}
		}
	}
	
	private void loadAuditTypeOperators() {
		for (Integer auditTypeID:employeeGuardAddableIds) {
			if (!auditTypeOperators.containsKey(auditTypeID)) {
				auditTypeOperators.put(auditTypeID, new ArrayList<OperatorAccount>());
			}
			for (ContractorOperator conOp:contractor.getNonCorporateOperators()) {
				if (conOp.getOperatorAccount().getVisibleAuditTypes().contains(auditTypeID)) {
					auditTypeOperators.get(auditTypeID).add(conOp.getOperatorAccount());
				}
			}
		}
	}
	
	private boolean isEmployeeGaurdAuditType(AuditType auditType) {
		return ((auditType.getClassType().isIm() || 
				auditType.getClassType().isEmployee() || 
				auditType.getId() == AuditType.HSE_COMPETENCY ||
				auditType.getId() == AuditType.HSE_COMPETENCY_REVIEW ||				
				auditType.getId() == AuditType.IMPLEMENTATIONAUDITPLUS));
	}

	public List<Employee> getActiveEmployees() throws Exception{
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

	public Integer getSelectedOperatorId() {
		return selectedOperatorId;
	}

	public void setSelectedOperatorId(Integer selectedOperator) {
		this.selectedOperatorId = selectedOperator;
	}
	
	public List<ContractorOperator> getVisibleOperators() {
		List<ContractorOperator> visibleOperators = new ArrayList<ContractorOperator>();
		
		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			if (permissions.isAdmin() || permissions.isContractor()
					|| co.getOperatorAccount().getId() == permissions.getAccountId()
					|| co.getOperatorAccount().isDescendantOf(permissions.getAccountId())) {
				visibleOperators.add(co);
			}
		}
		
	return visibleOperators;
	}
	
	public List<OperatorAccount> getOperatorsByAuditTypeId(int auditTypeId) {
		return auditTypeOperators.get(auditTypeId);
	}
	
	public List<ContractorAudit> getUnattachedEmployeeAudits() {
		List<ContractorAudit> unattachedEmployeeAudits = new ArrayList<ContractorAudit>();

		for (ContractorAudit ca : contractor.getAudits()) {
			if (isEmployeeGaurdAuditType(ca.getAuditType()) && ca.getEmployee() == null && ca.isVisibleTo(permissions)) {
				unattachedEmployeeAudits.add(ca);
			}
		}

		return unattachedEmployeeAudits;
	}
	
}
