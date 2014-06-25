package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MRolesManagerTest {
	private EGTestDataUtil egTestDataUtil;
	private AccountSkill skill;
	private Role role;
	private Map<Integer,AccountSkill> reqdSkillsMap;
	@Mock
	private SessionInfoProvider sessionInfoProvider;

	Map<String, Object> requestMap= new HashMap<>();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		egTestDataUtil = new EGTestDataUtil();

		reqdSkillsMap = egTestDataUtil.buildNewFakeSkillsMixedBagMap();
		for(AccountSkill skill1:reqdSkillsMap.values()){
			skill=skill1;
			break;
		}

		role = egTestDataUtil.buildNewFakeRole();
		skill.setRoles(Arrays.asList(new AccountSkillRole(role, skill)));

		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getRequest()).thenReturn(requestMap);

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
		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);
		mRolesManager.setmOperations(mRolesOperations);

		mRolesManager.copyRoles(Arrays.asList(role));
		assertTrue(role.getId() == mRolesManager.fetchModel(role.getId()).getId());
		assertEquals(role.getName(), mRolesManager.fetchModel(role.getId()).getName());
	}

	@Test
	public void testExtractRoleAndCopyWithBasicInfo() throws Exception {
		requestMap.put(MModels.MMODELS, MModels.newMModels());
		MRolesManager mRolesManager = MModels.fetchRolesManager();
		List<MOperations> mRolesOperations = new ArrayList<>();mRolesOperations.add(MOperations.COPY_ID);mRolesOperations.add(MOperations.COPY_NAME);
		mRolesManager.setmOperations(mRolesOperations);

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
