package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.fail;

public class SkillServiceTest {

	SkillService skillService;

	@Mock
	private AccountSkillDAO accountSkillDAO;

	@Before
	public void setUp() throws Exception {
		skillService = new SkillService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(skillService, "accountSkillDAO", accountSkillDAO);
	}

	@Test
	public void testFind() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testSearch() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testSave() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testUpdate() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testDelete() throws Exception {
		fail("Not implemented");
	}

	@Test
	public void testDeleteById() throws Exception {
		fail("Not implemented");
	}
}
