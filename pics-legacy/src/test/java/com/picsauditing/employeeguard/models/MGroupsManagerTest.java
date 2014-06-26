package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MGroupsManagerTest extends MManagersTest {

	private AccountSkill skill;
	private Group group;
	private Map<Integer, AccountSkill> reqdSkillsMap;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		reqdSkillsMap = egTestDataUtil.buildNewFakeContractorSkillsMixedBagMap();
		for (AccountSkill skill1 : reqdSkillsMap.values()) {
			skill = skill1;
			break;
		}

		group = egTestDataUtil.buildNewFakeGroup();
		skill.setGroups(Arrays.asList(new AccountSkillGroup(group, skill)));

	}

	@Test
	public void testAttachWithModel() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
		mGroupsManager.attachWithModel(group);
		assertNotNull(mGroupsManager.fetchModel(group.getId()));
	}

	@Test
	public void testCopyBasicInfo() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
		List<MOperations> mGroupsOperations = new ArrayList<>();mGroupsOperations.add(MOperations.COPY_ID);mGroupsOperations.add(MOperations.COPY_NAME);
		mGroupsManager.setmOperations(mGroupsOperations);

		mGroupsManager.copyGroups(Arrays.asList(group));

		assertTrue(group.getId() == mGroupsManager.fetchModel(group.getId()).getId());
		assertEquals(group.getName(), mGroupsManager.fetchModel(group.getId()).getName());

	}

	@Test
	public void testExtractGroupAndCopyWithBasicInfo() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
		List<MOperations> mGroupsOperations = new ArrayList<>();mGroupsOperations.add(MOperations.COPY_ID);mGroupsOperations.add(MOperations.COPY_NAME);
		mGroupsManager.setmOperations(mGroupsOperations);

		Set<MGroupsManager.MGroup> mGroups = mGroupsManager.copyGroups(skill.getGroups());
		assertTrue(mGroups.size() == 1);
		MGroupsManager.MGroup mExtractedGroup = null;
		for (MGroupsManager.MGroup mGroup : mGroups) {
			mExtractedGroup = mGroup;
			break;
		}
		assertTrue(group.getId() == mExtractedGroup.getId());
	}
}
