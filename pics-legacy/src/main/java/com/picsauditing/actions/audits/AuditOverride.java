package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorDocuments;
import com.picsauditing.audits.AuditBuilder;
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

		if (!isManuallyAddAudit()) {
			throw new NoRightsException("Cannot Manually Add Audits");
		}

        employeesRightList = new ArrayList<>();
		employeesLeftList = new ArrayList<>();
		for (Employee e : contractor.getEmployees()) {
			if (e.isActive())
				employeesLeftList.add(e);
		}

		if (button != null) {
			ContractorAudit conAudit = null;
			AuditType auditType = null;
			List<Employee> employees = new ArrayList<>();

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
				for (int employeeId : selectedEmployeeIds) {
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
				for (Employee employee : employees) {
					conAudit = createAudit(auditType, employee);
				}
			} else {
				conAudit = createAudit(auditType, null);
			}

			if ("Create".equals(button)) {
				auditBuilder.buildAudits(contractor);

				if (auditType.isEmployeeSpecificAudit()) {
					return this.setUrlForRedirect("EmployeeDashboard.action?id=" + id);
				} else {
					return this.setUrlForRedirect("Audit.action?auditID=" + conAudit.getId());
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
		
		updateAuditSubStatusForRejectedPolicy(cao);

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
		conAudit.setPreviousAudit(auditDao.findPreviousAudit(conAudit));
		contractor.getAudits().add(conAudit);

		auditDao.save(conAudit);

        int accountId = getViewableByAccountId(conAudit.getAuditType().getAccount());
		addNote(conAudit.getContractorAccount(), "Added " + conAudit.getAuditType().getName().toString() + " manually",
				NoteCategory.Audits, accountId);

		return conAudit;
	}

    private int getViewableByAccountId(Account account) {
        int id = getViewableByAccount(account);
        if (id == Account.EVERYONE && permissions.isOperatorCorporate()) {
            id = permissions.getAccountId();
        }
        return id;
    }

    private void updateAuditSubStatusForRejectedPolicy(ContractorAuditOperator cao) {
		if (cao.getAudit().getAuditType().getClassType().isPolicy() && cao.getStatus() == AuditStatus.Incomplete) {
			cao.setAuditSubStatus(AuditSubStatus.Other);
		}
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
