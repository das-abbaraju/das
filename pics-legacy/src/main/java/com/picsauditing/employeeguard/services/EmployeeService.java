package com.picsauditing.employeeguard.services;

import au.com.bytecode.opencsv.CSVWriter;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
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
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
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
	private EmployeeEntityService employeeEntityService;
	@Deprecated
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;

	@Deprecated
	public Employee findEmployee(final String id) {
		return employeeEntityService.find(NumberUtils.toInt(id));
	}

	@Deprecated
	public Employee findEmployee(final String id, final int accountId) {
		return employeeEntityService.find(NumberUtils.toInt(id), accountId);
	}

	@Deprecated
	public List<Employee> getEmployeesForAccount(final int accountId) {
		return employeeEntityService.getEmployeesForAccount(accountId);
	}

	@Deprecated
	public long getNumberOfEmployeesForAccount(final int accountId) {
		return employeeEntityService.getNumberOfEmployeesForAccount(accountId);
	}

	@Deprecated
	public List<Employee> getEmployeesForAccounts(final Collection<Integer> accountIds) {
		return employeeEntityService.getEmployeesForAccounts(accountIds);
	}

	@Deprecated
	public List<Employee> getEmployeesByProjects(final List<Project> projects) {
		return employeeEntityService.getEmployeesByProjects(projects);
	}

	public List<Employee> getEmployeesAssignedToSite(final int accountId, final int siteId) {
		return getEmployeesAssignedToSite(Arrays.asList(accountId), siteId);
	}

	public List<Employee> getEmployeesAssignedToSite(final Collection<Integer> contractorIds, final int siteId) {
		return employeeDAO.findEmployeesAssignedToSite(contractorIds, siteId);
	}

	public List<Employee> getEmployeesAssignedToSiteByEmployeeProfile(final Collection<Integer> contractorIds,
																	  final int siteId,
																	  final Employee employee) {
		if (employee.getProfile() == null) {
			return Arrays.asList(employee);
		}

		return employeeDAO.findEmployeesAssignedToSiteByProfile(contractorIds, siteId, employee.getProfile().getId());
	}

	public List<Employee> getEmployeesAssignedToSiteRole(final Collection<Integer> contractorIds, final int siteId,
	                                                     final Role siteRole, final Role corporateRole) {
		return employeeDAO.findEmployeesAssignedToSiteRole(contractorIds, siteId, siteRole, corporateRole);
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
		for (GroupEmployee groupEmployee : employee.getGroups()) {
			groupNames.add(groupEmployee.getGroup().getName());
		}

		List<Group> persistedGroups = accountGroupDAO.findGroupByAccountIdAndNames(accountId, groupNames);
		for (Group persistedGroup : persistedGroups) {
			for (GroupEmployee groupEmployee : employee.getGroups()) {
				if (persistedGroup.getName().equals(groupEmployee.getGroup().getName())) {
					groupEmployee.setGroup(persistedGroup);
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

		List<GroupEmployee> groupEmployees = IntersectionAndComplementProcess.intersection(
				updatedEmployee.getGroups(),
				employeeInDatabase.getGroups(),
				GroupEmployee.COMPARATOR,
				new BaseEntityCallback(appUserId, new Date()));
		employeeInDatabase.setGroups(groupEmployees);

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

	@Deprecated
	public Employee updatePhoto(PhotoForm photoForm, String directory, String id, int accountId) throws Exception {
		return employeeEntityService.updatePhoto(photoForm, directory, NumberUtils.toInt(id), accountId);
	}

	private Employee buildEmployeeFromForm(EmployeeEmploymentForm employeeEmploymentForm, Employee employeeFromDatabase, int accountId) {
		Employee employee = new Employee();
		employee.setSlug(employeeEmploymentForm.getEmployeeId());
		employee.setPositionName(employeeEmploymentForm.getTitle());

		if (ArrayUtils.isNotEmpty(employeeEmploymentForm.getGroups())) {
			List<Group> groups = accountGroupDAO.findGroupByAccountIdAndNames(accountId, Arrays.asList(employeeEmploymentForm.getGroups()));
			for (Group group : groups) {
				employee.getGroups().add(new GroupEmployee(employeeFromDatabase, group));
			}
		}

		return employee;
	}

	private List<GroupEmployee> getLinkedGroups(final Employee employeeInDatabase, final Employee updatedEmployee, final int appUserId) {
		BaseEntityCallback callback = new BaseEntityCallback<GroupEmployee>(appUserId, new Date());
		List<GroupEmployee> groupEmployees = IntersectionAndComplementProcess.intersection(updatedEmployee.getGroups(),
				employeeInDatabase.getGroups(), GroupEmployee.COMPARATOR, callback);

		List<String> groupNames = getGroupNames(groupEmployees);

		if (CollectionUtils.isNotEmpty(groupNames)) {
			List<Group> groups = accountGroupDAO.findGroupByAccountIdAndNames(updatedEmployee.getAccountId(), groupNames);

			for (GroupEmployee groupEmployee : groupEmployees) {
				Group group = groupEmployee.getGroup();
				int index = groups.indexOf(group);
				if (index >= 0) {
					groupEmployee.setGroup(groups.get(index));
				}
			}
		}

		groupEmployees.addAll(callback.getRemovedEntities());

		return groupEmployees;
	}

	private List<String> getGroupNames(List<GroupEmployee> groupEmployees) {
		if (CollectionUtils.isEmpty(groupEmployees)) {
			return Collections.emptyList();
		}

		List<String> groupNames = new ArrayList<>();
		for (GroupEmployee groupEmployee : groupEmployees) {
			groupNames.add(groupEmployee.getGroup().getName());
		}

		return groupNames;
	}

	@Deprecated
	public void delete(final String id, final int accountId, final int appUserId) {
		employeeEntityService.delete(NumberUtils.toInt(id), accountId);
	}

	@Deprecated
	public List<Employee> search(final String searchTerm, final int accountId) {
		return employeeEntityService.search(searchTerm, accountId);
	}

	public void linkEmployeeToProfile(SoftDeletedEmployee employee, final Profile profile) {
		employee.setProfile(profile);
		EntityHelper.setUpdateAuditFields(employee, Identifiable.SYSTEM, new Date());
		softDeletedEmployeeDAO.save(employee);
	}
}