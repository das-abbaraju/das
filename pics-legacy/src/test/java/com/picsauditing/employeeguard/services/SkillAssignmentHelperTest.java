package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import com.picsauditing.employeeguard.entities.SiteSkill;
import com.picsauditing.employeeguard.entities.builders.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class SkillAssignmentHelperTest {

    public static final int SITE_ID = 423;
    private SkillAssignmentHelper skillAssignmentHelper;

    @Mock
    private AccountService accountService;
    @Mock
    private RoleEmployeeDAO roleEmployeeDAO;
    @Mock
    private SiteSkillDAO siteSkillDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        skillAssignmentHelper = new SkillAssignmentHelper();

        Whitebox.setInternalState(skillAssignmentHelper, "accountService", accountService);
        Whitebox.setInternalState(skillAssignmentHelper, "roleEmployeeDAO", roleEmployeeDAO);
        Whitebox.setInternalState(skillAssignmentHelper, "siteSkillDAO", siteSkillDAO);
    }

    @Test
    public void testGetRequiredSkillsFromProjectsAndSiteRoles_NoProjectCompanies() throws Exception {
        Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(null, null, null);
        assertTrue(result.isEmpty());
    }

    private List<ProjectCompany> getFakeProjectCompanies() {
        return Arrays.asList(
                new ProjectCompanyBuilder()
                        .project(
                                new ProjectBuilder()
                                        .skills(Arrays.asList(
                                                new ProjectSkillBuilder()
                                                        .skill(
                                                                new AccountSkillBuilder()
                                                                        .name("Project Skill")
                                                                        .build())
                                                        .build()))
                                        .roles(Arrays.asList(
                                                new ProjectRoleBuilder()
                                                        .role(
                                                                new RoleBuilder()
                                                                        .skills(Arrays.asList(
                                                                                new AccountSkillBuilder()
                                                                                        .name("Project Role Skill")
                                                                                        .build()))
                                                                        .build())
                                                        .build()
                                        ))
                                        .build())
                        .accountId(SITE_ID)
                        .build(),
                new ProjectCompanyBuilder().build(),
                new ProjectCompanyBuilder().build(),
                new ProjectCompanyBuilder().build());
    }

    @Test
    public void testGetRequiredSkillsFromProjectsAndSiteRoles_() throws Exception {
        when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(Arrays.asList(32, 45));

        // TODO: Provide correct data here (site skill builder with correct skill
        when(siteSkillDAO.findByAccountIds(anyListOf(Integer.class))).thenReturn(Arrays.asList(new SiteSkill()));

        // TODO: Provide correct data here (site skill builder with correct skill
        when(roleEmployeeDAO.findByEmployeeAndSiteIds(anyInt(), anyListOf(Integer.class))).thenReturn(Arrays.asList(new RoleEmployee()));

        Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(getFakeProjectCompanies(), null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testFilterNoLongerNeededEmployeeSkills() throws Exception {
        fail("Not implemented yet.");
    }
}
