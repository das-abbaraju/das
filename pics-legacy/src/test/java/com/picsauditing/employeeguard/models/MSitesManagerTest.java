package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.models.operations.MOperations;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class MSitesManagerTest extends MManagersTest {
	private AccountModel accountModel;
	private int siteAccountId = egTestDataUtil.SITE_ID;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		accountModel = egTestDataUtil.buildFakeSiteAccountModel();
	}

	@Test
	public void testAttachWithModel() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MSitesManager mManager = MModels.fetchSitesManager();
		mManager.attachWithModel(siteAccountId, accountModel);
		assertNotNull(mManager.fetchModel(siteAccountId));
	}

	@Test
	public void testCopySite() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		MModels.fetchSkillsManager().operations().copyId().copyName();


		MModels.fetchSitesManager().operations().copyId().copyName().attachReqdSkills();
		MModels.fetchSitesManager().attachReqdSkills(siteAccountId, egTestDataUtil.buildFakeSiteReqdSkillsList());
		MModels.fetchSitesManager().copySite(siteAccountId, accountModel);

		assertNotNull(MModels.fetchSitesManager().fetchModel(siteAccountId).getReqdSkills());

	}
}
