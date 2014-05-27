package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.factory.AccountServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProjectActionTest extends PicsActionTest {
	EGTestDataUtil egTestDataUtil = new EGTestDataUtil();

	private ProjectAction projectAction;
	private AccountService accountService;

	@Mock
	private ContractorProjectService contractorProjectService;
	private SearchForm searchForm = null;
	@Mock
	private AccountSkillProfileService accountSkillProfileService;
	@Mock
	private ProjectRoleService projectRoleService;
	@Mock
	private SiteSkillService siteSkillService;
	@Mock
	private FormBuilderFactory formBuilderFactory;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectAction = new ProjectAction();
		accountService = AccountServiceFactory.getAccountService();

		super.setUp(projectAction);

		Whitebox.setInternalState(projectAction, "accountService", accountService);
		Whitebox.setInternalState(projectAction, "contractorProjectService", contractorProjectService);
		Whitebox.setInternalState(projectAction, "searchForm", searchForm);

		Whitebox.setInternalState(projectAction, "accountSkillProfileService", accountSkillProfileService);
		Whitebox.setInternalState(projectAction, "projectRoleService", projectRoleService);
		Whitebox.setInternalState(projectAction, "siteSkillService", siteSkillService);
		Whitebox.setInternalState(projectAction, "formBuilderFactory", formBuilderFactory);


	}

	@Test
	public void testIndex() throws Exception {
		int contractorId = egTestDataUtil.getContractorId();
		when(permissions.getAccountId()).thenReturn(contractorId);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
		when(permissions.isOperatorCorporate()).thenReturn(false);
		when(permissions.isContractor()).thenReturn(true);

		List<ProjectCompany> projectCompanies = egTestDataUtil.getFakeProjectCompanies();
		when(contractorProjectService.getProjectsForContractor(contractorId)).thenReturn(projectCompanies);

		assertEquals(PicsRestActionSupport.LIST, projectAction.index());
	}
}
