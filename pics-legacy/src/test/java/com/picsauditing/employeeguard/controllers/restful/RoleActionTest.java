package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.CorpRoleService;
import com.picsauditing.employeeguard.services.SiteRoleService;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class RoleActionTest extends PicsActionTest {

	public static final int ACCOUNT_ID = 345;
	public static final int ROLE_ID = 671;

	// Class under test
	private RoleAction roleAction;

	@Mock
	private CorpRoleService corpRoleService;
	@Mock
	private SiteRoleService siteRoleService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		roleAction = new RoleAction();

		super.setUp(roleAction);

		Whitebox.setInternalState(roleAction, "corpRoleService", corpRoleService);
		Whitebox.setInternalState(roleAction, "siteRoleService", siteRoleService);

		when(permissions.getAccountId()).thenReturn(ACCOUNT_ID);
	}

	@Test
	public void testIndex_Corporate() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);
		when(corpRoleService.findRolesForCorp(ACCOUNT_ID)).thenReturn(buildFakeRoles());

		String result = roleAction.index();

		verifyTestIndex(result);
	}

	@Test
	public void testIndex_Site() throws Exception {
		when(permissions.isOperator()).thenReturn(true);
		when(siteRoleService.findRolesForSite(ACCOUNT_ID)).thenReturn(buildFakeRoles());

		String result = roleAction.index();

		verifyTestIndex(result);
	}

	private void verifyTestIndex(String result) throws Exception {
		assertEquals(PicsActionSupport.JSON_STRING, result);
		Approvals.verify(roleAction.getJsonString());
	}

	private Set<MRolesManager.MRole> buildFakeRoles() {
		return new HashSet<>(Arrays.asList(
				new MRolesManager.MRole(
						new RoleBuilder()
								.id(ROLE_ID)
								.name("Test Role")
								.skills(Arrays.asList(new AccountSkillBuilder()
										.accountId(123)
										.name("Test Skill")
										.build()))
								.build()).copyId().copyName().copySkills()));
	}
}

