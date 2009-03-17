package com.picsauditing.actions.users;

import java.util.Vector;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.PasswordValidator;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ProfileEdit extends PicsActionSupport implements Preparable {
	protected User u;
	protected UserDAO dao;
	protected ContractorAccountDAO accountDao;
	protected String password1;
	protected String password2;

	public ProfileEdit(UserDAO dao, ContractorAccountDAO accountDao) {
		this.dao = dao;
		this.accountDao = accountDao;
	}

	public void prepare() throws Exception {
		loadPermissions();
		if (permissions == null)
			return;
		u = dao.find(permissions.getUserId());
		dao.clear();
	}

	public String execute() throws Exception {
		loadPermissions();
		
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;
		
		permissions.tryPermission(OpPerms.EditProfile);

		if (dao.duplicateUsername(u.getUsername(), u.getId())) {
			addActionError("Another user is already using the username: " + u.getUsername());
			return SUCCESS;
		}
		if (button != null) {
			if (!Strings.isEmpty(password1)) {
				if (!password1.equals(password2) && !password1.equals(u.getPassword()))
					addActionError("Passwords don't match");

				Vector<String> errors = PasswordValidator.validateContractor(u, password1);
				
				if (!Strings.isEmpty(u.getEmail()) && !Utilities.isValidEmail(u.getEmail())) {
					errors.add("Please enter a valid email address. This is our main way of communicating with you so it must be valid.");
				}
				
				for (String error : errors)
					addActionError(error);

				if (getActionErrors().size() > 0)
					return SUCCESS;
				int maxHistory = 0;
				// u.getAccount().getPasswordPreferences().getMaxHistory()
				u.addPasswordToHistory(password1, maxHistory);
				u.setPassword(password1);
			}
			u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
			u = dao.save(u);

			addActionMessage("Your profile was saved successfully");
		}

		return SUCCESS;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

}
