package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditOverride extends ContractorDocuments {
	@Autowired
	private AuditBuilder auditBuilder;
	
	protected List<Integer> selectedEmployeeIds;
	List<Employee> employeesLeftList;
	List<Employee> employeesRightList;

	@Override
	public String execute() throws Exception {
		subHeading = "Manually Add Audit";
		this.findContractor();

		auditTypeRuleCache.initialize(auditRuleDAO);

		if (!isManuallyAddAudit()) {
			throw new NoRightsException("Cannot Manually Add Audits");
		}
		
			employeesLeftList = new ArrayList<Employee>();
			for (Employee e : contractor.getEmployees()) {
				if (e.isActive())
					employeesLeftList.add(e);
			}

		if (button != null) {
			ContractorAudit conAudit = null;
			AuditType auditType = null;
			List<Employee> employees = new ArrayList<Employee>();
			
			
			if (selectedAudit != null)
				auditType = auditTypeDAO.find(selectedAudit);

			if (auditType == null) {
				addActionError("You must select an audit type.");
				return SUCCESS;
			}

			if (selectedOperator == null || selectedOperator == 0) {
				if (permissions.isOperatorCorporate())
					selectedOperator = permissions.getAccountId();
				else {
					addActionError("You must select an operator.");
					return SUCCESS;
				}
			}
			
			if (auditType.isEmployeeSpecificAudit()) {
				if (selectedEmployeeIds == null || selectedEmployeeIds.size() == 0) {
					addActionError("You must select an employee.");
					return SUCCESS;
				}
				for (int employeeId:selectedEmployeeIds) {
					Employee employee = dao.find(Employee.class, employeeId);
					if (employee != null) 
						employees.add(employee);
				}
				
				if (employees.size() == 0) {
					addActionError("You must select an employee.");
					return SUCCESS;
				}
			}
			
			if (auditType.isEmployeeSpecificAudit()) {
				for (Employee employee:employees) {
					conAudit = createAudit(auditType, employee);
				}
			} else {
				conAudit = createAudit(auditType, null);
			}

			if ("Create".equals(button)) {
				auditBuilder.buildAudits(contractor);

				if (auditType.isEmployeeSpecificAudit()) {
					this.redirect("EmployeeDashboard.action?id=" + id);
				} else {
					this.redirect("Audit.action?auditID=" + conAudit.getId());
				}
			}
		}

		return SUCCESS;
	}

	private ContractorAudit createAudit(AuditType auditType, Employee employee) {
		ContractorAudit conAudit = new ContractorAudit();

		conAudit.setAuditType(auditTypeDAO.find(selectedAudit));
		conAudit.setRequestingOpAccount(new OperatorAccount());
		conAudit.getRequestingOpAccount().setId(selectedOperator);

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
		
		if (employee != null) {
			conAudit.setEmployee(employee);
		}

		if (!Strings.isEmpty(auditFor))
			conAudit.setAuditFor(auditFor);

		conAudit.setManuallyAdded(true);
		conAudit.setAuditColumns(permissions);
		conAudit.setContractorAccount(contractor);
		
		contractor.getAudits().add(conAudit);

		auditDao.save(conAudit);
		
		addNote(conAudit.getContractorAccount(), "Added " + conAudit.getAuditType().getName().toString()
				+ " manually", NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));

		return conAudit;
	}

	@Override
	public Integer getSelectedAudit() {
		if (selectedAudit == null && getManuallyAddAudits().size() == 1)
			selectedAudit = getManuallyAddAudits().iterator().next().getId();
		return selectedAudit;
	}

	public List<Integer> getSelectedEmployeeIds() {
		return selectedEmployeeIds;
	}

	public void setSelectedEmployeeIds(List<Integer> selectedEmployeeIds) {
		this.selectedEmployeeIds = selectedEmployeeIds;
	}

	public List<Employee> getEmployeesLeftList() {
		return employeesLeftList;
	}

	public void setEmployeesLeftList(List<Employee> employeesLeftList) {
		this.employeesLeftList = employeesLeftList;
	}

	public List<Employee> getEmployeesRightList() {
		return employeesRightList;
	}

	public void setEmployeesRightList(List<Employee> employeesRightList) {
		this.employeesRightList = employeesRightList;
	}

}
