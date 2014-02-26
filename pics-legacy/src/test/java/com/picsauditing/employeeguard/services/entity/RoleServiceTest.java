package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleServiceTest {

	public static final String TEST_ROLE_NAME = "Test Role";

	public static final int ROLE_ID_1 = 1;
	public static final int ROLE_ID_2 = 2;
	public static final int ACCOUNT_ID = 465;
	public static final int USER_ID = 78;
	public static final int ROLE_ID_78 = 78;

	RoleService roleService;

	@Mock
	private RoleDAO roleDAO;

	@Before
	public void setUp() throws Exception {
		roleService = new RoleService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(roleService, "roleDAO", roleDAO);
	}

	@Test
	public void testFind() throws Exception {
		when(roleDAO.find(ROLE_ID_1)).thenReturn(buildFakeRole(ROLE_ID_1, ACCOUNT_ID, TEST_ROLE_NAME));

		Role result = roleService.find(ROLE_ID_1);

		assertEquals(ROLE_ID_1, result.getId());
		assertEquals("Test Role", result.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		roleService.find(null);
	}

	@Test
	public void testSearch_NoSearchTermProvided() throws Exception {
		List<Role> result = roleService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		List<Role> result = roleService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		Role fakeRole = buildFakeRole(ROLE_ID_1, ACCOUNT_ID, TEST_ROLE_NAME);
		when(roleDAO.save(fakeRole)).thenReturn(fakeRole);

		Role group = roleService.save(fakeRole, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		assertEquals(1, group.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		Role fakeRole = setupTestUpdate();

		Role group = roleService.update(fakeRole, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		verifyTestUpdate(group);
	}

	private Role setupTestUpdate() {
		Role fakeRole = buildFakeRole(ROLE_ID_2, ACCOUNT_ID, "Updated Role");
		Role groupToUpdate = buildFakeRole(ROLE_ID_2, ACCOUNT_ID, "Original Role");

		when(roleDAO.find(ROLE_ID_2)).thenReturn(groupToUpdate);
		when(roleDAO.save(groupToUpdate)).thenReturn(new Role());

		return fakeRole;
	}

	private void verifyTestUpdate(Role group) {
		assertNotNull(group);

		ArgumentCaptor<Role> argumentCaptor = ArgumentCaptor.forClass(Role.class);
		verify(roleDAO).save(argumentCaptor.capture());
		assertEquals("Updated Role", argumentCaptor.getValue().getName());
	}

	@Test
	public void testDelete() throws Exception {
		Role fakeRole = buildFakeRole(ROLE_ID_78, ACCOUNT_ID, TEST_ROLE_NAME);

		roleService.delete(fakeRole);

		verify(roleDAO).delete(fakeRole);
	}

	@Test(expected = NullPointerException.class)
	public void testDelete_NullEntity() throws Exception {
		roleService.delete(null);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteById_NullId() throws Exception {
		roleService.deleteById(null);
	}

	@Test
	public void testDeleteById() throws Exception {
		Role fakeRole = buildFakeRole(ROLE_ID_78, ACCOUNT_ID, TEST_ROLE_NAME);
		when(roleDAO.find(ROLE_ID_78)).thenReturn(fakeRole);

		roleService.deleteById(ROLE_ID_78);

		verify(roleDAO).delete(fakeRole);
	}

	private Role buildFakeRole(final int id, final int accountId, final String name) {
		return new RoleBuilder()
				.id(id)
				.accountId(accountId)
				.name(name)
				.build();
	}
}
