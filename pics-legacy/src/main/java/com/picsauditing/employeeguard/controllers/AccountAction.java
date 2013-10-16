package com.picsauditing.employeeguard.controllers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.LoginService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.validators.profile.ProfileFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedMessage;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.FailedLoginException;
import java.util.Date;

public class AccountAction extends PicsRestActionSupport {
	private static final long serialVersionUID = -3897271223264803860L;

	@Autowired
	private AppUserService appUserService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private EmailHashService emailHashService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ProfileFormValidator profileFormValidator;

	@FormBinding("employee_guard_create_account")
	private ProfileForm profileForm;

	private String hashCode;
	private Profile profile;

	@Anonymous
	public String index() throws Exception {
		if (!emailHashService.hashIsValid(hashCode)) {
			throw new PageNotFoundException();
		}

		EmailHash emailHash = emailHashService.findByHash(hashCode);

		profile = new Profile();

		if (emailHash != null) {
			profile.setFirstName(emailHash.getEmployee().getFirstName());
			profile.setLastName(emailHash.getEmployee().getLastName());
			profile.setEmail(emailHash.getEmailAddress());
		}

		return LIST;
	}

	@Anonymous
	public String create() throws Exception {
		if (!emailHashService.hashIsValid(hashCode)) {
			throw new PageNotFoundException();
		}

		return CREATE;
	}

	@Anonymous
	public String insert() throws Exception {
		JSONObject createAppUserResult = appUserService.createNewAppUser(profileForm.getEmail(), EncodedMessage.hash(profileForm.getPassword()));
		if (!"SUCCESS".equals(createAppUserResult.get("status").toString())) {
			return ERROR;
		}

		Profile profile = profileForm.buildProfile(NumberUtils.toInt(createAppUserResult.get("id").toString()));
		profileService.create(profile);

		EmailHash emailHash = emailHashService.findByHash(hashCode);
		emailHash.setExpirationDate(new Date());
		emailHashService.save(emailHash);
		Employee employee = emailHash.getEmployee();
		employee.setProfile(profile);
		employeeService.save(employee, employee.getAccountId(), User.SYSTEM);

		JSONObject loginResult = loginService.loginViaRest(profileForm.getEmail(), EncodedMessage.hash(profileForm.getPassword()));
		if (!"SUCCESS".equals(loginResult.get("status").toString())) {
			throw new FailedLoginException();
		} else {
			doSetCookie(loginResult.get("cookie").toString(), 10);
			return setUrlForRedirect("/employee-guard/employee/dashboard");
		}
	}

	@Override
	public void validate() {
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		profileFormValidator.validate(valueStack, validatorContext);
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public ProfileForm getProfileForm() {
		return profileForm;
	}

	public void setProfileForm(ProfileForm profileForm) {
		this.profileForm = profileForm;
	}

	public Profile getProfile() {
		return profile;
	}
}
