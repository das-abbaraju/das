package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.builders.GroupBuilder;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupEntityServiceTest {

	public static final String TEST_GROUP_NAME = "Test Group";

	public static final int GROUP_ID_1 = 1;
	public static final int GROUP_ID_2 = 2;
	public static final int ACCOUNT_ID = 465;
	public static final int USER_ID = 78;
	public static final int GROUP_ID_78 = 78;
	public static final Group WELDER_GROUP = new AccountGroupBuilder().id(12).accountId(123).name("Welders").build();
	public static final Group SHOP_WORKERS_GROUP = new AccountGroupBuilder().id(11).accountId(123).name("Shop Workers").build();

	GroupEntityService groupEntityService;

	@Mock
	private AccountGroupDAO accountGroupDAO;

	@Before
	public void setUp() throws Exception {
		groupEntityService = new GroupEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(groupEntityService, "accountGroupDAO", accountGroupDAO);
	}

	@Test
	public void testFind() throws Exception {
		when(accountGroupDAO.find(GROUP_ID_1)).thenReturn(buildFakeGroup(GROUP_ID_1, ACCOUNT_ID, TEST_GROUP_NAME));

		Group result = groupEntityService.find(GROUP_ID_1);

		assertEquals(GROUP_ID_1, result.getId());
		assertEquals("Test Group", result.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		groupEntityService.find(null);
	}

	@Test
	public void testGetGroupsByContractorId_NoContractorIds() {
		Map<Integer, Set<Group>> result = groupEntityService.getGroupsByContractorId(null);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testGetGroupsByContractorId() {
		setupTestGetGroupsByContractorId();

		Map<Integer, Set<Group>> result = groupEntityService.getGroupsByContractorId(Arrays.asList(new Employee()));

		verifyTestGetGroupsByContractorId(result);
	}

	private void setupTestGetGroupsByContractorId() {
		when(accountGroupDAO.findGroupsByEmployees(anyCollectionOf(Employee.class)))
				.thenReturn(Arrays.asList(WELDER_GROUP, SHOP_WORKERS_GROUP));
	}

	private void verifyTestGetGroupsByContractorId(final Map<Integer, Set<Group>> result) {
		Set<Group> groups = result.get(123);
		assertEquals(2, groups.size());
		assertTrue(groups.containsAll(Arrays.asList(WELDER_GROUP, SHOP_WORKERS_GROUP)));
	}

	@Test
	public void testSearch_NoSearchTermProvided() throws Exception {
		List<Group> result = groupEntityService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		List<Group> result = groupEntityService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		Group fakeGroup = buildFakeGroup(GROUP_ID_1, ACCOUNT_ID, TEST_GROUP_NAME);
		when(accountGroupDAO.save(fakeGroup)).thenReturn(fakeGroup);

		Group group = groupEntityService.save(fakeGroup, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		assertEquals(1, group.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		Group fakeGroup = setupTestUpdate();

		Group group = groupEntityService.update(fakeGroup, new EntityAuditInfo.Builder()
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

		groupEntityService.delete(fakeGroup);

		verify(accountGroupDAO).delete(fakeGroup);
	}

	@Test(expected = NullPointerException.class)
	public void testDelete_NullEntity() throws Exception {
		groupEntityService.delete(null);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteById_NullId() throws Exception {
		groupEntityService.deleteById(null);
	}

	@Test
	public void testDeleteById() throws Exception {
		Group fakeGroup = buildFakeGroup(GROUP_ID_78, ACCOUNT_ID, TEST_GROUP_NAME);
		when(accountGroupDAO.find(GROUP_ID_78)).thenReturn(fakeGroup);

		groupEntityService.deleteById(GROUP_ID_78);

		verify(accountGroupDAO).delete(fakeGroup);
	}

	private Group buildFakeGroup(final int id, final int accountId, final String name) {
		return new GroupBuilder(id, accountId)
				.name(name)
				.build();
	}
}
