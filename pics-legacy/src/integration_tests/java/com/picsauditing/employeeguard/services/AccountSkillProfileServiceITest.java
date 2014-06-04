package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.util.SpringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:EGITest-localhost-context.xml"} )
@WebAppConfiguration
@ActiveProfiles("jpa")
//@Transactional
public class AccountSkillProfileServiceITest {

	private static final int LENNY_LEONARD_PROFILE_ID =1;
	private static final int LENNY_LEONARD_EMPLOYEE_ID =31;
	private static final int ANOTHER_EMPLOYEE_ID =41;
	private static final int LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID =54578;
	private static final int LENNY_LEONARD_PROFILE_DOC_ID =9;
	private static final int SKILL_FirePreventionTraining_ID =12;
	private static final int SKILL_GeneralSafetyTraining_ID =13;
	private static final int PROJECT_ID =1;
	private static final int ROLE_ID =1;
	private static final int CORPORATE_ID =55653;
	private static final int SITE_ID =55654;
	private static final Set CONTRACTORS =new HashSet(){{add(LENNY_LEONARD_EMPLOYEE_CONTRACTOR_ACCOUNT_ID);}};
	private static final Set CORPORATES =new HashSet(){{add(CORPORATE_ID);}};

	@Autowired
	private AccountSkillProfileDAO accountSkillProfileDAO;
	@Autowired private ProfileDAO profileDAO;
	@Autowired private ProfileDocumentDAO profileDocumentDAO;
	@Autowired private EmployeeDAO employeeDAO;
	@Autowired private AccountSkillDAO accountSkillDAO;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private RoleDAO roleDAO;

	@Autowired
	private AccountSkillProfileService accountSkillProfileService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private FormBuilderFactory formBuilderFactory;

	@PersistenceContext
	private EntityManager em;

	public void deleteAccountSkillProfile(int id){
		AccountSkillProfile accountSkillProfile=em.find(AccountSkillProfile.class, id);
		em.merge(accountSkillProfile);
		em.remove(accountSkillProfile);
		em.flush();

	}

	@Test
	public void testUpdate_BrandNewSkillUpdate_ExpectCompletedStatus() throws Exception {
		Profile profile = profileDAO.find(LENNY_LEONARD_PROFILE_ID);
		AccountSkill accountSkill = accountSkillDAO.find(SKILL_FirePreventionTraining_ID);
		AccountSkillProfile accountSkillProfile = accountSkillProfileDAO.findByProfileAndSkill(profile,accountSkill);

		if(accountSkillProfile!=null) {
			accountSkillProfile=accountSkillProfileDAO.find(accountSkillProfile.getId());
			accountSkillProfileDAO.delete(accountSkillProfile);
		}

		SkillInfo skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkill, SkillStatus.Completed);
		SkillDocumentForm skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(skillInfo, null);
		skillDocumentForm.setVerified(true);

		accountSkillProfileService.update(accountSkill, profile, skillDocumentForm);

		accountSkillProfile = accountSkillProfileDAO.findByProfileAndSkill(profile,accountSkill);

		assertNotNull(accountSkillProfile);

		SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);
		assertEquals(skillStatus.toString(),SkillStatus.Completed.toString());
	}

}
