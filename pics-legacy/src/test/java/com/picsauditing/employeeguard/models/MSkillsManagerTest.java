package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.operations.MOperations;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.verify;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MSkillsManagerTest extends MManagersTest  {
	private AccountSkill skill;
	private Role role;
	private Map<Integer,AccountSkill> reqdSkillsMap;
	private Map<Integer, AccountSkill> contractorSkillsMap;
	private AccountSkill contractorSkill;
	private Group group;
	private static final int TOTAL_EMPLOYEES=10;

	@Before
	public void setUp() throws Exception {
		super.setUp();

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
		requestMap.put(MModels.MMODELS, MModels.newMModels());

		MSkillsManager mSkillsManager = MModels.fetchSkillsManager();
		MModels.fetchSkillsManager().operations().copyId().copyName();
		mSkillsManager.copySkills(Arrays.asList(skill));

		assertTrue(skill.getId()==mSkillsManager.fetchModel(skill.getId()).getId());
		assertEquals(skill.getName(), mSkillsManager.fetchModel(skill.getId()).getName());
	}

	@Test
	public void testCopyBasicInfoAttachRoles() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MSkillsManager skillsManager = MModels.fetchSkillsManager();
		MModels.fetchSkillsManager().operations().copyId().copyName().attachRoles();

		MRolesManager mRolesManager = MModels.fetchRolesManager();

		mRolesManager.operations().copyId().copyName();

		Set<MSkillsManager.MSkill> mSkills = skillsManager.copySkills(Arrays.asList(skill));

		for(MSkillsManager.MSkill mSkill:mSkills){
			assertTrue(skill.getId()== mSkill.getId());
			assertEquals(skill.getName(), mSkill.getName());
			for(MRolesManager.MRole mRole: mSkill.getRoles()){
				assertTrue(role.getId() == mRole.getId());
				assertEquals(role.getName(),mRole.getName());
			}
		}
	}

	@Test
	public void testCopyBasicInfoAttachGroupsEmployeeCount() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MContractorSkillsManager skillsManager = MModels.fetchContractorSkillManager();
		skillsManager.operations().copyId().copyName().attachGroups().evalEmployeeCount();

		MContractor mContractor = new MContractor();
		mContractor.setTotalEmployees(TOTAL_EMPLOYEES);

		MGroupsManager mGroupsManager = MModels.fetchContractorGroupsManager();
				mGroupsManager.operations().copyId().copyName();

		skillsManager.setmContractor(mContractor);

		Set<MContractorSkillsManager.MContractorSkill> mSkills = skillsManager.copySkills(Arrays.asList(contractorSkill));

		for(MContractorSkillsManager.MContractorSkill mSkill:mSkills){
			assertTrue(contractorSkill.getId()== mSkill.getId().intValue());
			assertEquals(contractorSkill.getName(), mSkill.getName());
			for(MGroupsManager.MGroup mGroup: mSkill.getGroups()){
				assertTrue(group.getId()==mGroup.getId().intValue());
				assertEquals(group.getName(),mGroup.getName());
			}

			assertTrue(TOTAL_EMPLOYEES== mSkill.getTotalEmployees());
		}
	}

}
