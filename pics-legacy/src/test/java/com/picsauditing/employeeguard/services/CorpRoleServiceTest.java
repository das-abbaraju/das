package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MRolesManager;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
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
import static org.mockito.Mockito.when;

public class CorpRoleServiceTest {

	public static final int CORP_ID = 124;
	public static final int ROLE_ID = 67;
	public static final String ROLE_NAME = "Test Role";
	// Class under test
	private CorpRoleService corpRoleService;

	@Mock
	private RoleEntityService roleEntityService;

	@Mock
	private SessionInfoProvider sessionInfoProvider;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		corpRoleService = new CorpRoleService();

		Whitebox.setInternalState(corpRoleService, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);

	}

	@Test
	public void testFindRolesForCorp() throws ReqdInfoMissingException {
		setupTestFindRolesForCorp();

		Set<MRolesManager.MRole> results = corpRoleService.findRolesForCorp(CORP_ID);

		verifyTestFindRolesForCorp(results);
	}

	private void setupTestFindRolesForCorp() {
		List<Role> fakeRoles = Arrays.asList(new RoleBuilder().id(ROLE_ID).name(ROLE_NAME).build());
		when(roleEntityService.findRolesForCorporateAccounts(anyCollectionOf(Integer.class))).thenReturn(fakeRoles);
	}

	private void verifyTestFindRolesForCorp(final Set<MRolesManager.MRole> results) {
		assertEquals(1, results.size());
		assertEquals(ROLE_NAME, results.iterator().next().getName());
	}
}
