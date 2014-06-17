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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MRolesManagerTest {
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
		MRolesManager mRolesManager = new MRolesManager();
		mRolesManager.attachWithModel(role);
		assertNotNull(mRolesManager.fetchModel(role.getId()));
	}

	@Test
	public void testCopyBasicInfo() throws Exception {
		MRolesManager mRolesManager = new MRolesManager();
		mRolesManager.copyBasicInfo(Arrays.asList(role));
		assertEquals(role.getId(), mRolesManager.fetchModel(role.getId()).getId());
		assertEquals(role.getName(), mRolesManager.fetchModel(role.getId()).getName());
	}

	@Test
	public void testExtractRoleAndCopyWithBasicInfo() throws Exception {
		MRolesManager mRolesManager = new MRolesManager();
		Set<MRolesManager.MRole> mRoles = mRolesManager.extractRoleAndCopyWithBasicInfo(skill.getRoles());
		assertTrue(mRoles.size() == 1);
		MRolesManager.MRole mExtractedRole=null;
		for(MRolesManager.MRole mRole :mRoles){
			mExtractedRole=mRole;
			break;
		}
		assertEquals(role.getId(), mExtractedRole.getId());
	}
}
