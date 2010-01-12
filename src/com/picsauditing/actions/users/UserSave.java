package com.picsauditing.actions.users;

import java.util.Vector;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.picsauditing.PICS.PasswordValidator;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class UserSave extends UsersManage {
	protected String password1;
	protected String password2;

	public UserSave(OperatorAccountDAO operatorDao, UserDAO userDAO) {
		super(operatorDao, userDAO);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		super.execute();

		if ("sendWelcomeEmail".equals(button) && user != null) {
			try {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setFromAddress(permissions.getEmail());
				emailBuilder.setTemplate(5); // New User Welcome
				emailBuilder.setPermissions(permissions);
				emailBuilder.setUser(user);
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setPriority(90);
				EmailSender.send(emailQueue);
			} catch (Exception e) {
				addActionError(e.getMessage());
				return SUCCESS;
			}
			addActionMessage("Welcome Email sent to " + user.getEmail());
		}

		if ("Save".equalsIgnoreCase(button)) {
			if (!isOK()) {
				userDAO.clear();
				return SUCCESS;
			}
			
			if (!Strings.isEmpty(password1) && user.compareEncryptedPasswords(password1, password2)){
				user.setEncryptedPassword(password1);
			}

			user.setAuditColumns(permissions);

			if (user.getAccount() == null) {
				user.setAccount(new Account());
				if (!permissions.hasPermission(OpPerms.AllOperators)) {
					user.getAccount().setId(permissions.getAccountId());
				} else
					user.getAccount().setId(accountId);
			}

			if (user.isGroup()) {
				// Create a unique username for this group
				String username = "GROUP";
				username += user.getAccount().getId();
				username += user.getName();

				user.setUsername(username);
			} else {
				int maxHistory = 0;
				// TODO u.getAccount().getPasswordPreferences().getMaxHistory()
				user.addPasswordToHistory(user.getPassword(), maxHistory);
				user.setPhoneIndex(Strings.stripPhoneNumber(user.getPhone()));
			}
			try {
				user = userDAO.save(user);
				addActionMessage("User saved successfully.");
			} catch(ConstraintViolationException e) {
				addActionError("That Username is already in use.  Please select another.");
				return SUCCESS;
			} catch(DataIntegrityViolationException e) {
				addActionError("That Username is already in use.  Please select another.");
				return SUCCESS;
			}
		}

		if ("Delete".equalsIgnoreCase(button)) {
			permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
			String message = "Cannot remove users who performed some actions in the system. Please inactivate them.";
			if(!userDAO.canRemoveUser("ContractorAudit", user.getId(), null)) {
				addActionMessage(message);
			} 
			else if(!userDAO.canRemoveUser("ContractorAuditOperator", user.getId(), null)) {
				addActionMessage(message);
			}
			else if(!userDAO.canRemoveUser("AuditData", user.getId(), null)) {
				addActionMessage(message);
			}
			else if(!userDAO.canRemoveUser("ContractorOperator", user.getId(), null)) {
				addActionMessage(message);
			}
			else if(!userDAO.canRemoveUser("UserAccess", user.getId(), "t.grantedBy.id = :userID")) {
				addActionMessage(message);
			}
			else {
				userDAO.remove(user);
				addActionMessage("Successfully removed "
					+ (user.isGroup() ? "group: " + user.getName() : "user: " + user.getUsername()));
				user = null;
			}
		}	
		return SUCCESS;
	}

	private boolean isOK() throws Exception {
		if (user == null) {
			addActionError("No user found");
			return false;
		}
		if (user.getName() == null || user.getName().length() == 0)
			addActionError("Please enter a Display Name.");
		else if (user.getName().length() < 3)
			addActionError("Please enter a Display Name with more than 2 characters.");

		if (user.isGroup())
			return (getActionErrors().size() == 0);

		// Users only after this point
		if (user.getUsername() == null || user.getUsername().length() < 5)
			addActionError("Please choose a Username at least 5 characters long.");
		
		if (!Strings.validUserName(user.getUsername().trim()))
			addActionError("Please enter a valid Username.");

		if (user.getEmail() == null || user.getEmail().length() == 0 || !Utilities.isValidEmail(user.getEmail()))
			addActionError("Please enter a valid Email address.");
		
		if(Strings.isEmpty(user.getPassword()) && Strings.isEmpty(password1))
			addActionError("Please enter a password");
		
		if (!Strings.isEmpty(password1)) {
			if (!password1.equals(password2) && !password1.equals(user.getPassword()))
				addActionError("Passwords don't match");

			Vector<String> errors = PasswordValidator.validateContractor(user, password1);
			for (String error : errors)
				addActionError(error);
		}

		return getActionErrors().size() == 0;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}
}
