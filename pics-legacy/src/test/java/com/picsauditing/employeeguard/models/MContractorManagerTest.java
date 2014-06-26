package com.picsauditing.employeeguard.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class MContractorManagerTest extends MManagersTest {
	private AccountModel accountModel;
	private int contractorAccountId = egTestDataUtil.CONTRACTOR_ID;


	@Before
	public void setUp() throws Exception {
		super.setUp();
		accountModel = egTestDataUtil.buildFakeContractorAccountModel();
	}

	@Test
	public void testAttachWithModel() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MContractorManager mManager = MModels.fetchContractorManager();
		mManager.attachWithModel(contractorAccountId, accountModel);
		assertNotNull(mManager.fetchModel(contractorAccountId));

	}

	@Test
	public void testCopyContractor() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		List<MOperations> mSkillsOperations = new ArrayList<>();mSkillsOperations.add(MOperations.COPY_ID);mSkillsOperations.add(MOperations.COPY_NAME);
		MModels.fetchContractorSkillManager().setmOperations(mSkillsOperations);


		List<MOperations> mContractorOperations = new ArrayList<>();mContractorOperations.add(MOperations.COPY_ID);mContractorOperations.add(MOperations.COPY_NAME);mContractorOperations.add(MOperations.ATTACH_REQD_SKILLS);
		MModels.fetchContractorManager().setmOperations(mContractorOperations);

		MModels.fetchContractorManager().attachReqdSkills(contractorAccountId, egTestDataUtil.buildNewFakeContractorSkillsMixedBag());
		MModels.fetchContractorManager().copyContractor(contractorAccountId, accountModel);

		assertNotNull(MModels.fetchContractorManager().fetchModel(contractorAccountId).getReqdSkills());

	}
}
