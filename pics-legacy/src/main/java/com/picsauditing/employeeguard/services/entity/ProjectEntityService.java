package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.ProjectRoleEmployeeBuilder;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectEntityService implements EntityService<Project, Integer>, Searchable<Project> {

	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private ProjectRoleDAO projectRoleDAO;
	@Autowired
	private SiteAssignmentDAO siteAssignmentDAO;

	/* All Find Methods */

	@Override
	public Project find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return projectDAO.find(id);
	}

	public Set<Project> getProjectsForEmployeeBySiteId(final Employee employee, final int siteId) {
		Map<Employee, Set<Project>> employeeProjects = getProjectsForEmployeesBySiteId(Arrays.asList(employee), siteId);

		if (MapUtils.isEmpty(employeeProjects)) {
			return Collections.emptySet();
		}

		return employeeProjects.get(employee);
	}

	public Map<Employee, Set<Project>> getProjectsForEmployeesBySiteId(final Collection<Employee> employees, final int siteId) {
		return getProjectsForEmployeesBySiteIds(employees, Arrays.asList(siteId));
	}

	public Map<Employee, Set<Project>> getProjectsForEmployeesBySiteIds(final Collection<Employee> employees,
																		final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(employees) || CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectRoleEmployeeDAO.findByEmployeesAndSiteIds(employees, siteIds),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Project>() {
					@Override
					public Employee getKey(ProjectRoleEmployee projectRoleEmployee) {
						return projectRoleEmployee.getEmployee();
					}

					@Override
					public Project getValue(ProjectRoleEmployee projectRoleEmployee) {
						return projectRoleEmployee.getProjectRole().getProject();
					}
				});
	}

	public Map<Employee, Set<Project>> getProjectsForEmployees(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return PicsCollectionUtil.convertToMapOfSets(projectRoleEmployeeDAO.findByEmployees(employees),
				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Project>() {
					@Override
					public Employee getKey(ProjectRoleEmployee entity) {
						return entity.getEmployee();
					}

					@Override
					public Project getValue(ProjectRoleEmployee entity) {
						return entity.getProjectRole().getProject();
					}
				});
	}

	public List<Project> getProjectsForEmployee(final Employee employee) {
		return projectDAO.findByEmployee(employee);
	}

	public Project getProjectByRoleAndAccount(final String roleId, final int accountId) {
		return projectDAO.findProjectByRoleAndAccount(NumberUtils.toInt(roleId), accountId);
	}

	public Set<Integer> getContractorIdsForProject(final Project project) {
		Set<Integer> contractorIds = new HashSet<>();
		for (ProjectCompany projectCompany : project.getCompanies()) {
			contractorIds.add(projectCompany.getAccountId());
		}

		return contractorIds;
	}

	public List<Project> getAllProjectsForSite(final int siteId) {
		return projectDAO.findByAccount(siteId);
	}

	/* All search related methods */

	@Override
	public List<Project> search(final String searchTerm, final int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return projectDAO.search(searchTerm, accountId);
	}

	/* All Save Operations */

	@Override
	public Project save(Project project, final EntityAuditInfo entityAuditInfo) {
		project = EntityHelper.setCreateAuditFields(project, entityAuditInfo);
		return projectDAO.save(project);
	}

	/* All Update Operations */

	@Override
	public Project update(final Project project, final EntityAuditInfo entityAuditInfo) {
		Project projectToUpdate = find(project.getId());

		projectToUpdate.setAccountId(project.getAccountId());
		projectToUpdate.setName(project.getName());
		projectToUpdate.setLocation(project.getLocation());
		projectToUpdate.setStartDate(project.getStartDate());
		projectToUpdate.setEndDate(project.getEndDate());

		projectToUpdate = EntityHelper.setUpdateAuditFields(projectToUpdate, entityAuditInfo);

		return projectDAO.save(projectToUpdate);
	}

	/* All Delete Operations */

	@Override
	public void delete(final Project project) {
		if (project == null) {
			throw new NullPointerException("project cannot be null");
		}

		projectDAO.delete(project);
	}

	@Override
	public void deleteById(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		Project project = find(id);
		delete(project);
	}

	public Set<Project> getProjectsBySiteIds(final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptySet();
		}

		return new HashSet<>(projectDAO.findByAccounts(siteIds));
	}

	public void assignEmployeeToProjectRole(final Project project, final int roleId, final int employeeId,
											final EntityAuditInfo entityAuditInfo) {
		ProjectRole projectRole = projectRoleDAO.findByProjectAndRoleId(project.getId(), roleId);
		if (projectRole == null) {
			return; // We shouldn't be able to assign a user to a project role that doesn't exist
		}

		ProjectRoleEmployee projectRoleEmployee = projectRoleEmployeeDAO.findByProjectRoleAndEmployeeId(projectRole, employeeId);
		if (projectRoleEmployee == null) {
			projectRoleEmployee = buildProjectRoleEmployee(projectRole, employeeId, entityAuditInfo);
			projectRoleEmployeeDAO.save(projectRoleEmployee);
		}
	}

	private ProjectRoleEmployee buildProjectRoleEmployee(final ProjectRole projectRole, final int employeeId,
														 final EntityAuditInfo entityAuditInfo) {
		return new ProjectRoleEmployeeBuilder()
				.employee(new Employee(employeeId))
				.projectRole(projectRole)
				.createdBy(entityAuditInfo.getAppUserId())
				.createdDate(entityAuditInfo.getTimestamp())
				.build();
	}

	public void removeEmployeeFromProjectRole(final Project project, final int roleId, final int employeeId) {
		projectRoleEmployeeDAO.delete(project, roleId, employeeId);
	}

	public void unassignEmployeeFromAllProjectsOnSite(final int siteId, final int employeeId) {
		siteAssignmentDAO.deleteByEmployeeIdAndSiteId(siteId, employeeId);
	}
}
