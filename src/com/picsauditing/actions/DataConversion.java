package com.picsauditing.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditFileDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeClassification;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.PicsOrganizerVersion;
import com.picsauditing.util.Strings;

/**
 * This is a generic data conversion utility that we should use when releasing.
 * 
 * This should be called once when releasing.
 */
@SuppressWarnings("serial")
public class DataConversion extends PicsActionSupport {
	@Autowired
	private AppPropertyDAO appPropertyDAO;
	@Autowired
	private ContractorAuditDAO auditDao;
	@Autowired
	private ContractorAuditFileDAO contractorAuditFileDAO;
	@Autowired
	private EmployeeDAO employeeDAO;

	@Anonymous
	public String execute() throws Exception {
		if (applicationNeedsUpgrade()) {
			addActionMessage("Database needs upgrading to " + PicsOrganizerVersion.getVersion());
			return SUCCESS;
		} else {
			addAlertMessage("Application is already up to date");
			return BLANK;
		}
	}

	private boolean applicationNeedsUpgrade() {
		String versionMajor = appPropertyDAO.getProperty("VERSION.major");
		if (Strings.isEmpty(versionMajor))
			return true;

		String versionMinor = appPropertyDAO.getProperty("VERSION.minor");
		if (Strings.isEmpty(versionMajor))
			return true;

		if (PicsOrganizerVersion.greaterThan(Integer.parseInt(versionMajor), Integer.parseInt(versionMinor))) {
			return true;
		}
		return false;
	}

	@Anonymous
	public String upgrade() throws Exception {
		if (!applicationNeedsUpgrade()) {
			addActionError("Application is already up to date");
			return BLANK;
		}
		long startTime = System.currentTimeMillis();
		convertEmployeeGuard();
		// updateDatabaseVersions();
		long endTime = System.currentTimeMillis();
		addActionMessage("Data conversion completed successfully in " + (endTime - startTime) + " ms");
		return BLANK;
	}

	private void updateDatabaseVersions() {
		appPropertyDAO.setProperty("VERSION.major", PicsOrganizerVersion.major + "");
		appPropertyDAO.setProperty("VERSION.minor", PicsOrganizerVersion.minor + "");
	}

	/*
	 * Parse out employee create emp records copy audit & files link emp and uadit
	 */
	private void convertEmployeeGuard() {
		List<ContractorAudit> auditList = auditDao.findWhere(0, "auditType.id IN (29) OR id IN (43413,86061)", "");
		for (ContractorAudit conAudit : auditList) {
			conAudit = auditDao.find(conAudit.getId());
			
			if (conAudit.getAuditType().getId() == 17)
			{
				convertIntegrityManagement(conAudit);
			}
			else if (conAudit.getAuditType().getId() == 29)
			{
				for (AuditData ad : conAudit.getData()) {
					if (ad.getQuestion().getId() == 2385) {
						convertImplementationAuditPlusAudit(conAudit, ad);
					}
				}
			}
		}
	}

	private void convertIntegrityManagement(ContractorAudit conAudit) {
		int oldConAuditID = conAudit.getId();
		ArrayList<String> employees = parseEmployees(conAudit.getAuditFor());
		
		for (int i = 0; i < employees.size(); i++) {
			String employee = employees.get(i);
			String title = StringUtils.trim(StringUtils.substring(employee,
					StringUtils.lastIndexOf(employee, "/") + 1));
			String name = StringUtils.trim(StringUtils.substring(employee, 0,
					StringUtils.lastIndexOf(employee, "/")));
			
			String firstName = StringUtils.trim(StringUtils.substring(name, 0,
					StringUtils.lastIndexOf(name, " ")));
			String lastName = StringUtils.trim(StringUtils.substring(name,
					StringUtils.lastIndexOf(name, " ") + 1));
			Employee newEmployee = new Employee();
			newEmployee.setClassification(EmployeeClassification.FullTime);
			newEmployee.setFirstName(firstName);
			newEmployee.setLastName(lastName);
			newEmployee.setTitle(title);
			newEmployee.setAccount(conAudit.getContractorAccount());
			employeeDAO.save(newEmployee);

			int firstEmployee = 0;
			if (i != firstEmployee) {
				Map<Integer, AuditData> preToPostAuditDataIdMapper = new HashMap<Integer, AuditData>();
				auditDao.copyAuditForNewEmployee(conAudit, newEmployee, preToPostAuditDataIdMapper);

				copyAuditQuestionFiles(preToPostAuditDataIdMapper, oldConAuditID);

				copyAuditFiles(oldConAuditID, conAudit);
			} else {
				conAudit.setEmployee(newEmployee);
				auditDao.save(conAudit);
			}
		}
	}

