package com.picsauditing.employeeguard.controllers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.ProfileForm;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.validators.profile.ProfileFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY;

public class AccountAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = -3897271223264803860L;

	private static final Logger LOG = LoggerFactory.getLogger(AccountAction.class);

	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private EmailHashService emailHashService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private ProfileEntityService profileEntityService;
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

		loadProfile(emailHash);

		return LIST;
	}

	private void loadProfile(final EmailHash emailHash) {
		profile = new Profile();

		if (emailHash.getEmployee() != null) {
			profile.setFirstName(emailHash.getEmployee().getFirstName());
			profile.setLastName(emailHash.getEmployee().getLastName());
			profile.setEmail(emailHash.getEmployee().getEmail());
		}
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
		try {
			AppUser appUser = authenticationService.createNewAppUser(profileForm.getEmail(), profileForm.getPassword());

			Profile profile = profileForm.buildProfile(appUser.getId());
			profile = profileEntityService.save(profile, new EntityAuditInfo.Builder().appUserId(User.SYSTEM)
					.timestamp(DateBean.today()).build());

			EmailHash emailHash = emailHashService.findByHash(hashCode);
			employeeEntityService.linkEmployeeToProfile(emailHash.getEmployee(), profile);
			emailHashService.expire(emailHash);

			String sessionCookieContent = authenticationService.authenticateEmployeeGUARDUser(profileForm.getEmail(),
					profileForm.getPassword(), hashCode, true);
			doSetCookie(sessionCookieContent, 10);

			return setUrlForRedirect(EMPLOYEE_SUMMARY);
		} catch (Exception e) {
			LOG.error("Error creating appUser and logging in ", e);
			return ERROR;
		}
	}

	@Override
	public Validator getCustomValidator() {
		return profileFormValidator;
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
