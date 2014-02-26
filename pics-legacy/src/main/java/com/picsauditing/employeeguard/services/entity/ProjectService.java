package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProjectService implements EntityService<Project, Integer>, Searchable<Project> {

	@Autowired
	private ProjectDAO projectDAO;

	/* All Find Methods */

	@Override
	public Project find(final Integer id) {
		if (id == null) {
			throw new NullPointerException("id cannot be null.");
		}

		return projectDAO.find(id);
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
	public Project save(final Project project, final int createdBy, final Date createdDate) {
		project.setCreatedBy(createdBy);
		project.setCreatedDate(createdDate);
		return projectDAO.save(project);
	}

	/* All Update Operations */

	@Override
	public Project update(final Project project, final int updatedBy, final Date updatedDate) {
		Project projectToUpdate = find(project.getId());

		projectToUpdate.setAccountId(project.getAccountId());
		projectToUpdate.setName(project.getName());
		projectToUpdate.setLocation(project.getLocation());
		projectToUpdate.setStartDate(project.getStartDate());
		projectToUpdate.setEndDate(project.getEndDate());

		return projectDAO.save(projectToUpdate);
	}

	/* All Delete Operations */

	@Override
	public void delete(final Project project) {
		projectDAO.delete(project);
	}

	@Override
	public void deleteById(final Integer id) {
		Project project = find(id);
		delete(project);
	}
}
