package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class SiteRoleServiceTest {

	private SiteRoleService siteRoleService;

	@Mock
	private AccountService accountService;
	@Mock
	private RoleEntityService roleEntityService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		siteRoleService = new SiteRoleService();

		Whitebox.setInternalState(siteRoleService, "accountService", accountService);
		Whitebox.setInternalState(siteRoleService, "roleEntityService", roleEntityService);
	}

	@Test
	public void testFindRolesForSite() {
		setupTestFindRolesForSite();

		Set<MRolesManager.MRole> roles = siteRoleService.findRolesForSite(23);

		verifyTestFindRolesForSite(roles);
	}

	private void setupTestFindRolesForSite() {
		when(accountService.getTopmostCorporateAccountIds(anyInt())).thenReturn(Arrays.asList(23));

		List<Role> fakeRoles = Arrays.asList(new RoleBuilder()
				.name("Test Role")
				.skills(Arrays.asList(new AccountSkillBuilder().name("Test Skill").build()))
				.build());
		when(roleEntityService.findRolesForCorporateAccounts(anyCollectionOf(Integer.class))).thenReturn(fakeRoles);
	}

	private void verifyTestFindRolesForSite(Set<MRolesManager.MRole> roles) {
		assertEquals(1, roles.size());
		assertEquals("Test Role", roles.iterator().next().getName());
		assertEquals(1, roles.iterator().next().getSkills().size());
		assertEquals("Test Skill", roles.iterator().next().getSkills().iterator().next().getName());
	}
}