	private void convertImplementationAuditPlusAudit(ContractorAudit conAudit, AuditData data) {
		int oldConAuditID = conAudit.getId();
		ArrayList<String> employees = parseEmployees(data.getAnswer());

		for (int i = 0; i < employees.size(); i++) {
			String employeeName = employees.get(i);
			String firstName = StringUtils.trim(StringUtils.substring(employeeName, 0,
					StringUtils.lastIndexOf(employeeName, " ")));
			String lastName = StringUtils.trim(StringUtils.substring(employeeName,
					StringUtils.lastIndexOf(employeeName, " ") + 1));
			Employee newEmployee = new Employee();
			newEmployee.setClassification(EmployeeClassification.FullTime);
			newEmployee.setFirstName(firstName);
			newEmployee.setLastName(lastName);
			newEmployee.setAccount(conAudit.getContractorAccount());
			employeeDAO.save(newEmployee);

			int firstEmployee = 0;
			if (i != firstEmployee) {
				Map<Integer, AuditData> preToPostAuditDataIdMapper = new HashMap<Integer, AuditData>();
				auditDao.copyAuditForNewEmployee(conAudit, newEmployee, preToPostAuditDataIdMapper);

				copyAuditQuestionFiles(preToPostAuditDataIdMapper, oldConAuditID);

				copyAuditFiles(oldConAuditID, conAudit);
			} else {
				conAudit.setEmployee(newEmployee);
				auditDao.save(conAudit);
			}
		}
		
		if (employees.size() == 0)
		{
			Employee newEmployee = new Employee();
			newEmployee.setClassification(EmployeeClassification.FullTime);
			newEmployee.setFirstName("Missing Name");
			newEmployee.setLastName("Missing Name");
			newEmployee.setAccount(conAudit.getContractorAccount());
			employeeDAO.save(newEmployee);

			conAudit.setEmployee(newEmployee);
			auditDao.save(conAudit);
		}
	}

	private ArrayList<String> parseEmployees(String employees) {
		if (employees == null) {
			return new ArrayList<String>();
		}

		employees = StringUtils.trim(employees);
		ArrayList<String> employeeList = new ArrayList<String>();

		if (StringUtils.contains(employees, "\n")) {
			String[] eList = employees.split("\n");

			for (int i = 0; i < eList.length; i++) {
				eList[i] = StringUtils.trim(StringUtils.substring(eList[i], StringUtils.indexOf(eList[i], '.') + 1));

				String firstAndLast = "";
				if (StringUtils.contains(eList[i], ",")) {
					String[] empName = eList[i].split(",");
					firstAndLast = empName[1] + " " + empName[0];
				} else {
					firstAndLast = eList[i];
				}

				eList[i] = StringUtils.trim(firstAndLast);
				employeeList.add(eList[i]);
			}

		} else {
			String[] eList = employees.split(",");

			for (int i = 0; i < eList.length; i++) {
				eList[i] = StringUtils.trim(StringUtils.substring(eList[i], StringUtils.indexOf(eList[i], '.') + 1));
				employeeList.add(eList[i]);
			}
		}

		return employeeList;
	}

	private void copyAuditQuestionFiles(Map<Integer, AuditData> preToPostAuditDataIdMapper, int oldConAuditID) {
		ContractorAudit oldConAudit = auditDao.find(oldConAuditID);

		for (AuditData auditData : oldConAudit.getData()) {

			if (auditData.getQuestion().getQuestionType().equals("File")) {

				AuditData newAnswer = preToPostAuditDataIdMapper.get(auditData.getId());

				String newFileBase = "files/"
						+ FileUtils.thousandize(preToPostAuditDataIdMapper.get(auditData.getId()).getId());
				String newFileName = "data_" + auditData.getId();

				String oldFileBase = "files/" + FileUtils.thousandize(auditData.getId());
				String oldFileName = "data_" + auditData.getId() + "." + newAnswer.getAnswer();

				File oldFile = new File(getFtpDir() + "/" + oldFileBase, oldFileName);
				try {
					FileUtils.copyFile(oldFile, getFtpDir(), newFileBase, newFileName, newAnswer.getAnswer(), true);
				} catch (Exception couldntCopyTheFile) {
					couldntCopyTheFile.printStackTrace();
				}
			}
		}
	}

	private void copyAuditFiles(int oldConAuditID, ContractorAudit conAudit) {
		List<ContractorAuditFile> auditFiles = contractorAuditFileDAO.findByAudit(oldConAuditID);
		for (ContractorAuditFile caf : auditFiles) {
			ContractorAuditFile contractorAuditFile = new ContractorAuditFile();

			contractorAuditFile.setAudit(conAudit);
			contractorAuditFile.setReviewed(caf.isReviewed());
			contractorAuditFile.setDescription(caf.getDescription());
			contractorAuditFile.setFileType(caf.getFileType());
			contractorAuditFile.setAuditColumns(permissions);

			contractorAuditFile = contractorAuditFileDAO.save(contractorAuditFile);

			int fileId = contractorAuditFile.getId();

			String newFileBase = "files/" + FileUtils.thousandize(fileId);
			String newFileName = getFileName(fileId);

			File[] files = getFiles(caf.getId());
			if (files != null && files.length == 1) {
				try {
					FileUtils.copyFile(files[0], getFtpDir(), newFileBase, newFileName, caf.getFileType(), true);
				} catch (Exception couldntCopyTheFile) {
					couldntCopyTheFile.printStackTrace();
				}
			}
		}
	}

	private String getFileName(int fileID) {
		return PICSFileType.audit + "_" + fileID;
	}

	private File[] getFiles(int certID) {
		File dir = new File(getFtpDir() + "/files/" + FileUtils.thousandize(certID));
		return FileUtils.getSimilarFiles(dir, getFileName(certID));
	}

}
