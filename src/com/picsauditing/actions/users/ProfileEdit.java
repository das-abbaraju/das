package com.picsauditing.actions.users;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;

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

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		if (!dao.checkUserName(u.getUsername(), u.getId())) {
			addActionError("Another user is already using the username: " + u.getUsername());
			return SUCCESS;
		}
		if (button != null) {
			if (password1 != null && password1.length() > 0) {
				if (password1.length() < 5)
					addActionError("Password must be at least 5 characters long");
				if (!password1.equals(password2))
					addActionError("Passwords don't match");
				if (getActionErrors().size() > 0)
					return SUCCESS;
				u.setPassword(password1);
			}
			u = dao.save(u);

			addActionMessage("Your profile was saved successfully");
		}

		return SUCCESS;
	}

	public void prepare() throws Exception {
		loadPermissions();
		if (permissions == null)
			return;
		u = dao.find(permissions.getUserId());
		dao.clear();
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
