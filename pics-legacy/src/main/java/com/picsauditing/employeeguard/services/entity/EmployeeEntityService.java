package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.PhotoForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeEntityService implements EntityService<Employee, Integer>, Searchable<Employee> {

	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private PhotoUtil photoUtil;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private RoleEmployeeDAO roleEmployeeDAO;

	/* All Find Methods */

	@Override
	public Employee find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return employeeDAO.find(id);
	}

	public Employee find(final int id, final int accountId) {
		return employeeDAO.findEmployeeByAccount(id, accountId);
	}

	public List<Employee> getEmployeesForAccount(final int accountId) {
		return employeeDAO.findByAccount(accountId);
	}

	public long getNumberOfEmployeesForAccount(final int accountId) {
		return employeeDAO.findEmployeeCount(accountId);
	}

	public List<Employee> getEmployeesForAccounts(final Collection<Integer> accountIds) {
		return employeeDAO.findByAccounts(accountIds);
	}

	public List<Employee> getEmployeesForProjects(final List<Project> projects) {
		return employeeDAO.findByProjects(projects);
	}

	public Map<Project, Set<Employee>> getEmployeesByProject(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectRoleEmployeeDAO.findByProjects(projects),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Project, Employee>() {

					@Override
					public Project getKey(ProjectRoleEmployee projectRoleEmployee) {
						return projectRoleEmployee.getProjectRole().getProject();
					}

					@Override
					public Employee getValue(ProjectRoleEmployee projectRoleEmployee) {
						return projectRoleEmployee.getEmployee();
					}
				});
	}

	public Map<Role, Set<Employee>> getEmployeesByProjectRoles(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				projectRoleEmployeeDAO.findByProjects(projects),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Role, Employee>() {
					@Override
					public Role getKey(ProjectRoleEmployee entity) {
						return entity.getProjectRole().getRole();
					}

					@Override
					public Employee getValue(ProjectRoleEmployee entity) {
						return entity.getEmployee();
					}
				}
		);
	}

	public Map<Role, Set<Employee>> getEmployeesBySiteRoles(final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyMap();
		}

		List<Employee> employeesAssignedToSite = employeeDAO.findEmployeesAssignedToSites(siteIds);
		if (CollectionUtils.isEmpty(employeesAssignedToSite)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				roleEmployeeDAO.findByEmployeesAndSiteIds(employeesAssignedToSite, siteIds),
				new PicsCollectionUtil.EntityKeyValueConvertable<RoleEmployee, Role, Employee>() {
					@Override
					public Role getKey(RoleEmployee entity) {
						return entity.getRole();
					}

					@Override
					public Employee getValue(RoleEmployee entity) {
						return entity.getEmployee();
					}
				}
		);
	}

	public List<Employee> getEmployeesAssignedToSite(final Collection<Integer> contractorIds, final int siteId) {
		if (CollectionUtils.isEmpty(contractorIds)) {
			return Collections.emptyList();
		}

		return employeeDAO.findEmployeesAssignedToSiteForContractors(contractorIds, siteId);
	}

	/**
	 * Returns the count of Employees with no profile.
	 *
	 * @param accountId
	 * @return
	 */
	public int getRequestedEmployeeCount(final int accountId) {
		return employeeDAO.findRequestedEmployees(accountId);
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
	public Employee save(Employee employee, final EntityAuditInfo entityAuditInfo) {
		employee = EntityHelper.setCreateAuditFields(employee, entityAuditInfo);
		return employeeDAO.save(employee);
	}

	public void save(final Collection<Employee> employees, final EntityAuditInfo entityAuditInfo) {
		EntityHelper.setCreateAuditFields(employees, entityAuditInfo);
		employeeDAO.save(employees);
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

	public void delete(final int id, final int accountId) {
		Employee employee = find(id, accountId);
		delete(employee);
	}

	/* Additional Methods */

	public Employee updatePhoto(final PhotoForm photoForm, final String directory, final int id,
								final int accountId) throws Exception {

		String extension = FileUtils.getExtension(photoForm.getPhotoFileName()).toLowerCase();

		if (photoUtil.isValidExtension(extension)) {
			String filename = PICSFileType.employee_photo.filename(id) + "-" + accountId;
			photoUtil.sendPhotoToFilesDirectory(photoForm.getPhoto(), directory, id, extension, filename);
		} else {
			throw new IllegalArgumentException("Invalid file format");
		}

		return find(id, accountId);
	}
}
