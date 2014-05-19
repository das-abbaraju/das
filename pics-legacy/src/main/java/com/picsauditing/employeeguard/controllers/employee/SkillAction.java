package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.employee.CompanySkillInfo;
import com.picsauditing.employeeguard.forms.employee.CompanySkillsForm;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import com.picsauditing.forms.binding.FormBinding;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SkillAction extends PicsRestActionSupport {

	private static final long serialVersionUID = -76323003242644511L;

	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private ProfileDocumentService profileDocumentService;
	@Autowired
	private SkillEntityService skillEntityService;
	@Autowired
	private FormBuilderFactory formBuilderFactory;

	private AccountSkill skill;
	private List<ProfileDocument> documents;
	private List<CompanySkillInfo> companySkillInfoList;

	@FormBinding({"employee_skill_file", "employee_skill_training"})
	private SkillDocumentForm skillDocumentForm;

	public String index() {
		companySkillInfoList = buildCompanySkillInfoList();

		return LIST;
	}

	public String show() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillEntityService.find(getIdAsInt());
		AccountSkillProfile accountSkillProfile = accountSkillEmployeeService
				.getAccountSkillEmployeeForProfileAndSkill(profile, accountSkill);

		ProfileDocument profileDocument = null;
		if (accountSkillProfile != null) {
			profileDocument = accountSkillProfile.getProfileDocument();
		}

		SkillInfo skillInfo = null;
		if (accountSkillProfile != null) {
			skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkillProfile);
		} else {
			skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkill, SkillStatus.Expired);
		}

		skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(skillInfo, profileDocument);

		return SHOW;
	}

	public String edit() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillEntityService.find(getIdAsInt());
		AccountSkillProfile accountSkillProfile = accountSkillEmployeeService
				.getAccountSkillEmployeeForProfileAndSkill(profile, accountSkill);

		ProfileDocument profileDocument = accountSkillProfile.getProfileDocument();

		SkillInfo skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkillProfile);
		skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(skillInfo, profileDocument);

		return "edit-form";
	}

	public String file() {
		documents = profileDocumentService.getDocumentsForProfile(profileEntityService.findByAppUserId(permissions.getAppUserID()).getId());

		return "file";
	}

	public String training() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillEntityService.find(getIdAsInt());
		AccountSkillProfile accountSkillProfile = accountSkillEmployeeService
				.getAccountSkillEmployeeForProfileAndSkill(profile, accountSkill);

		if (accountSkillProfile == null) {
			SkillInfo skillInfo = formBuilderFactory.getSkillInfoBuilder().build(accountSkill, SkillStatus.Expired);
			skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(skillInfo, null);
		} else {
			skillDocumentForm = formBuilderFactory.getSkillDocumentFormBuilder().build(accountSkillProfile);
		}

		return "training";
	}

	private List<CompanySkillInfo> buildCompanySkillInfoList() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());


		CompanySkillsForm companySkillsForm = formBuilderFactory.getCompanySkillsFormBuilder().build(profile);
		return companySkillsForm.getCompanySkillInfoList();
	}

	public String manage() {
		skill = skillEntityService.find(getIdAsInt());
		if (skill.getSkillType().isCertification()) {
			Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
			documents = profileDocumentService.getDocumentsForProfile(profile.getId());
		}

		return "manage";
	}

	public String update() throws Exception {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		AccountSkill accountSkill = skillEntityService.find(getIdAsInt());

		accountSkillEmployeeService.update(accountSkill, profile, skillDocumentForm);

		return setUrlForRedirect("/employee-guard/employee/skill/" + accountSkill.getId());
	}

	private Employee getEmployee(final Profile profile) {
		return profile.getEmployees().get(profile.getEmployees().size() - 1);
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
}
