package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ProjectServiceFactory {
	private static ProjectService projectService = Mockito.mock(ProjectService.class);

	public static ProjectService getProjectService() {
		Mockito.reset(projectService);

		Project project = new ProjectBuilder().name("Project").location("Location").build();
		List<Project> projects = Arrays.asList(project, new ProjectBuilder().name("Project 2").location("Location 2").build());

		when(projectService.getProject(anyInt(), anyInt())).thenReturn(project);
		when(projectService.getProjectsForAccount(anyInt())).thenReturn(projects);
		when(projectService.search(anyString(), anyInt())).thenReturn(projects);

		return projectService;
	}
}
