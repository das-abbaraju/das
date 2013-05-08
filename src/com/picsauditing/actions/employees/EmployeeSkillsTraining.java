package com.picsauditing.actions.employees;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.OperatorCompetencyEmployeeFile;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.util.Strings;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EmployeeSkillsTraining extends PicsActionSupport {
	public static final String CURRENT = "EmployeeSkillsTraining.Current";
	public static final String EXPIRED = "EmployeeSkillsTraining.Expired";

	private Employee employee;
	private OperatorCompetencyEmployeeFile employeeFile;

	private List<OperatorCompetency> competenciesMissingDocumentation;
	private Map<String, List<OperatorCompetencyEmployeeFile>> filesByStatus;

	@Override
	public String execute() throws Exception {
		if (!canViewPage()) {
			if (permissions.isContractor()) {
				throw new NoRightsException(OpPerms.ContractorSafety, OpType.View);
			} else {
				throw new NoRightsException(OpPerms.UploadEmployeeDocumentation, OpType.View);
			}
		}

		if (employee == null) {
			throw new RecordNotFoundException("Employee");
		}

		return SUCCESS;
	}

	private boolean canViewPage() {
		return permissions.isOperator() && permissions.has(OpPerms.UploadEmployeeDocumentation)
				|| permissions.isContractor() && permissions.has(OpPerms.ContractorSafety)
				|| permissions.isPicsEmployee();
	}

	public String download() throws Exception {
		if (!canViewPage()) {
			if (permissions.isContractor()) {
				throw new NoRightsException(OpPerms.ContractorSafety, OpType.View);
			} else {
				throw new NoRightsException(OpPerms.UploadEmployeeDocumentation, OpType.View);
			}
		}

		if (employeeFile == null || Strings.isEmpty(employeeFile.getFileName()) || employeeFile.getFileContent() == null) {
			addActionError(getText("EmployeeSkillsTraining.MissingFile"));
			return SUCCESS;
		} else {
			fileContainer = new FileDownloadContainer.Builder()
					.contentType("text/csv")
					.contentDisposition("attachment; filename=" + employeeFile.getFileName())
					.fileInputStream(new ByteArrayInputStream(employeeFile.getFileContent())).build();
			return FILE_DOWNLOAD;
		}
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public OperatorCompetencyEmployeeFile getEmployeeFile() {
		return employeeFile;
	}

	public void setEmployeeFile(OperatorCompetencyEmployeeFile employeeFile) {
		this.employeeFile = employeeFile;
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