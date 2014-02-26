package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SkillServiceTest {

	SkillService skillService;

	@Mock
	private AccountSkillDAO accountSkillDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillService = new SkillService();
	}

	@Test
	public void testFind() throws Exception {

	}

	@Test
	public void testSearch() throws Exception {

	}

	@Test
	public void testSave() throws Exception {

	}

	@Test
	public void testUpdate() throws Exception {

	}

	@Test
	public void testDelete() throws Exception {

	}

	@Test
	public void testDeleteById() throws Exception {

	}
}
