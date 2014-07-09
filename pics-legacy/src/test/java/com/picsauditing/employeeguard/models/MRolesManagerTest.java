package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.operations.MOperations;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MRolesManagerTest extends MManagersTest {
	private AccountSkill skill;
	private Role role;
	private Map<Integer,AccountSkill> reqdSkillsMap;

	@Before
	public void setUp() throws Exception {
		super.setUp();

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
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MRolesManager mRolesManager = MModels.fetchRolesManager();
		mRolesManager.attachWithModel(role);
		assertNotNull(mRolesManager.fetchModel(role.getId()));
	}

	@Test
	public void testCopyBasicInfo() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MRolesManager mRolesManager = MModels.fetchRolesManager();

		mRolesManager.operations().copyId().copyName();

		mRolesManager.copyRoles(Arrays.asList(role));
		assertTrue(role.getId() == mRolesManager.fetchModel(role.getId()).getId());
		assertEquals(role.getName(), mRolesManager.fetchModel(role.getId()).getName());
	}

	@Test
	public void testExtractRoleAndCopyWithBasicInfo() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MRolesManager mRolesManager = MModels.fetchRolesManager();
		mRolesManager.operations().copyId().copyName();

		Set<MRolesManager.MRole> mRoles = mRolesManager.copySkillRoles(skill.getRoles());
		assertTrue(mRoles.size() == 1);
		MRolesManager.MRole mExtractedRole=null;
		for(MRolesManager.MRole mRole :mRoles){
			mExtractedRole=mRole;
			break;
		}
		assertTrue(role.getId() == mExtractedRole.getId());
	}
}
