package com.picsauditing.actions.employees;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.OperatorCompetencyEmployeeFile;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Date;
import java.util.List;

public class EmployeeDocumentationFileUpload extends PicsActionSupport {
	@Autowired
	private EmployeeDAO employeeDAO;

	private Employee employee;
	private OperatorCompetency competency;
	private File file;
	private String fileContentType = null;
	private String fileFileName = null;
	private Date expiration;

	private List<OperatorCompetency> competenciesMissingDocumentation;

	@Override
	public String execute() throws Exception {
		if (employee == null || competency == null) {
			addActionError(getText("EmployeeDocumentFileUpload.MissingRequiredInformation"));
		}

		return SUCCESS;
	}

	public String save() throws Exception {
		if (employee != null && competency != null && Strings.isNotEmpty(fileFileName) && expiration != null) {
			saveEmployeeFile();

			addActionMessage(getTextParameterized("EmployeeDocumentFileUpload.SuccessfullyUploadedFileFor",
					fileFileName, competency.getLabel(), employee.getDisplayName()));
			URLUtils urlUtils = new URLUtils();
			String url = urlUtils.getActionUrl("EmployeeSkillsTraining", "employee", employee.getId());

			return setUrlForRedirect(url);
		} else {
			addActionError(getText("EmployeeDocumentFileUpload.MissingRequiredInformation"));
			return SUCCESS;
		}
	}

	private String getFileExtension(String fileFileName) {
		int extensionStart = fileFileName.lastIndexOf(".");
		String extension = fileFileName.substring(extensionStart + 1);
		return extension.toUpperCase();
	}

	private void saveEmployeeFile() throws Exception {
		OperatorCompetencyEmployeeFile employeeFile = new OperatorCompetencyEmployeeFile();
		employeeFile.setCompetency(competency);
		employeeFile.setEmployee(employee);
		employeeFile.setFileName(fileFileName);
		employeeFile.setFileType(getFileExtension(fileFileName));
		employeeFile.setFileContent(FileUtils.readFileToByteArray(file));
		employeeFile.setExpiration(expiration);
		employeeFile.setAuditColumns(permissions);

		employeeDAO.save(employeeFile);
		employee.getCompetencyFiles().add(employeeFile);
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public OperatorCompetency getCompetency() {
		return competency;
	}

	public void setCompetency(OperatorCompetency competency) {
		this.competency = competency;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
}
