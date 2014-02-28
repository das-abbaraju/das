package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.List;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

	private ProjectService projectService;

	@Mock
	private ProjectDAO projectDAO;

	@Before
	public void setUp() throws Exception {
		projectService = new ProjectService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(projectService, "projectDAO", projectDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		projectService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		Project expected = buildFakeProject();

		when(projectDAO.find(expected.getId())).thenReturn(expected);

		Project result = projectService.find(expected.getId());

		assertNotNull(result);
		assertEquals(expected.getId(), result.getId());
	}

	@Test
	public void testSearch_NullEmpty() throws Exception {
		List<Project> nullSearch = projectService.search(null, ACCOUNT_ID);
		assertNotNull(nullSearch);
		assertTrue(nullSearch.isEmpty());

		List<Project> emptySearch = projectService.search(" ", ACCOUNT_ID);
		assertNotNull(emptySearch);
		assertTrue(emptySearch.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		when(projectDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeProject()));

		List<Project> result = projectService.search(SEARCH_TERM, ACCOUNT_ID);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(ENTITY_ID, result.get(0).getId());
	}

	@Test
	public void testSave() throws Exception {
		Project fakeProject = buildFakeProject();

		when(projectDAO.save(fakeProject)).thenReturn(fakeProject);

		Project result = projectService.save(fakeProject, CREATED);

		verify(projectDAO).save(fakeProject);
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
		assertNull(result.getUpdatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		Project fakeProject = buildFakeProject();
		fakeProject.setName("Fake Project");
		fakeProject.setLocation("Fake Location");

		Project updatedProject = buildFakeProject();

		when(projectDAO.find(updatedProject.getId())).thenReturn(updatedProject);
		when(projectDAO.save(updatedProject)).thenReturn(updatedProject);

		Project result = projectService.update(fakeProject, UPDATED);

		verify(projectDAO).find(updatedProject.getId());
		verify(projectDAO).save(updatedProject);
		assertEquals(updatedProject.getId(), result.getId());
		assertEquals(fakeProject.getName(), result.getName());
		assertEquals(fakeProject.getLocation(), result.getLocation());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		Project fakeProject = buildFakeProject();

		projectService.delete(fakeProject);

		verify(projectDAO).delete(fakeProject);
	}

	@Test
	public void testDeleteById() throws Exception {
		Project fakeProject = buildFakeProject();

		when(projectDAO.find(fakeProject.getId())).thenReturn(fakeProject);

		projectService.deleteById(fakeProject.getId());

		verify(projectDAO).delete(fakeProject);
	}

	private Project buildFakeProject() {
		return new ProjectBuilder()
				.id(ENTITY_ID)
				.build();
	}

}
