package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.util.Strings;
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
	private RoleEmployeeDAO roleEmployeeDAO;

	/* All Find Methods */

	@Override
	public Role find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return roleDAO.find(id);
	}

	public Map<Project, Set<Role>> getRolesForProjects(final Collection<Project> projects) {
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
		return Utilities.convertToMapOfSets(roleEmployeeDAO.findByEmployeesAndSiteId(employees, siteId),
				new Utilities.EntityKeyValueConvertable<RoleEmployee, Employee, Role>() {
					@Override
					public Employee getKey(RoleEmployee roleEmployee) {
						return roleEmployee.getEmployee();
					}

					@Override
					public Role getValue(RoleEmployee roleEmployee) {
						return roleEmployee.getRole();
					}
				});
	}

	public Map<Employee, Set<Role>> getProjectRolesForEmployees(final Collection<Employee> employees,
																final int siteId) {

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
}
