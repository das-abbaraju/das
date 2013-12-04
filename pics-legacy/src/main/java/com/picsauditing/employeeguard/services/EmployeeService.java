package com.picsauditing.employeeguard.services;

import au.com.bytecode.opencsv.CSVWriter;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.softdeleted.SoftDeletedEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.PersonalInformationForm;
import com.picsauditing.employeeguard.forms.PhotoForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeForm;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class EmployeeService {
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private PhotoUtil photoUtil;
	@Autowired
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;

	public Employee findEmployee(final String id, final int accountId) {
		int employeeId = NumberUtils.toInt(id);

		return employeeDAO.findEmployeeByAccount(employeeId, accountId);
	}

	public List<Employee> getEmployeesForAccount(final int accountId) {
		return employeeDAO.findByAccount(accountId);
	}

    public long getNumberOfEmployeesForAccount(final int accountId) {
        return employeeDAO.findEmployeeCount(accountId);
    }

	public List<Employee> getEmployeesForAccounts(final List<Integer> accountIds) {
		return employeeDAO.findByAccounts(accountIds);
	}

	public List<Employee> getEmployeesByProjects(final List<Project> projects) {
		return employeeDAO.findByProjects(projects);
	}

	public Employee save(Employee employee, final int accountId, final int appUserId) throws Exception {
		Date now = new Date();

		setEmployeeAuditingFields(employee, accountId, appUserId);
		setPersistedEntitiesOnJoinTables(employee, accountId);
		EntityHelper.setCreateAuditFields(employee.getGroups(), appUserId, now);

		employee = employeeDAO.save(employee);
		accountSkillEmployeeService.linkEmployeeToSkills(employee, appUserId, now);

		return employee;
	}

	public Employee save(final EmployeeForm employeeForm, final String directory, final int accountId, final int appUserId) throws Exception {
		Employee employee = save(employeeForm.buildEmployee(accountId), accountId, appUserId);

		if (employeeForm.getPhoto() != null) {
			updatePhoto(employeeForm, directory, employee.getId() + "", accountId);
		}

		return employee;
	}

	private void setPersistedEntitiesOnJoinTables(Employee employee, int accountId) {
		List<String> groupNames = new ArrayList<>();
		for (AccountGroupEmployee accountGroupEmployee : employee.getGroups()) {
			groupNames.add(accountGroupEmployee.getGroup().getName());
		}

		List<AccountGroup> persistedGroups = accountGroupDAO.findGroupByAccountIdAndNames(accountId, groupNames);
		for (AccountGroup persistedGroup : persistedGroups) {
			for (AccountGroupEmployee accountGroupEmployee : employee.getGroups()) {
				if (persistedGroup.getName().equals(accountGroupEmployee.getGroup().getName())) {
					accountGroupEmployee.setGroup(persistedGroup);
				}
			}
		}
	}

	private void setEmployeeAuditingFields(final Employee employee, int accountId, int appUserId) {
		employee.setAccountId(accountId);

		if (employee.getCreatedBy() == 0 || employee.getCreatedDate() == null) {
			EntityHelper.setCreateAuditFields(employee, appUserId, new Date());
		} else {
			EntityHelper.setUpdateAuditFields(employee, appUserId, new Date());
		}

		if (Strings.isEmpty(employee.getSlug())) {
			String hash = Strings.hashUrlSafe(employee.getAccountId() + employee.getEmail());
			employee.setSlug("EID-" + hash.substring(0, 8).toUpperCase());
		}
	}

	public void importEmployees(final File file, final int accountId, final int appUserId) throws Exception {
		EmployeeFileImportService fileImportService = new EmployeeFileImportService();
		fileImportService.importFile(file);

		List<Employee> processedEmployees = fileImportService.getEntities();
		for (Employee employee : processedEmployees) {
			setEmployeeAuditingFields(employee, accountId, appUserId);
		}

		employeeDAO.save(processedEmployees);
	}

	public byte[] exportEmployees(final int accountId) throws Exception {
		CSVWriter csvWriter = null;

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
			csvWriter = new CSVWriter(printWriter);

			addCsvHeader(csvWriter);

			List<Employee> employees = getEmployeesForAccount(accountId);
			for (Employee employee : employees) {
				csvWriter.writeNext(new String[]{
						employee.getFirstName(),
						employee.getLastName(),
						employee.getPositionName(),
						employee.getEmail(),
						employee.getPhone(),
						employee.getSlug()
				});
			}

			csvWriter.flush();
			return byteArrayOutputStream.toByteArray();
		} finally {
			safeCloseWriter(csvWriter);
		}
	}

	public byte[] exportTemplate() throws Exception {
		CSVWriter csvWriter = null;

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
			csvWriter = new CSVWriter(printWriter);

			addCsvHeader(csvWriter);
			csvWriter.flush();
			return byteArrayOutputStream.toByteArray();
		} finally {
			safeCloseWriter(csvWriter);
		}
	}

	private void addCsvHeader(CSVWriter csvWriter) {
		csvWriter.writeNext(new String[]{"First Name", "Last Name", "Title", "Email", "Phone", "Employee ID"});
	}

	private void safeCloseWriter(CSVWriter csvWriter) {
		try {
			if (csvWriter != null) {
				csvWriter.close();
			}
		} catch (Exception exception) {
			LOG.error("Exception closing resources", exception);
		}
	}

	public Employee updatePersonal(PersonalInformationForm personalInformationForm, String employeeId, int accountId, int appUserId) {
		Employee employeeToUpdate = findEmployee(employeeId, accountId);

		employeeToUpdate.setFirstName(personalInformationForm.getFirstName());
		employeeToUpdate.setLastName(personalInformationForm.getLastName());
		employeeToUpdate.setEmail(personalInformationForm.getEmail());
		employeeToUpdate.setPhone(personalInformationForm.getPhoneNumber());

		EntityHelper.setUpdateAuditFields(employeeToUpdate, appUserId, new Date());

		return employeeDAO.save(employeeToUpdate);
	}

	public Employee updateEmployment(EmployeeEmploymentForm employeeEmploymentForm, final String employeeId, final int accountId, final int appUserId) {
		Employee employeeInDatabase = findEmployee(employeeId, accountId);
		Date timestamp = new Date();
		Employee updatedEmployee = buildEmployeeFromForm(employeeEmploymentForm, employeeInDatabase, accountId);

		List<AccountGroupEmployee> accountGroupEmployees = IntersectionAndComplementProcess.intersection(
				updatedEmployee.getGroups(),
				employeeInDatabase.getGroups(),
				AccountGroupEmployee.COMPARATOR,
				new BaseEntityCallback(appUserId, new Date()));
		employeeInDatabase.setGroups(accountGroupEmployees);

		if (Strings.isEmpty(updatedEmployee.getSlug())) {
			String hash = Strings.hashUrlSafe(employeeInDatabase.getAccountId() + employeeInDatabase.getEmail());
			employeeInDatabase.setSlug("EID-" + hash.substring(0, 8).toUpperCase());
		} else {
			employeeInDatabase.setSlug(updatedEmployee.getSlug());
		}

		employeeInDatabase.setPositionName(updatedEmployee.getPositionName());

		EntityHelper.setUpdateAuditFields(employeeInDatabase, appUserId, timestamp);
		employeeInDatabase = employeeDAO.save(employeeInDatabase);

		accountSkillEmployeeService.linkEmployeeToSkills(employeeInDatabase, appUserId, timestamp);

		return employeeInDatabase;
	}

	public Employee updatePhoto(PhotoForm photoForm, String directory, String id, int accountId) throws Exception {
		String extension = FileUtils.getExtension(photoForm.getPhotoFileName()).toLowerCase();

		if (photoUtil.isValidExtension(extension)) {
			int employeeId = NumberUtils.toInt(id);
			String filename = PICSFileType.employee_photo.filename(employeeId) + "-" + accountId;
			photoUtil.sendPhotoToFilesDirectory(photoForm.getPhoto(), directory, employeeId, extension, filename);
		} else {
			throw new IllegalArgumentException("Invalid file format");
		}

		return findEmployee(id, accountId);
	}

	private Employee buildEmployeeFromForm(EmployeeEmploymentForm employeeEmploymentForm, Employee employeeFromDatabase, int accountId) {
		Employee employee = new Employee();
		employee.setSlug(employeeEmploymentForm.getEmployeeId());
		employee.setPositionName(employeeEmploymentForm.getTitle());

		if (ArrayUtils.isNotEmpty(employeeEmploymentForm.getGroups())) {
			List<AccountGroup> groups = accountGroupDAO.findGroupByAccountIdAndNames(accountId, Arrays.asList(employeeEmploymentForm.getGroups()));
			for (AccountGroup accountGroup : groups) {
				employee.getGroups().add(new AccountGroupEmployee(employeeFromDatabase, accountGroup));
			}
		}

		return employee;
	}

	private List<AccountGroupEmployee> getLinkedGroups(final Employee employeeInDatabase, final Employee updatedEmployee, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback<AccountGroupEmployee>(appUserId, new Date());
		List<AccountGroupEmployee> accountGroupEmployees = IntersectionAndComplementProcess.intersection(updatedEmployee.getGroups(),
				employeeInDatabase.getGroups(), AccountGroupEmployee.COMPARATOR, callback);

		List<String> groupNames = getGroupNames(accountGroupEmployees);

		if (CollectionUtils.isNotEmpty(groupNames)) {
			List<AccountGroup> accountGroups = accountGroupDAO.findGroupByAccountIdAndNames(updatedEmployee.getAccountId(), groupNames);

			for (AccountGroupEmployee accountGroupEmployee : accountGroupEmployees) {
				AccountGroup group = accountGroupEmployee.getGroup();
				int index = accountGroups.indexOf(group);
				if (index >= 0) {
					accountGroupEmployee.setGroup(accountGroups.get(index));
				}
			}
		}

		accountGroupEmployees.addAll(callback.getRemovedEntities());

		return accountGroupEmployees;
	}

	private List<String> getGroupNames(List<AccountGroupEmployee> accountGroupEmployees) {
		if (CollectionUtils.isEmpty(accountGroupEmployees)) {
			return Collections.emptyList();
		}

		List<String> groupNames = new ArrayList<>();
		for (AccountGroupEmployee accountGroupEmployee : accountGroupEmployees) {
			groupNames.add(accountGroupEmployee.getGroup().getName());
		}

		return groupNames;
	}

	public void delete(final String id, final int accountId, final int appUserId) {
		Employee employee = findEmployee(id, accountId);

		Date deletedDate = new Date();
		EntityHelper.softDelete(employee, appUserId, deletedDate);
		EntityHelper.softDelete(employee.getGroups(), appUserId, deletedDate);
		EntityHelper.softDelete(employee.getSkills(), appUserId, deletedDate);

		employeeDAO.save(employee);
	}

	public void hardDelete(final String id, final int accountId) {
		employeeDAO.delete(NumberUtils.toInt(id), accountId);
	}

	public List<Employee> search(final String searchTerm, final int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return employeeDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

	public void linkEmployeeToProfile(SoftDeletedEmployee employee, final Profile profile) {
		employee.setProfile(profile);
		EntityHelper.setUpdateAuditFields(employee, Identifiable.SYSTEM, new Date());
		softDeletedEmployeeDAO.save(employee);
	}
}
