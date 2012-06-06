package com.picsauditing.actions.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.PasswordValidator;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ChangePassword extends PicsActionSupport {
	@Autowired
	protected UserDAO dao;
	@Autowired
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired
	protected UserDAO userDAO;

	protected User u;
	protected User user;
	protected String passwordc;
	protected String password1;
	protected String password2;
	protected String source;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected String url;

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

		return SUCCESS;
	}

	public void resetPasswordLink(int user, String url) throws IOException{
		redirect("ChangePassword.action?source=profile&user=" + user + "&url="+ url);
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

				Vector<String> errors = PasswordValidator.validateContractor(user, password1);
				for (String error : errors)
					addActionError(error);

				if (getActionErrors().size() > 0) {
					return SUCCESS;
				}

				// Set password to the encrypted version
				user.setEncryptedPassword(password1);

				/*
				 * TODO: this doesn't seem to to anything at the moment.
				 * 
				 * Also, these passwords should not be saved in plain text.
				 */
				int maxHistory = 0;
				user.addPasswordToHistory(password1, maxHistory);

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
					redirect(url);
					return SUCCESS;
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
		if (source.equalsIgnoreCase("manage"))
			url = "UsersManage.action?account=" + user.getAccount() + "&user=" + user.getId() + "&isActive="
					+ user.getIsActive() + "&isGroup=" + user.getIsGroup() + "&msg=" + getText("global.Password.saved");
		else
			url = "ProfileEdit.action?msg=" + getText("global.Password.saved");
		this.redirect(url);
		return SUCCESS;
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
}
