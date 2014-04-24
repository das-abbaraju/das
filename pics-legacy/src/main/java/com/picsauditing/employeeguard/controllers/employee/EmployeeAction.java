package com.picsauditing.employeeguard.controllers.employee;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.forms.employee.ProfilePhotoForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.ProjectRoleService;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.validators.profile.ProfileEditFormValidator;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.validator.Validator;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmployeeAction extends PicsRestActionSupport implements AjaxValidator {
	private static final long serialVersionUID = -490476912556033616L;

    @Autowired
    private AccountService accountService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private ProfileDocumentService profileDocumentService;
    @Autowired
    private FormBuilderFactory formBuilderFactory;
    @Autowired
    private PhotoUtil photoUtil;
    @Autowired
    private ProfileEditFormValidator profileEditFormValidator;
    @Autowired
    private ProjectRoleService projectRoleService;

    @FormBinding("employee_profile_edit")
    private EmployeeProfileEditForm personalInfo;
    @FormBinding("contractor_employee_edit_photo")
	private ProfilePhotoForm profilePhotoForm;
    private EmployeeProfileForm employeeProfileForm;

	private InputStream inputStream;
	private Profile profile;
    private List<EmployeeAssignmentModel> employeeAssignments;

    public String show() {
		permissions.getCurrentMode();
		profile = profileService.findById(id);
        loadProfileAssignments(profile);

        employeeProfileForm = formBuilderFactory.getEmployeeProfileFormBuilder().build(profile);

		return SHOW;
	}

    @SkipValidation
    public String editPersonalSection() {
        profile = profileService.findById(id);

        personalInfo = formBuilderFactory.getEmployeeProfileEditFormBuilder().build(profile);

        return "personal-form";
    }

	@SkipValidation
	public String photo() throws FileNotFoundException {
		profile = profileService.findById(id);
		File photo = photoUtil.getPhotoForProfile(profileDocumentService.getPhotoDocumentFromProfile(profile), getFtpDir());

		if (photo != null && photo.exists()) {
			inputStream = new FileInputStream(photo);
		} else {
			inputStream = new FileInputStream(photoUtil.getDefaultPhoto(getFtpDir()));
		}

		return "photo";
	}

	public String insert() {
		return null;
	}

	public String update() throws Exception {
        Profile profile = profileService.findById(id);
		if (personalInfo != null) {
			profile = profileService.update(personalInfo, id, permissions.getAppUserID());
		} else if (profilePhotoForm != null) {
			profileDocumentService.update(profilePhotoForm, getFtpDir(), profile, permissions.getAppUserID());
		}

		return setUrlForRedirect("/employee-guard/employee/profile/" + profile.getId());
	}

	public String delete() {
		return NONE;
	}

	public String badge() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());

        personalInfo = formBuilderFactory.getEmployeeProfileEditFormBuilder().build(profile);

		return "badge";
	}

	public String settings() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());

		personalInfo = formBuilderFactory.getEmployeeProfileEditFormBuilder().build(profile);

		return "settings";
	}

    private void loadProfileAssignments(Profile profile) {
        List<ProjectRole> projectRoles = projectRoleService.getRolesForProfile(profile);
        Set<Integer> accountIds = PicsCollectionUtil.getIdsFromCollection(projectRoles, new PicsCollectionUtil.Identitifable<ProjectRole, Integer>() {

			@Override
			public Integer getId(ProjectRole projectRole) {
				return projectRole.getProject().getAccountId();
			}
		});

        Map<Integer, AccountModel> accountModelMap = accountService.getIdToAccountModelMap(accountIds);

        employeeAssignments = ViewModelFactory.getEmployeeAssignmentModelFactory().create(projectRoles, accountModelMap);
    }

    /* Validation */

    @Override
    public Validator getCustomValidator() {
        return profileEditFormValidator;
    }

    @Override
    public void validate() {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

        profileEditFormValidator.validate(valueStack, validatorContext);
    }

	/* getters + setters */

	public ProfilePhotoForm getProfilePhotoForm() {
		return profilePhotoForm;
	}

	public void setProfilePhotoForm(ProfilePhotoForm profilePhotoForm) {
		this.profilePhotoForm = profilePhotoForm;
	}

	public Profile getProfile() {
		return profile;
	}

	public InputStream getInputStream() {
		return inputStream;
    }

	public EmployeeProfileEditForm getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(EmployeeProfileEditForm personalInfo) {
		this.personalInfo = personalInfo;
	}

    public EmployeeProfileForm getEmployeeProfileForm() {
        return employeeProfileForm;
    }

    public List<EmployeeAssignmentModel> getEmployeeAssignments() {
        return employeeAssignments;
    }
}
