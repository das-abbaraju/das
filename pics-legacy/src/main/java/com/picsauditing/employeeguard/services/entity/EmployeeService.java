package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

public class EmployeeService implements EntityService<Employee, Integer>, Searchable<Employee> {

	@Autowired
	private EmployeeDAO employeeDAO;

	/* All Find Methods */

	@Override
	public Employee find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return employeeDAO.find(id);
	}

	/* All Search Methods */

	@Override
	public List<Employee> search(final String searchTerm, final int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return employeeDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}

	/* All Save Methods */

	@Override
	public Employee save(Employee employee, final  EntityAuditInfo entityAuditInfo) {
		employee = EntityHelper.setCreateAuditFields(employee, entityAuditInfo);
		return employeeDAO.save(employee);
	}

	/* All Update Methods */

	@Override
	public Employee update(final Employee employee, final EntityAuditInfo entityAuditInfo) {
		Employee employeeToUpdate = find(employee.getId());
		employeeToUpdate = updatePersonalInformation(employeeToUpdate, employee);
		employeeToUpdate = updateEmployeeEmploymentInformation(employeeToUpdate, employee);

		employeeToUpdate = EntityHelper.setUpdateAuditFields(employeeToUpdate, entityAuditInfo);

		return employeeDAO.save(employeeToUpdate);
	}

	private Employee updatePersonalInformation(final Employee employeeToUpdate, final Employee employee) {
		employeeToUpdate.setFirstName(employee.getFirstName());
		employeeToUpdate.setLastName(employee.getLastName());
		employeeToUpdate.setEmail(employee.getEmail());
		employeeToUpdate.setPhone(employee.getPhone());
		return employeeToUpdate;
	}

	private Employee updateEmployeeEmploymentInformation(final Employee employeeToUpdate, final Employee employee) {
		employeeToUpdate.setSlug(generateSlug(employee));
		employeeToUpdate.setPositionName(employee.getPositionName());

		return employeeToUpdate;
	}

	private String generateSlug(final Employee employee) {
		if (Strings.isNotEmpty(employee.getSlug())) {
			return employee.getSlug();
		}

		String hash = Strings.hashUrlSafe(employee.getAccountId() + employee.getEmail());
		return "EID-" + hash.substring(0, 8).toUpperCase();
	}

//	public Employee updatePersonal(PersonalInformationForm personalInformationForm, String employeeId, int accountId, int appUserId) {
//		Employee employeeToUpdate = findEmployee(employeeId, accountId);
//
//		employeeToUpdate.setFirstName(personalInformationForm.getFirstName());
//		employeeToUpdate.setLastName(personalInformationForm.getLastName());
//		employeeToUpdate.setEmail(personalInformationForm.getEmail());
//		employeeToUpdate.setPhone(personalInformationForm.getPhoneNumber());
//
//		EntityHelper.setUpdateAuditFields(employeeToUpdate, appUserId, new Date());
//
//		return employeeDAO.save(employeeToUpdate);
//	}
//
//	public Employee updateEmployment(EmployeeEmploymentForm employeeEmploymentForm, final String employeeId, final int accountId, final int appUserId) {
//		Employee employeeInDatabase = findEmployee(employeeId, accountId);
//		Date timestamp = new Date();
//		Employee updatedEmployee = buildEmployeeFromForm(employeeEmploymentForm, employeeInDatabase, accountId);
//
//		List<GroupEmployee> groupEmployees = IntersectionAndComplementProcess.intersection(
//				updatedEmployee.getGroups(),
//				employeeInDatabase.getGroups(),
//				GroupEmployee.COMPARATOR,
//				new BaseEntityCallback(appUserId, new Date()));
//		employeeInDatabase.setGroups(groupEmployees);
//
//		if (Strings.isEmpty(updatedEmployee.getSlug())) {
//			String hash = Strings.hashUrlSafe(employeeInDatabase.getAccountId() + employeeInDatabase.getEmail());
//			employeeInDatabase.setSlug("EID-" + hash.substring(0, 8).toUpperCase());
//		} else {
//			employeeInDatabase.setSlug(updatedEmployee.getSlug());
//		}
//
//		employeeInDatabase.setPositionName(updatedEmployee.getPositionName());
//
//		EntityHelper.setUpdateAuditFields(employeeInDatabase, appUserId, timestamp);
//		employeeInDatabase = employeeDAO.save(employeeInDatabase);
//
//		accountSkillEmployeeService.linkEmployeeToSkills(employeeInDatabase, appUserId, timestamp);
//
//		return employeeInDatabase;
//	}

	/* All Delete Methods */

	@Override
	public void delete(final Employee employee) {
		if (employee == null) {
			throw new NullPointerException("employee cannot be null");
		}

		employeeDAO.delete(employee);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Employee employee = find(id);
		delete(employee);
	}
}
