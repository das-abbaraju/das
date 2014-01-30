package com.picsauditing.actions.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.PasswordHistory;

import com.picsauditing.jpa.entities.PasswordSecurityLevel;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.PasswordDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.PasswordValidator;

@SuppressWarnings("serial")
public class ChangePassword extends PicsActionSupport {
	@Autowired
	protected UserDAO dao;
	@Autowired
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected PasswordDAO passwordDAO;
	@Autowired
	protected PasswordValidator passwordValidator;

	protected User u;
	protected User user;
	protected String passwordc;
	protected String password1;
	protected String password2;
	protected String source;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected String url;
	private PasswordSecurityLevel passwordSecurityLevel;

	@Anonymous
	public String execute() throws Exception {
		super.execute();
		loadPermissions();
		if (u == null)
			u = dao.find(permissions.getUserId());

		if (u.getId() == user.getId())
			user = u;
		else
			user = userDAO.find(user.getId());

		passwordSecurityLevel = user.getAccount().getPasswordSecurityLevel();
		return SUCCESS;
	}

	public void resetPasswordLink(int user, String url) throws IOException {
		ServletActionContext.getResponse().sendRedirect(
				"ChangePassword.action?source=profile&user=" + user + "&url=" + url);
	}

	@Anonymous
	public String changePassword() throws Exception {
		/*
		 * Some browsers (i.e. Chrome) store the user's password in the first
		 * password field. We will assume that if the confirm password has a
		 * value that the user is attempting to change their password.
		 */
		boolean changePassword = false;
		// if the user is admin and user id and user's password need to be
		// changed doesnt match
		loadPermissions();

		if ((user.getId() != u.getId()) || permissions.isForcePasswordReset()) {
			changePassword = true;
		} else {
			if (u.isEncryptedPasswordEqual(passwordc)) {
				changePassword = true;
			} else {
				addActionError(getText("ProfileEdit.error.PasswordsDoNotMatch"));
				return SUCCESS;
			}
		}

		if (changePassword) {
			if (!Strings.isEmpty(password2)) {
				boolean forcedReset = u.isForcePasswordReset();

				if (!password1.equals(password2)) {
					addActionError(getText("ProfileEdit.error.PasswordsDoNotMatch"));
				}

                addActionErrorsForPasswordValidation();

				if (getActionErrors().size() > 0) {
					return SUCCESS;
				}

				// Set password to the encrypted version
				String oldPassword = user.getPassword();
				user.setEncryptedPassword(password1);
                user.setPasswordChanged(new Date());
                savePasswordHistory(oldPassword);

				// If the user is changing their password, they are no longer
				// forced to reset.
				user.setForcePasswordReset(false);
				permissions.setForcePasswordReset(false);

				/*
				 * If the user came to profile edit as a result of a
				 * forcedPasswordReset, they will have the `url` field set.
				 */
				if (!Strings.isEmpty(url) && forcedReset) {
					user = dao.save(user);
					return setUrlForRedirect(url);
				}
			} else {
				if (Strings.isEmpty(password1))
					addActionError(getText("global.Password.needBothPassword"));
				else
					addActionError(getText("global.Password.needConfirmedPassword"));
				return SUCCESS;
			}
		}

		user = dao.save(user);
		if (source.equalsIgnoreCase("manage")) {
			url = "UsersManage.action?account=" + user.getAccount() + "&user=" + user.getId() + "&isActive="
					+ user.getIsActive() + "&isGroup=" + user.getIsGroup();
			addActionMessage(getText("global.Password.saved"));
		} else {
			url = "ProfileEdit.action";

			addActionMessage(getText("global.Password.saved"));
		}

		this.setUrlForRedirect(url);
		return SUCCESS;
	}

    private void addActionErrorsForPasswordValidation() {
        Vector<String> errors = passwordValidator.validatePassword(user, password1);
        for (String error : errors) {
            String[] params = error.split("::");
            if (params.length > 1) {
                addActionError(getText(params[0], new Object[] { params[1] }));
            } else {
                addActionError(getText(error));
            }
        }
    }

    private void savePasswordHistory(String oldPassword) {
	    if (!getAccount().getPasswordSecurityLevel().enforceHistory()) {
		    return;
	    }

	    PasswordHistory passwordHistory = new PasswordHistory(user, oldPassword, new Date());
	    passwordHistory.setAuditColumns(user);
	    passwordDAO.save(passwordHistory);
    }

    public void setU(User u) {
		this.u = u;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public User getU() {
		return u;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setPasswordc(String passwordc) {
		this.passwordc = passwordc;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public PasswordSecurityLevel getPasswordSecurityLevel() {
		return passwordSecurityLevel;
	}

	public void setPasswordSecurityLevel(PasswordSecurityLevel passwordSecurityLevel) {
		this.passwordSecurityLevel = passwordSecurityLevel;
	}
}
