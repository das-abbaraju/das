package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProjectStatisticsModel;
import com.picsauditing.employeeguard.models.SiteAssignmentStatisticsModel;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:EGITest-demo2-context.xml"} )
public class SiteAssignmentsAndProjectsFactoryITest {

	private static final int KUPER_DEMO2_CONTRACTOR_ID =64374;

	@Autowired
	private AccountSkillProfileService accountSkillProfileService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ContractorProjectService contractorProjectService;
	@Autowired
	private SiteSkillService siteSkillService;


	@Test
	public void testCreateSiteAssignmentsWithoutProjects() throws Exception {
		int contractorId=KUPER_DEMO2_CONTRACTOR_ID;

		List<Integer> contractorClientSitesAttachedToProjs = contractorProjectService.findClientSitesByContractorAccount(contractorId);

		List<AccountModel> contractorClientSitesNotAttachedToProjects = accountService.findContractorClientSitesNotAttachedToProjects(contractorId, contractorClientSitesAttachedToProjs);
		Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills = siteSkillService.getCorporateSiteRequiredSkills(contractorClientSitesNotAttachedToProjects);
		List<AccountSkillProfile> employeeSkills = accountSkillProfileService.getSkillsForAccount(contractorId);

		SiteAssignmentsAndProjectsFactory saapf = ModelFactory.getSiteAssignmentsAndProjectsFactory();

		List<SiteAssignmentStatisticsModel> sasmForClientSitesNotAttchdToProjs =  saapf.createSiteAssignmentsWithoutProjects(
						siteAndCorporateRequiredSkills,
						employeeSkills);

		assertTrue(sasmForClientSitesNotAttchdToProjs.size()==3);
	}

}
