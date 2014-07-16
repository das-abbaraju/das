package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ContractorSkillService;
import com.picsauditing.employeeguard.services.CorporateSkillService;
import com.picsauditing.employeeguard.services.SiteSkillService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillActionTest extends PicsActionTest {
	@Mock
	private AccountService accountService;

	@Mock
	CorporateSkillService corporateSkillService;

	@Mock
	SiteSkillService siteSkillService;

	@Mock
	private SkillEntityService skillEntityService;

	@Mock
	ContractorSkillService contractorSkillService;

	private SkillAction skillAction;
	private EGTestDataUtil egTestDataUtil;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		skillAction = new SkillAction();
		super.setUp(skillAction);

		Whitebox.setInternalState(skillAction, "corporateSkillService", corporateSkillService);
		Whitebox.setInternalState(skillAction, "siteSkillService", siteSkillService);
		Whitebox.setInternalState(skillAction, "contractorSkillService", contractorSkillService);
		Whitebox.setInternalState(corporateSkillService, "skillEntityService", skillEntityService);
		Whitebox.setInternalState(corporateSkillService, "accountService", accountService);

		egTestDataUtil = new EGTestDataUtil();
	}


	@Test
	public void testFindSkillsForCorporate() throws Exception {
		when(permissions.isCorporate()).thenReturn(true);

		skillAction.findSkills();
		verify(corporateSkillService).findSkills(anyInt());

	}

	@Test
	public void testFindSkillsForSite() throws Exception {
		when(permissions.isCorporate()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(true);

		skillAction.findSkills();
		verify(siteSkillService).findSkills(anyInt());

	}

	@Test
	public void testFindSkillsForContractor() throws Exception {
		when(permissions.isCorporate()).thenReturn(false);
		when(permissions.isOperator()).thenReturn(false);
		when(permissions.isContractor()).thenReturn(true);

		skillAction.findSkills();
		verify(contractorSkillService).findSkills(anyInt());

	}

}
