package com.picsauditing.actions.employees;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.OperatorCompetencyEmployeeFile;
import com.picsauditing.report.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EmployeeSkillsTraining extends PicsActionSupport {
	public static final String CURRENT = "EmployeeSkillsTraining.Current";
	public static final String EXPIRED = "EmployeeSkillsTraining.Expired";

	@Autowired
	private EmployeeDAO employeeDAO;

	private Employee employee;

	private List<OperatorCompetency> competenciesMissingDocumentation;
	private Map<String, List<OperatorCompetencyEmployeeFile>> filesByStatus;

	@Override
	public String execute() throws Exception {
		if (employee == null) {
			throw new RecordNotFoundException("Employee");
		}

		return SUCCESS;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<OperatorCompetency> getCompetenciesMissingDocumentation() {
		if (competenciesMissingDocumentation == null) {
			competenciesMissingDocumentation = new ArrayList<>();

			for (EmployeeCompetency employeeCompetency : employee.getEmployeeCompetencies()) {
				if (employeeCompetency.isMissingDocumentation()) {
					competenciesMissingDocumentation.add(employeeCompetency.getCompetency());
				}
			}
		}

		return competenciesMissingDocumentation;
	}

	public Map<String, List<OperatorCompetencyEmployeeFile>> getFilesByStatus() {
		if (filesByStatus == null) {
			filesByStatus = new TreeMap<>();

			for (OperatorCompetencyEmployeeFile employeeFile : employee.getCompetencyFiles()) {
				if (!employeeFile.isExpired()) {
					if (filesByStatus.get(CURRENT) == null) {
						filesByStatus.put(CURRENT, new ArrayList<OperatorCompetencyEmployeeFile>());
					}

					filesByStatus.get(CURRENT).add(employeeFile);
				} else {
					if (filesByStatus.get(EXPIRED) == null) {
						filesByStatus.put(EXPIRED, new ArrayList<OperatorCompetencyEmployeeFile>());
					}

					filesByStatus.get(EXPIRED).add(employeeFile);
				}
			}
		}

		return filesByStatus;
	}
}
