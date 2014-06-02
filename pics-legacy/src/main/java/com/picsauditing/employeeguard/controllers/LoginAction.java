package com.picsauditing.employeeguard.controllers;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.actions.validation.AjaxValidator;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.validators.login.LoginFormValidator;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.security.SessionCookie;
import com.picsauditing.security.SessionSecurity;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY;

public class LoginAction extends PicsRestActionSupport implements AjaxValidator {

	private static final long serialVersionUID = 3274071143261978073L;

	private static final Logger LOG = LoggerFactory.getLogger(LoginAction.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private EmailHashService emailHashService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private LoginFormValidator loginFormValidator;

	@FormBinding("employee_guard_login")
	private LoginForm loginForm;

	private String hashCode;

	private Profile profile;
	private String companyName = "PICS";
	/* pages */

	@Anonymous
	public String index() throws Exception {
		if (!emailHashService.hashIsValid(hashCode)) {
			LOG.warn("Invalid hashCode = " + hashCode);
			throw new PageNotFoundException();
		}

		EmailHash emailHash = emailHashService.findByHash(hashCode);
		SoftDeletedEmployee employee = emailHash.getEmployee();
		if (employee == null) {
			LOG.warn("Could not find employee for hashCode = " + hashCode);
			throw new PageNotFoundException();
		}

		prepareProfile(employee);

		AccountModel account = accountService.getAccountById(employee.getAccountId());
		if (account != null) {
			companyName = account.getName();
		}

		return LIST;
	}

	private void prepareProfile(SoftDeletedEmployee employee) {
		if (employee.getProfile() != null) {
			profile = employee.getProfile();
		} else {
			profile = new Profile();
			profile.setFirstName(employee.getFirstName());
			profile.setLastName(employee.getLastName());
		}
	}

	@Anonymous
	public String login() throws Exception {
		try {
			String cookieContent = authenticationService.authenticateEmployeeGUARDUser(loginForm.getUsername(),
					loginForm.getPassword(), true);

			doSetCookie(cookieContent, 10);
			SessionCookie cookie = SessionSecurity.parseSessionCookie(cookieContent);

			Profile profile = profileEntityService.findByAppUserId(cookie.getAppUserID());
			EmailHash emailHash = emailHashService.findByHash(loginForm.getHashCode());
			employeeEntityService.linkEmployeeToProfile(emailHash.getEmployee(), profile);
			emailHashService.expire(emailHash);

			return setUrlForRedirect(EMPLOYEE_SUMMARY);

		} catch (Exception e) {
			LOG.error("Login failed ", e);
			throw new PageNotFoundException();
		}
	}

	/* other methods */

	@Override
	public String logout() {
		return NONE;
	}

    /* Validation */

	@Override
	public Validator getCustomValidator() {
		return loginFormValidator;
	}

	@Override
	public void validate() {
		ValueStack valueStack = ActionContext.getContext().getValueStack();
		DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(this);

		loginFormValidator.validate(valueStack, validatorContext);
	}

	/* getters + setters */

	public LoginForm getLoginForm() {
		return loginForm;
	}

	public void setLoginForm(LoginForm loginForm) {
		this.loginForm = loginForm;
	}

	public String getHashCode() {
		return hashCode;
	}

	public void setHashCode(String hashCode) {
		this.hashCode = hashCode;
	}

	public Profile getProfile() {
		return profile;
	}

	public String getCompanyName() {
		return companyName;
	}
}
