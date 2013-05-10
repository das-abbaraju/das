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
import com.picsauditing.util.URLUtils;

import java.io.ByteArrayInputStream;
import java.util.*;

public class EmployeeSkillsTraining extends PicsActionSupport {
	public static final String CURRENT = "EmployeeSkillsTraining.Current";
	public static final String EXPIRED = "EmployeeSkillsTraining.Expired";

	private Employee employee;
	private OperatorCompetencyEmployeeFile employeeFile;

	private List<OperatorCompetency> competenciesMissingDocumentation;
	private Map<String, List<OperatorCompetencyEmployeeFile>> filesByStatus;

	private URLUtils urlUtils;

	@Override
	public String execute() throws Exception {
		if (employee == null) {
			throw new RecordNotFoundException("Employee");
		}

		updateSkilledBasedOnDocumentation();

		return SUCCESS;
	}

	private void updateSkilledBasedOnDocumentation() {
		for (EmployeeCompetency employeeCompetency : employee.getEmployeeCompetencies()) {
			if (employeeCompetency.getCompetency().isRequiresDocumentation()) {
				employeeCompetency.setSkilled(employeeCompetency.isDocumentationValid());
				dao.save(employeeCompetency);
			}
		}
	}

	public boolean isCanAccessDocumentation() {
		return permissions.isOperatorCorporate() && permissions.has(OpPerms.UploadEmployeeDocumentation)
				|| permissions.isContractor() && permissions.has(OpPerms.ContractorSafety)
				|| permissions.isPicsEmployee();
	}

	public String download() throws NoRightsException {
		throwNoRightsIfNoDocumentAccess();

		if (employeeFileIsInvalid()) {
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

	public String delete() throws Exception {
		if (employeeFileIsInvalid()) {
			addActionError(getText("EmployeeSkillsTraining.MissingFile"));
			return SUCCESS;
		} else {
			int employeeID = employeeFile.getEmployee().getId();
			addActionMessage(getTextParameterized("EmployeeSkillsTraining.SuccessfullyDeleted",
					employeeFile.getFileName(), employeeFile.getCompetency().getLabel(),
					employeeFile.getEmployee().getDisplayName()));
			dao.remove(employeeFile);

			return setUrlForRedirect(urlUtils().getActionUrl("EmployeeSkillsTraining", "employee", employeeID));
		}
	}

	private boolean employeeFileIsInvalid() {
		return employeeFile == null || Strings.isEmpty(employeeFile.getFileName()) || employeeFile.getFileContent() == null;
	}

	private void throwNoRightsIfNoDocumentAccess() throws NoRightsException {
		if (!isCanAccessDocumentation()) {
			if (permissions.isContractor()) {
				throw new NoRightsException(OpPerms.ContractorSafety, OpType.View);
			} else {
				throw new NoRightsException(OpPerms.UploadEmployeeDocumentation, OpType.View);
			}
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

			for (String status : filesByStatus.keySet()) {
				Collections.sort(filesByStatus.get(status));
			}
		}

		return filesByStatus;
	}

	private URLUtils urlUtils() {
		if (urlUtils == null) {
			urlUtils = new URLUtils();
		}

		return urlUtils;
	}
}
