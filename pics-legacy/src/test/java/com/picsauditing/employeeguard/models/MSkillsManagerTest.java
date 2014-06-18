package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.*;
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
	private Map<Integer, AccountSkill> contractorSkillsMap;
	private AccountSkill contractorSkill;
	private Group group;
	private static final int TOTAL_EMPLOYEES=10;

	@Before
	public void setUp() throws Exception {

		prepareCorporateSkillAndRoleData();
		prepareContractorSkillAndRoleData();

	}

	private void prepareCorporateSkillAndRoleData(){
		egTestDataUtil = new EGTestDataUtil();

		reqdSkillsMap = egTestDataUtil.buildNewFakeSkillsMixedBagMap();
		for(AccountSkill skill1:reqdSkillsMap.values()){
			skill=skill1;
			break;
		}
		role = egTestDataUtil.buildNewFakeRole();
		skill.setRoles(Arrays.asList(new AccountSkillRole(role, skill)));

	}

	private void prepareContractorSkillAndRoleData(){
		egTestDataUtil = new EGTestDataUtil();


		contractorSkillsMap = egTestDataUtil.buildNewFakeContractorSkillsMixedBagMap();
		for(AccountSkill contractorSkill1:contractorSkillsMap.values()){
			contractorSkill=contractorSkill1;
			break;
		}

		contractorSkill.setRuleType(RuleType.REQUIRED);
		group = egTestDataUtil.buildNewFakeGroup();
		contractorSkill.setGroups(Arrays.asList(new AccountSkillGroup(group, contractorSkill)));

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

	@Test
	public void testCopyBasicInfoAttachGroupsReqdSkillsEmployeeCount() throws Exception {
		MSkillsManager skillsManager = new MSkillsManager();
		MContractor mContractor = new MContractor();
		mContractor.setTotalEmployees(TOTAL_EMPLOYEES);
		Set<MSkillsManager.MSkill> mSkills = skillsManager.copyBasicInfoAttachGroupsReqdSkillsEmployeeCount(Arrays.asList(contractorSkill),mContractor);

		for(MSkillsManager.MSkill mSkill:mSkills){
			assertEquals(skill.getId(), mSkill.getId());
			assertEquals(skill.getName(), mSkill.getName());
			for(MGroupsManager.MGroup mGroup: mSkill.getGroups()){
				assertEquals(group.getId(),mGroup.getId());
				assertEquals(group.getName(),mGroup.getName());
			}
			assertTrue(mSkill.isReqdSkill());
			assertEquals(TOTAL_EMPLOYEES, mSkill.getTotalEmployees());
		}
	}

}
