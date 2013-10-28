package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.softdeleted.SoftDeletedEmployee;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.LoginService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.security.SessionCookie;
import com.picsauditing.security.SessionSecurity;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginAction extends PicsRestActionSupport {
	private static final long serialVersionUID = 3274071143261978073L;

	// FIXME replace with rest call in the future?
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private EmailHashService emailHashService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ProfileService profileService;

	@FormBinding("employee_guard_login")
	private LoginForm loginForm;

	private String hashCode;

	private Profile profile;
	private String companyName = "PICS";
	/* pages */

	@Anonymous
	public String index() throws Exception {
		if (!emailHashService.hashIsValid(hashCode)) {
			throw new PageNotFoundException();
		}

		EmailHash hash = emailHashService.findByHash(hashCode);
		SoftDeletedEmployee employee = hash.getEmployee();
		if (employee == null) {
			throw new PageNotFoundException();
		}

		prepareProfile(employee);

		Account account = accountDAO.find(employee.getAccountId());
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
		JSONObject loginResult = loginService.loginViaRest(loginForm.getUsername(), EncodedMessage.hash(loginForm.getPassword()));
		if (!"SUCCESS".equals(loginResult.get("status").toString())) {
			throw new PageNotFoundException();
		} else {
			String cookieContent = loginResult.get("cookie").toString();

			doSetCookie(cookieContent, 10);
			SessionCookie cookie = SessionSecurity.parseSessionCookie(cookieContent);

			Profile profile = profileService.findByAppUserId(cookie.getAppUserID());
			EmailHash emailHash = emailHashService.findByHash(hashCode);
			employeeService.linkEmployeeToProfile(emailHash.getEmployee(), profile);
			emailHashService.expire(emailHash);

			return setUrlForRedirect("/employee-guard/employee/dashboard");
		}
	}

	/* other methods */

	@Override
	public String logout() {
		return NONE;
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
