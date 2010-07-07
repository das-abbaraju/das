package com.picsauditing.actions.employees;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.JobTaskDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeQualification;
import com.picsauditing.jpa.entities.JobTask;

@SuppressWarnings("serial")
public class EmployeeQualificationCron extends PicsActionSupport {

	private JobTaskDAO taskDAO;
	private EmployeeDAO employeeDAO;
	private int employeeID;

	public EmployeeQualificationCron(EmployeeDAO employeeDAO, JobTaskDAO taskDAO) {
		this.employeeDAO = employeeDAO;
		this.taskDAO = taskDAO;
	}

	@Override
	public String execute() throws Exception {

		if (button != null) {
			if (button.equals("employee")) {
				if (employeeID > 0) {
					Employee employee = employeeDAO.find(employeeID);
					calculate(employee);
				} else {
					String where = "";
					List<Employee> employeesToRecalculate = employeeDAO.findWhere(where, 10);

					for (Employee employee : employeesToRecalculate) {
						calculate(employee);
					}
				}
			}
		}
		return SUCCESS;
	}

	@Transactional
	private void calculate(Employee employee) {
		employee.getAssessmentResults();
		employee.getEmployeeQualifications();
		Iterator<EmployeeQualification> iterator = employee.getEmployeeQualifications().iterator();
		while (iterator.hasNext()) {
			EmployeeQualification eq = iterator.next();
			iterator.remove();
			employeeDAO.remove(eq);
		}
		
		List<JobTask> applicableTasks = taskDAO.findByEmployee(employee.getId());
		for (JobTask jobTask : applicableTasks) {
			Map<Date, Boolean> qualificationHistory = jobTask.reconstructQualificationHistory(employee
					.getAssessmentResults());

			EmployeeQualification previousEq = null;
			for (Date effectiveDate : qualificationHistory.keySet()) {
				Boolean qualified = qualificationHistory.get(effectiveDate);
				if (previousEq != null)
					previousEq.setExpirationDate(effectiveDate);
				if (qualified != null) {
					EmployeeQualification eq = new EmployeeQualification();
					eq.setEmployee(employee);
					eq.setTask(jobTask);
					eq.setEffectiveDate(effectiveDate);
					eq.setExpirationDate(EmployeeQualification.END_OF_TIME);
					eq.setAuditColumns();
					eq.setQualified(qualified);
					employee.getEmployeeQualifications().add(eq);
					previousEq = eq;
				}
			}
		}
		employeeDAO.save(employee);
	}

	public int getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(int employeeID) {
		this.employeeID = employeeID;
	}

}
