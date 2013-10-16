package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PageNotFoundException;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.employeeguard.entities.EmailHash;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.services.EmailHashService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.LoginService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedMessage;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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
		Employee employee = hash.getEmployee();
		if (employee == null) {
			throw new PageNotFoundException();
		}

		if (employee.getProfile() != null) {
			profile = employee.getProfile();
		} else {
			profile = new Profile();
			profile.setFirstName(employee.getFirstName());
			profile.setLastName(employee.getLastName());
		}

		Account account = accountDAO.find(employee.getAccountId());
		if (account != null) {
			companyName = account.getName();
		}

		return LIST;
	}

	@Anonymous
	public String login() throws Exception {
		JSONObject loginResult = loginService.loginViaRest(loginForm.getUsername(), EncodedMessage.hash(loginForm.getPassword()));
		if (!"SUCCESS".equals(loginResult.get("status").toString())) {
			throw new Exception();
		} else {
			doSetCookie(loginResult.get("cookie").toString(), 10);

			EmailHash emailHash = emailHashService.findByHash(hashCode);
			emailHash.setExpirationDate(new Date());
			emailHashService.save(emailHash);
			Employee employee  = emailHash.getEmployee();
			employee.setProfile(profileService.findByAppUserId(appUserDAO.findListByUserName(loginForm.getUsername()).get(0).getId()));
			employeeService.save(employee, employee.getAccountId(), User.SYSTEM);

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
