package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.builders.AccountGroupBuilder;
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

public class GroupServiceTest {

	public static final String TEST_GROUP_NAME = "Test Group";

	public static final int GROUP_ID_1 = 1;
	public static final int GROUP_ID_2 = 2;
	public static final int ACCOUNT_ID = 465;
	public static final int USER_ID = 78;
	public static final int GROUP_ID_78 = 78;

	GroupService groupService;

	@Mock
	private AccountGroupDAO accountGroupDAO;

	@Before
	public void setUp() throws Exception {
		groupService = new GroupService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(groupService, "accountGroupDAO", accountGroupDAO);
	}

	@Test
	public void testFind() throws Exception {
		when(accountGroupDAO.find(GROUP_ID_1)).thenReturn(buildFakeGroup(GROUP_ID_1, ACCOUNT_ID, TEST_GROUP_NAME));

		Group result = groupService.find(GROUP_ID_1);

		assertEquals(GROUP_ID_1, result.getId());
		assertEquals("Test Group", result.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		groupService.find(null);
	}

	@Test
	public void testSearch_NoSearchTermProvided() throws Exception {
		List<Group> result = groupService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		List<Group> result = groupService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		Group fakeGroup = buildFakeGroup(GROUP_ID_1, ACCOUNT_ID, TEST_GROUP_NAME);
		when(accountGroupDAO.save(fakeGroup)).thenReturn(fakeGroup);

		Group group = groupService.save(fakeGroup, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		assertEquals(1, group.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		Group fakeGroup = setupTestUpdate();

		Group group = groupService.update(fakeGroup, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		verifyTestUpdate(group);
	}

	private Group setupTestUpdate() {
		Group fakeGroup = buildFakeGroup(GROUP_ID_2, ACCOUNT_ID, "Updated Group");
		Group groupToUpdate = buildFakeGroup(GROUP_ID_2, ACCOUNT_ID, "Original Group");

		when(accountGroupDAO.find(GROUP_ID_2)).thenReturn(groupToUpdate);
		when(accountGroupDAO.save(groupToUpdate)).thenReturn(new Group());

		return fakeGroup;
	}

	private void verifyTestUpdate(Group group) {
		assertNotNull(group);

		ArgumentCaptor<Group> argumentCaptor = ArgumentCaptor.forClass(Group.class);
		verify(accountGroupDAO).save(argumentCaptor.capture());
		assertEquals("Updated Group", argumentCaptor.getValue().getName());
	}

	@Test
	public void testDelete() throws Exception {
		Group fakeGroup = buildFakeGroup(GROUP_ID_78, ACCOUNT_ID, TEST_GROUP_NAME);

		groupService.delete(fakeGroup);

		verify(accountGroupDAO).delete(fakeGroup);
	}

	@Test(expected = NullPointerException.class)
	public void testDelete_NullEntity() throws Exception {
		groupService.delete(null);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteById_NullId() throws Exception {
		groupService.deleteById(null);
	}

	@Test
	public void testDeleteById() throws Exception {
		Group fakeGroup = buildFakeGroup(GROUP_ID_78, ACCOUNT_ID, TEST_GROUP_NAME);
		when(accountGroupDAO.find(GROUP_ID_78)).thenReturn(fakeGroup);

		groupService.deleteById(GROUP_ID_78);

		verify(accountGroupDAO).delete(fakeGroup);
	}

	private Group buildFakeGroup(final int id, final int accountId, final String name) {
		return new AccountGroupBuilder(id, accountId)
				.name(name)
				.build();
	}
}
