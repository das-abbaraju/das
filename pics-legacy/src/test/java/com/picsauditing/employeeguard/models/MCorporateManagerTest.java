package com.picsauditing.employeeguard.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class MCorporateManagerTest extends MManagersTest {
	private AccountModel accountModel;
	private int siteAccountId = egTestDataUtil.SITE_ID;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		accountModel = egTestDataUtil.buildFakeCorporateAccountModel();
	}

	@Test
	public void testAttachWithModel() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MCorporateManager mManager = MModels.fetchCorporateManager();
		mManager.attachWithModel(siteAccountId, accountModel);
		assertNotNull(mManager.fetchModel(siteAccountId));

	}

	@Test
	public void testCopySite() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		MModels.fetchSkillsManager().setmOperations(mSkillsOperations);

		List<MOperations> mCorporateOperations = new ArrayList<>();mCorporateOperations.add(MOperations.COPY_ID);mCorporateOperations.add(MOperations.COPY_NAME);mCorporateOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchCorporateManager().setmOperations(mCorporateOperations);

		MModels.fetchCorporateManager().attachReqdSkills(siteAccountId, egTestDataUtil.buildFakeCorporateReqdSkillsList());
		MModels.fetchCorporateManager().copySite(siteAccountId, accountModel);

		assertNotNull(MModels.fetchCorporateManager().fetchModel(siteAccountId).getReqdSkills());


	}
}
