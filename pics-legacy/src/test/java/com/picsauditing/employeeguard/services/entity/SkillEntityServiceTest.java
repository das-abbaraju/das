package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SkillEntityServiceTest {

	private SkillEntityService skillEntityService;

	@Mock
	private AccountSkillDAO accountSkillDAO;

	@Before
	public void setUp() throws Exception {
		skillEntityService = new SkillEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(skillEntityService, "accountSkillDAO", accountSkillDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		skillEntityService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		when(accountSkillDAO.find(ENTITY_ID)).thenReturn(buildFakeAccountSkill());

		AccountSkill skill = skillEntityService.find(ENTITY_ID);

		assertNotNull(skill);
		assertEquals(ENTITY_ID, skill.getId());
	}

	@Test
	public void testSearch_NullOrEmpty() throws Exception {
		List<AccountSkill> skills = skillEntityService.search(null, ACCOUNT_ID);

		assertNotNull(skills);
		assertTrue(skills.isEmpty());
		verify(accountSkillDAO, never()).search(anyString(), anyInt());

		skills = skillEntityService.search(Strings.EMPTY_STRING, ACCOUNT_ID);

		assertNotNull(skills);
		assertTrue(skills.isEmpty());
		verify(accountSkillDAO, never()).search(anyString(), anyInt());
	}

	@Test
	public void testSearch() throws Exception {
		when(accountSkillDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeAccountSkill()));

		List<AccountSkill> skills = skillEntityService.search(SEARCH_TERM, ACCOUNT_ID);

		verify(accountSkillDAO).search(SEARCH_TERM, ACCOUNT_ID);
		assertNotNull(skills);
		assertFalse(skills.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();

		when(accountSkillDAO.save(fakeAccountSkill)).thenReturn(fakeAccountSkill);

		AccountSkill result = skillEntityService.save(fakeAccountSkill, CREATED);

		assertNotNull(result);
		assertEquals(fakeAccountSkill.getId(), result.getId());
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();
		fakeAccountSkill.setName("Original name");

		AccountSkill updatedAccountSkill = buildFakeAccountSkill();
		updatedAccountSkill.setName("Skill");
		updatedAccountSkill.setDescription("Description");

		when(accountSkillDAO.find(fakeAccountSkill.getId())).thenReturn(fakeAccountSkill);
		when(accountSkillDAO.save(fakeAccountSkill)).thenReturn(fakeAccountSkill);

		AccountSkill result = skillEntityService.update(updatedAccountSkill, UPDATED);

		assertNotNull(result);
		assertEquals(updatedAccountSkill.getId(), result.getId());
		assertEquals(updatedAccountSkill.getName(), result.getName());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();

		skillEntityService.delete(fakeAccountSkill);

		verify(accountSkillDAO).delete(fakeAccountSkill);
	}

	@Test
	public void testDeleteById() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();

		when(accountSkillDAO.find(fakeAccountSkill.getId())).thenReturn(fakeAccountSkill);

		skillEntityService.deleteById(fakeAccountSkill.getId());

		verify(accountSkillDAO).find(fakeAccountSkill.getId());
		verify(accountSkillDAO).delete(fakeAccountSkill);
	}

	private AccountSkill buildFakeAccountSkill() {
		return new AccountSkillBuilder()
				.id(ENTITY_ID)
				.build();
	}
}
