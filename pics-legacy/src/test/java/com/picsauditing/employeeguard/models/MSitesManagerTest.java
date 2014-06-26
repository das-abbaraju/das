package com.picsauditing.employeeguard.models;

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

		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		MModels.fetchSkillsManager().setmOperations(mSkillsOperations);

		List<MOperations> mSiteOperations = new ArrayList<>();mSiteOperations.add(MOperations.COPY_ID);mSiteOperations.add(MOperations.COPY_NAME);mSiteOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchSitesManager().setmOperations(mSiteOperations);
		MModels.fetchSitesManager().attachReqdSkills(siteAccountId, egTestDataUtil.buildFakeSiteReqdSkillsList());
		MModels.fetchSitesManager().copySite(siteAccountId, accountModel);

		assertNotNull(MModels.fetchSitesManager().fetchModel(siteAccountId).getReqdSkills());

	}
}
