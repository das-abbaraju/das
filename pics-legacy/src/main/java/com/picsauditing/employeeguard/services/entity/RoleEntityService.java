package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
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

	public Map<Project, Set<Role>> getRolesForProjects(final Collection<Project> projects) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyMap();
		}

		return Utilities.convertToMapOfSets(projectRoleDAO.findByProjects(projects),
				new Utilities.EntityKeyValueConvertable<ProjectRole, Project, Role>() {
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

	public Map<Employee, Set<Role>> getSiteRolesForEmployees(final Collection<Employee> employees, final int siteId) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		List<SiteAssignment> siteAssignments = siteAssignmentDAO.findByEmployeesAndSiteId(employees, siteId);

		return Utilities.convertToMapOfSets(siteAssignments,
				new Utilities.EntityKeyValueConvertable<SiteAssignment, Employee, Role>() {
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
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return Utilities.convertToMapOfSets(projectRoleEmployeeDAO.findByEmployeesAndSiteId(employees, siteId),
				new Utilities.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Role>() {

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

//	public Map<Role, Role> getSiteToCorporateRoles(final int siteId, final List<Integer> corporateIds) {
//		return roleDAO.findSiteToCorporateRoles(corporateIds, siteId);
//	}

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
}
