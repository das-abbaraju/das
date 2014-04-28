package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.SiteAssignmentBuilder;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class RoleEntityService implements EntityService<Role, Integer>, Searchable<Role> {

	@Autowired
	private ProjectRoleDAO projectRoleDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;

	/* All Find Methods */

	@Override
	public Role find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return roleDAO.find(id);
	}

	public Role findByIdAndAccountId(final Integer id, final int accountId) {
		if (id == null || accountId <= 0) {
			throw new IllegalArgumentException("Invalid id and/or accountId=");
		}

		return roleDAO.findRoleByAccount(id, accountId);
	}

	public List<Role> findRolesForCorporateAccounts(final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return Collections.EMPTY_LIST;

		}

		return roleDAO.findByAccounts(accountIds);
	}

	public Map<Project, Set<Role>> getRolesForProjects(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectRoleDAO.findByProjects(projects),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRole, Project, Role>() {
					@Override
					public Project getKey(ProjectRole projectRole) {
						return projectRole.getProject();
					}

					@Override
					public Role getValue(ProjectRole projectRole) {
						return projectRole.getRole();
					}
				});
	}

	public Map<Project, Set<Role>> getRolesForProjectsAndEmployees(final Collection<Project> projects,
																   final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(projects) || CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectRoleDAO.findByProjectsAndEmployees(projects, employees),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRole, Project, Role>() {
					@Override
					public Project getKey(ProjectRole projectRole) {
						return projectRole.getProject();
					}

					@Override
					public Role getValue(ProjectRole projectRole) {
						return projectRole.getRole();
					}
				});
	}

	public Set<Role> getSiteRolesForEmployee(final Employee employee, final int siteId) {
		return new HashSet<>(roleDAO.findSiteRolesForEmployee(siteId, employee));
	}

	public Map<Employee, Set<Role>> getSiteRolesForEmployees(final Collection<Employee> employees, final int siteId) {
		return getSiteRolesForEmployees(employees, Arrays.asList(siteId));
	}

	public Map<Employee, Set<Role>> getSiteRolesForEmployees(final Collection<Employee> employees,
															 final Collection<Integer> siteIds) {

		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyMap();
		}

		List<SiteAssignment> siteAssignments = siteAssignmentDAO.findByEmployeesAndSiteIds(employees, siteIds);

		return PicsCollectionUtil.convertToMapOfSets(siteAssignments,
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Employee, Role>() {
					@Override
					public Employee getKey(SiteAssignment siteAssignment) {
						return siteAssignment.getEmployee();
					}

					@Override
					public Role getValue(SiteAssignment siteAssignment) {
						return siteAssignment.getRole();
					}
				});
	}

	public Map<Employee, Set<Role>> getProjectRolesForEmployees(final Collection<Employee> employees,
																final int siteId) {

		return getProjectRolesForEmployees(employees, Arrays.asList(siteId));
	}

	public Map<Employee, Set<Role>> getProjectRolesForEmployees(final Collection<Employee> employees,
																final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				projectRoleEmployeeDAO.findByEmployeesAndSiteIds(employees, siteIds),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Role>() {

					@Override
					public Employee getKey(ProjectRoleEmployee projectRoleEmployee) {
						return projectRoleEmployee.getEmployee();
					}

					@Override
					public Role getValue(ProjectRoleEmployee projectRoleEmployee) {
						return projectRoleEmployee.getProjectRole().getRole();
					}
				});
	}

	public Map<Employee, Set<Role>> findByProjectsAndEmployees(final Collection<Project> projects,
															   final Collection<Employee> employees) {

		if (CollectionUtils.isEmpty(projects) || CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				projectRoleEmployeeDAO.findByEmployeesAndProjects(employees, projects),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Role>() {

					@Override
					public Employee getKey(ProjectRoleEmployee entity) {
						return entity.getEmployee();
					}

					@Override
					public Role getValue(ProjectRoleEmployee entity) {
						return entity.getProjectRole().getRole();
					}
				});
	}

	/**
	 * Returns a map of SiteID to a set of Roles that the Employee has on that site.
	 *
	 * @param employees
	 * @return
	 */
	public Map<Integer, Set<Role>> getSiteRolesForEmployees(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(
				siteAssignmentDAO.findByEmployees(employees),
				new PicsCollectionUtil.EntityKeyValueConvertable<SiteAssignment, Integer, Role>() {

					@Override
					public Integer getKey(SiteAssignment entity) {
						return entity.getSiteId();
					}

					@Override
					public Role getValue(SiteAssignment entity) {
						return entity.getRole();
					}
				});
	}

	public Set<Role> getAllSiteRolesForEmployee(final Employee employee) {
		return new HashSet<>(roleDAO.findRolesForEmployee(employee));
	}

	/* All Search Methods */

	@Override
	public List<Role> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm) || accountId == 0) {
			return Collections.emptyList();
		}

		return roleDAO.search(searchTerm, accountId);
	}

	/* All Save Methods */

	@Override
	public Role save(Role role, final EntityAuditInfo entityAuditInfo) {
		role = EntityHelper.setCreateAuditFields(role, entityAuditInfo);
		return roleDAO.save(role);
	}

	/* All Update Methods */

	@Override
	public Role update(final Role role, final EntityAuditInfo entityAuditInfo) {
		Role roleToUpdate = find(role.getId());

		roleToUpdate.setName(role.getName());
		roleToUpdate.setDescription(role.getDescription());
		roleToUpdate = EntityHelper.setUpdateAuditFields(roleToUpdate, entityAuditInfo);

		return roleDAO.save(roleToUpdate);
	}

	/* All Delete Methods */

	@Override
	public void delete(final Role role) {
		if (role == null) {
			throw new NullPointerException("role cannot be null");
		}

		roleDAO.delete(role);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Role role = find(id);
		delete(role);
	}

	public void deleteByIdAndAccountId(final Integer id, final int accountId) {
		if (id == null || accountId <= 0) {
			throw new NullPointerException("Invalid id and/or accountId");
		}

		Role role = findByIdAndAccountId(id, accountId);
		delete(role);
	}

	public void addEmployeeSiteAssignment(final int siteId, final int roleId, final int employeeId,
										  final EntityAuditInfo entityAuditInfo) {
		SiteAssignment siteAssignment = siteAssignmentDAO.find(siteId, roleId, employeeId);
		if (siteAssignment == null) {
			siteAssignment = buildSiteAssignment(siteId, roleId, employeeId, entityAuditInfo);
			siteAssignmentDAO.save(siteAssignment);
		}
	}

	private SiteAssignment buildSiteAssignment(final int siteId, final int roleId, final int employeeId,
											   final EntityAuditInfo entityAuditInfo) {
		return new SiteAssignmentBuilder()
				.employee(new Employee(employeeId))
				.role(new Role(roleId))
				.siteId(siteId)
				.createdBy(entityAuditInfo.getAppUserId())
				.createdDate(entityAuditInfo.getTimestamp())
				.build();
	}

	public void deleteEmployeeSiteAssignment(final int siteId, final int roleId, final int employeeId) {
		siteAssignmentDAO.delete(employeeId, roleId, siteId);
	}

	public void deleteAllEmployeeSiteAssignmentsForSite(final int siteId, final int employeeId) {
		siteAssignmentDAO.deleteByEmployeeIdAndSiteId(employeeId, siteId);
	}
}
