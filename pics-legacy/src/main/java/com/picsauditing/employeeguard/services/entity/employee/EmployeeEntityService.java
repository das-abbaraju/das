package com.picsauditing.employeeguard.services.entity.employee;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.daos.softdeleted.SoftDeletedEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.PhotoForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.entity.EntityService;
import com.picsauditing.employeeguard.services.entity.Searchable;
import com.picsauditing.employeeguard.services.entity.util.file.UploadResult;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EmployeeEntityService implements EntityService<Employee, Integer>, Searchable<Employee> {

	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private EmployeeImportExportProcess employeeImportExportProcess;
	@Autowired
	private PhotoUtil photoUtil;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;
	@Autowired
	private SoftDeletedEmployeeDAO softDeletedEmployeeDAO;

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

	public int getNumberOfEmployeesForAccount(final int accountId) {
		return (int) employeeDAO.findEmployeeCount(accountId);
	}

	public List<Employee> getEmployeesForAccounts(final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return employeeDAO.findByAccounts(accountIds);
	}

	public List<Employee> getEmployeesForProjects(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		return employeeDAO.findByProjects(projects);
	}

	public Map<Project, Set<Employee>> getEmployeesByProjects(final Collection<Project> projects) {
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

	public Map<Role, Set<Employee>> getEmployeesByProjectRoles(final Project project) {
		return getEmployeesByProjectRoles(Arrays.asList(project));
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
		return PicsCollectionUtil.convertToMapOfSets(
				siteAssignmentDAO.findBySiteIds(siteIds),
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Role, Employee>() {
					@Override
					public Role getKey(SiteAssignment entity) {
						return entity.getRole();
					}

					@Override
					public Employee getValue(SiteAssignment entity) {
						return entity.getEmployee();
					}
				}
		);
	}

	public List<Employee> getEmployeesAssignedToSite(final int contractorId, final int siteId) {
		return getEmployeesAssignedToSites(Arrays.asList(contractorId), Arrays.asList(siteId));
	}

	public List<Employee> getEmployeesAssignedToSite(final Collection<Integer> contractorIds, final int siteId) {
		return getEmployeesAssignedToSites(contractorIds, Arrays.asList(siteId));
	}

	public List<Employee> getEmployeesAssignedToSites(final Collection<Integer> contractorIds, final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(contractorIds) || CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyList();
		}

		List<Employee> employees = new ArrayList<>(employeeDAO.findEmployeesAssignedToSites(siteIds));
		CollectionUtils.filter(employees, new GenericPredicate<Employee>() {
			@Override
			public boolean evaluateEntity(Employee employee) {
				return contractorIds.contains(employee.getAccountId());
			}
		});

		return employees;
	}

	public Map<Integer, Employee> getEmployeesByContractorId(final Profile profile) {
		return PicsCollectionUtil.convertToMap(employeeDAO.findByProfile(profile),
				new PicsCollectionUtil.MapConvertable<Integer, Employee>() {

					@Override
					public Integer getKey(Employee employee) {
						return employee.getAccountId();
					}
				});
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

	public Map<Project, Set<Employee>> getAllProjectsByEmployees(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				projectRoleEmployeeDAO.findByEmployees(employees),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Project, Employee>() {
					@Override
					public Project getKey(ProjectRoleEmployee entity) {
						return entity.getProjectRole().getProject();
					}

					@Override
					public Employee getValue(ProjectRoleEmployee entity) {
						return entity.getEmployee();
					}
				});
	}

	public Map<Employee, Set<Integer>> getEmployeeSiteAssignments(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(siteAssignmentDAO.findByEmployees(employees),
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Employee, Integer>() {

					@Override
					public Employee getKey(SiteAssignment siteAssignment) {
						return siteAssignment.getEmployee();
					}

					@Override
					public Integer getValue(SiteAssignment siteAssignment) {
						return siteAssignment.getSiteId();
					}
				});
	}

	public Map<Integer, Employee> getContractorEmployees(final Profile profile) {
		return PicsCollectionUtil.convertToMap(
				profile.getEmployees(),
				new PicsCollectionUtil.MapConvertable<Integer, Employee>() {

					@Override
					public Integer getKey(Employee entity) {
						return entity.getAccountId();
					}
				});
	}

	public Set<Integer> getAllSiteIdsForEmployeeAssignments(final Employee employee) {
		return getAllSiteIdsForEmployeeAssignments(Arrays.asList(employee));
	}

	public Set<Integer> getAllSiteIdsForEmployeeAssignments(final Collection<Employee> employees) {
		List<SiteAssignment> siteAssignments = siteAssignmentDAO.findByEmployees(employees);
		if (CollectionUtils.isEmpty(siteAssignments)) {
			return Collections.emptySet();
		}

		Set<Integer> siteIds = new HashSet<>();
		for (SiteAssignment siteAssignment : siteAssignments) {
			siteIds.add(siteAssignment.getSiteId());
		}

		return siteIds;
	}

	public Set<Integer> getEmployeeContractorsForSite(final int siteId, final int employeeId) {
		return new HashSet<>(employeeDAO.findContractorsForEmployeeBySite(siteId, employeeId));
	}

	public Set<Employee> getEmployeesAssignedToProject(final int projectId) {
		return new HashSet<>(employeeDAO.findByProjectId(projectId));
	}

	public List<Employee> getEmployeesAssignedToSiteRole(final Set<Integer> contractorIds,
														 final int siteId,
														 final Role role) {
		return employeeDAO.findEmployeesAssignedToSiteRole(contractorIds, siteId, role);
	}

	/* All Search Methods */

	@Override
	public List<Employee> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return employeeDAO.search(searchTerm, accountId);
	}

	/* All Save Methods */

	@Override
	public Employee save(Employee employee, final EntityAuditInfo entityAuditInfo) {
		employee = EntityHelper.setCreateAuditFields(employee, entityAuditInfo);
		return employeeDAO.save(employee);
	}

	public void save(final Collection<Employee> employees, final EntityAuditInfo entityAuditInfo) {
		if (CollectionUtils.isEmpty(employees)) {
			return;
		}

		for (Employee employee : employees) {
			if (Strings.isEmpty(employee.getSlug())) {
				employee.setSlug(generateSlug(employee));
			}
		}

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

	public void linkEmployeeToProfile(SoftDeletedEmployee employee, final Profile profile) {
		employee.setProfile(profile);
		EntityHelper.setUpdateAuditFields(employee, Identifiable.SYSTEM, new Date());
		softDeletedEmployeeDAO.save(employee);
	}

	public byte[] employeeImportTemplate() {
		return new EmployeeImportTemplate().template();
	}

	public UploadResult<Employee> importEmployees(final int contractorId,
												  final File file,
												  final String uploadFileName) {
		return employeeImportExportProcess.importEmployees(contractorId, file, uploadFileName);
	}

	public byte[] exportEmployees(final int contractorId) throws IOException {
		List<Employee> contractorEmployees = getEmployeesForAccount(contractorId);

		return employeeImportExportProcess.exportEmployees(contractorEmployees);
	}
}
