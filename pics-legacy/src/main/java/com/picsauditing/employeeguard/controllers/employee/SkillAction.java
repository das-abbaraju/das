package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.employeeguard.forms.employee.CompanySkillInfo;
import com.picsauditing.employeeguard.forms.employee.CompanySkillsForm;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.forms.employee.SkillInfo;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.calculator.ExpirationCalculator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SkillAction extends PicsRestActionSupport {

	private static final long serialVersionUID = -76323003242644511L;

	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private SkillService skillService;

	@Autowired
	private FormBuilderFactory formBuilderFactory;

	private AccountSkill skill;
	private List<ProfileDocument> documents;
	private List<CompanySkillInfo> companySkillInfoList;

	@FormBinding("employee_skill_certification")
	private SkillDocumentForm skillDocumentForm;

	public String index() {
		companySkillInfoList = buildCompanySkillInfoList();

		return LIST;
	}

	public String show() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillService.getSkill(id);
		AccountSkillEmployee accountSkillEmployee = accountSkillEmployeeService.getAccountSkillEmployeeForProfileAndSkill(profile, accountSkill);
		ProfileDocument profileDocument = accountSkillEmployee.getProfileDocument();

		SkillInfo skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkillEmployee);
		skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(skillInfo, profileDocument);

		return SHOW;
	}

	public String edit() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillService.getSkill(id);
		AccountSkillEmployee accountSkillEmployee = accountSkillEmployeeService.getAccountSkillEmployeeForProfileAndSkill(profile, accountSkill);
		ProfileDocument profileDocument = accountSkillEmployee.getProfileDocument();

		SkillInfo skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkillEmployee);
		skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(skillInfo, profileDocument);

		return "edit-form";
	}

	public String certification() {
		documents = profileDocumentService.getDocumentsForProfile(profileService.findByAppUserId(permissions.getAppUserID()).getId());

		return "certification";
	}

	public String training() {
		return "training";
	}

	private List<CompanySkillInfo> buildCompanySkillInfoList() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());

		CompanySkillsForm companySkillsForm = formBuilderFactory.getCompanySkillsFormBuilder().build(profile);

		return companySkillsForm.getCompanySkillInfoList();
	}

	public String manage() {
		skill = skillService.getSkill(id);
		if (skill.getSkillType().isCertification()) {
			Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
			documents = profileDocumentService.getDocumentsForProfile(profile.getId());
		}

		return "manage";
	}

	public String update() throws Exception {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillService.getSkill(id);
		AccountSkillEmployee accountSkillEmployee = accountSkillEmployeeService.getAccountSkillEmployeeForProfileAndSkill(profile, accountSkill);

		if (accountSkill.getSkillType().isCertification()) {
			ProfileDocument document = profileDocumentService.getDocument(Integer.toString(skillDocumentForm.getDocumentId()));
			accountSkillEmployee = accountSkillEmployeeService.linkProfileDocumentToEmployeeSkill(accountSkillEmployee, document);
		} else {
			accountSkillEmployee.setEndDate(ExpirationCalculator.calculateExpirationDate(accountSkillEmployee));
			accountSkillEmployeeService.save(accountSkillEmployee);
		}

		return setUrlForRedirect("/employee-guard/employee/skill/" + accountSkillEmployee.getSkill().getId());
	}

	public AccountSkill getSkill() {
		return skill;
	}

	public List<ProfileDocument> getDocuments() {
		return documents;
	}

	public List<CompanySkillInfo> getCompanySkillInfoList() {
		return companySkillInfoList;
	}

	public SkillDocumentForm getSkillDocumentForm() {
		return skillDocumentForm;
	}

	public void setSkillDocumentForm(SkillDocumentForm skillDocumentForm) {
		this.skillDocumentForm = skillDocumentForm;
	}

	public String getDisplayName() {
		AccountSkill accountSkill = skillService.getSkill(id);
		if (accountSkill != null) {
			return accountSkill.getName();
		}

		return null;
	}

	public boolean isHasSkills() {
		for (CompanySkillInfo companySkillInfo : companySkillInfoList) {

		}

		return false;
	}
}
