package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MGroupsManagerTest {
	private EGTestDataUtil egTestDataUtil;
	private AccountSkill skill;
	private Group group;
	private Map<Integer,AccountSkill> reqdSkillsMap;


	@Before
	public void setUp() throws Exception {
		egTestDataUtil = new EGTestDataUtil();

		reqdSkillsMap = egTestDataUtil.buildNewFakeContractorSkillsMixedBagMap();
		for(AccountSkill skill1:reqdSkillsMap.values()){
			skill=skill1;
			break;
		}

		group = egTestDataUtil.buildNewFakeGroup();
		skill.setGroups(Arrays.asList(new AccountSkillGroup(group, skill)));

	}

	@Test
	public void testAttachWithModel() throws Exception {
		MGroupsManager mGroupsManager = new MGroupsManager();
		mGroupsManager.attachWithModel(group);
		assertNotNull(mGroupsManager.fetchModel(group.getId()));
	}

	@Test
	public void testCopyBasicInfo() throws Exception {
		MGroupsManager mGroupsManager = new MGroupsManager();
		mGroupsManager.copyBasicInfo(Arrays.asList(group));
		assertTrue(group.getId() == mGroupsManager.fetchModel(group.getId()).getId());
		assertEquals(group.getName(), mGroupsManager.fetchModel(group.getId()).getName());

	}

	@Test
	public void testExtractGroupAndCopyWithBasicInfo() throws Exception {
		MGroupsManager mGroupsManager = new MGroupsManager();
		Set<MGroupsManager.MGroup> mGroups = mGroupsManager.extractGroupAndCopyWithBasicInfo(skill.getGroups());
		assertTrue(mGroups.size() == 1);
		MGroupsManager.MGroup mExtractedGroup=null;
		for(MGroupsManager.MGroup mGroup :mGroups){
			mExtractedGroup=mGroup;
			break;
		}
		assertTrue(group.getId()==mExtractedGroup.getId());
	}
}
