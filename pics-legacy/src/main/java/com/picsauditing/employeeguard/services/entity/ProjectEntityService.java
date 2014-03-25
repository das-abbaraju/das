package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectEntityService implements EntityService<Project, Integer>, Searchable<Project> {

	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;

	/* All Find Methods */

	@Override
	public Project find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null");
		}

		return projectDAO.find(id);
	}

	public Map<Employee, Set<Project>> getProjectsForEmployees(final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return Utilities.convertToMapOfSets(projectRoleEmployeeDAO.findByEmployees(employees),
				new Utilities.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Project>() {
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

	public Map<Employee, Set<Project>> getProjectsForEmployees(final Collection<Employee> employees, final int siteId) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		return Utilities.convertToMapOfSets(projectRoleEmployeeDAO.findByEmployeesAndSiteId(employees, siteId),
				new Utilities.EntityKeyValueConvertable<ProjectRoleEmployee, Employee, Project>() {
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

}
