package com.picsauditing.mail;

import com.picsauditing.access.User;
import com.picsauditing.access.Permissions;

public class EmailUserBean extends EmailBean {
	private User user;
	public User getUser() {
		return user;
	}
	
	public void setData(String userID, Permissions permissions) throws Exception {
		if (userID==null) throw new NullPointerException("userID must be set");
		if (userID.equals("")) throw new Exception("userID must be set");
		this.permissions = permissions;
		user = new User();
		user.setFromDB(userID);
		this.toAddress = user.userDO.email;
		merge.addTokens("permissions.display_name", permissions.getName());
		merge.addTokens("display_name", user.userDO.accountName);
		merge.addTokens("contact_name", user.userDO.name);
		merge.addTokens("username", user.userDO.username);
		merge.addTokens("password", user.userDO.password);
	}
	public String getSentTo() {
		// return John Doe <john@doe.org>
		return user.userDO.name + " &lt;" + this.toAddress + "&gt;";
	}
	
	/**
	 * Send an email template built for a given account and post a note to the contractor notes.
	 * To test the email, use testMessage first.
	 * @param emailType
	 * @param accountID
	 * @param perms
	 * @throws Exception
	 */
	public void sendMessage(EmailTemplates emailType, String userID, Permissions perms) throws Exception {
		this.setData(userID, perms);
		this.setMerge(emailType);
		this.sendMail();
	}
	
	/**
	 * Create the email for a given account and return the result without actually sending or saving any data
	 * @param emailType
	 * @param accountID
	 * @param perms
	 * @return
	 * @throws Exception
	 */
	public Email testMessage(EmailTemplates emailType, String accountID, Permissions perms) throws Exception {
		try {
			this.setData(accountID, perms);
			this.setMerge(emailType);
			buildEmail();
			return this.email;
		} catch (Exception e) {
			Email badEmail = new Email();
			badEmail.setSubject("Error creating email");
			badEmail.setBody(e.getMessage());
			return this.email;
		}
	}
}
