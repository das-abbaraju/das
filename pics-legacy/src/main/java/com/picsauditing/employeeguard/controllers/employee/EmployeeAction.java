package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.forms.employee.ProfilePhotoForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EmployeeAction extends PicsRestActionSupport {
	private static final long serialVersionUID = -490476912556033616L;

	@Autowired
	private ProfileService profileService;
	@Autowired
	private ProfileDocumentService profileDocumentService;
    @Autowired
    private FormBuilderFactory formBuilderFactory;

    @FormBinding("employee_profile_edit")
    private EmployeeProfileEditForm personalInfo;
    @FormBinding("contractor_employee_edit_photo")
	private ProfilePhotoForm profilePhotoForm;
    private EmployeeProfileForm employeeProfileForm;

	private InputStream inputStream;
	private Profile profile;

    public String show() {
		profile = profileService.findById(id);

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
		File photo = PhotoUtil.getPhotoForProfile(profile, getFtpDir());

		if (photo != null && photo.exists()) {
			inputStream = new FileInputStream(photo);
		} else {
			inputStream = new FileInputStream(PhotoUtil.getDefaultPhoto(getFtpDir()));
		}

		return "photo";
	}

	public String insert() {
		return null;
	}

	public String update() throws Exception {
        Profile profile = null;
		if (personalInfo != null) {
			profile = profileService.update(personalInfo, id, permissions.getAppUserID());
		} else if (profilePhotoForm != null) {
			profile = profileService.findById(id);
			profileDocumentService.update(profilePhotoForm, getFtpDir(), profile, permissions.getAppUserID());
		}

		return setUrlForRedirect("/employee-guard/employee/profile/" + profile.getId());
	}

	public String delete() {
		return NONE;
	}

	public String badge() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
		id = Integer.toString(profile.getId());

        personalInfo = formBuilderFactory.getEmployeeProfileEditFormBuilder().build(profile);

		return "badge";
	}

	public String settings() {
		Profile profile = profileService.findByAppUserId(permissions.getAppUserID());
		id = Integer.toString(profile.getId());

        personalInfo = formBuilderFactory.getEmployeeProfileEditFormBuilder().build(profile);

		return "settings";
	}

	/* getters + setters */

	public ProfilePhotoForm getProfilePhotoForm() {
		return profilePhotoForm;
	}

	public void setProfilePhotoForm(ProfilePhotoForm profilePhotoForm) {
		this.profilePhotoForm = profilePhotoForm;
	}

	public String getDisplayName() {
		if (employeeProfileForm != null) {
			return employeeProfileForm.getDisplayName();
		}

		return null;
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
}
