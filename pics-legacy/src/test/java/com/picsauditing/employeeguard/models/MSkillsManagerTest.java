package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MSkillsManagerTest {
	private EGTestDataUtil egTestDataUtil;
	private AccountSkill skill;
	private Role role;
	private Map<Integer,AccountSkill> reqdSkillsMap;

	@Before
	public void setUp() throws Exception {
		egTestDataUtil = new EGTestDataUtil();

		reqdSkillsMap = egTestDataUtil.buildNewFakeSkillsMixedBagMap();
		for(AccountSkill skill1:reqdSkillsMap.values()){
			skill=skill1;
			break;
		}

		role = egTestDataUtil.buildNewFakeRole();
		skill.setRoles(Arrays.asList(new AccountSkillRole(role, skill)));

	}

	@Test
	public void testAttachWithModel() throws Exception {
		MSkillsManager mSkillsManager= new MSkillsManager();
		mSkillsManager.attachWithModel(skill);
		assertNotNull(mSkillsManager.fetchModel(skill.getId()));
	}

	@Test
	public void testCopyBasicInfo() throws Exception {
		MSkillsManager mSkillsManager = new MSkillsManager();
		mSkillsManager.copyBasicInfo(Arrays.asList(skill));
		assertEquals(skill.getId(), mSkillsManager.fetchModel(skill.getId()).getId());
		assertEquals(skill.getName(), mSkillsManager.fetchModel(skill.getId()).getName());
	}

	@Test
	public void copyBasicInfoAndAttachRoles() throws Exception {
		MSkillsManager mSkillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = mSkillsManager.copyBasicInfoAndAttachRoles(Arrays.asList(skill));

		for(MSkillsManager.MSkill mSkill:mSkills){
			assertEquals(skill.getId(), mSkill.getId());
			assertEquals(skill.getName(), mSkill.getName());
			for(MRolesManager.MRole mRole: mSkill.getRoles()){
				assertEquals(role.getId(),mRole.getId());
				assertEquals(role.getName(),mRole.getName());
			}
		}

	}

	@Test
	public void testCopyBasicInfoAttachRolesAndFlagReqdSkills() throws Exception {
		MSkillsManager skillsManager = new MSkillsManager();
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfoAttachRolesAndFlagReqdSkills(Arrays.asList(skill),reqdSkillsMap);

		for(MSkillsManager.MSkill mSkill:mSkills){
			assertEquals(skill.getId(), mSkill.getId());
			assertEquals(skill.getName(), mSkill.getName());
			for(MRolesManager.MRole mRole: mSkill.getRoles()){
				assertEquals(role.getId(),mRole.getId());
				assertEquals(role.getName(),mRole.getName());
			}
			assertTrue(mSkill.isReqdSkill());
		}
	}

}
