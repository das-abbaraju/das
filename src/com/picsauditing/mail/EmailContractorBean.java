package com.picsauditing.mail;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import java.util.HashMap;

public class EmailContractorBean extends EmailBean {
	private AccountBean aBean;
	private ContractorBean cBean;
	public AccountBean getAccountBean() {
		return aBean;
	}
	public ContractorBean getContractorBean() {
		return cBean;
	}
	
	public void setData(String accountID, Permissions permissions) throws Exception {
		if (accountID==null) throw new NullPointerException("accountID must be set");
		if (accountID.equals("")) throw new Exception("accountID must be set");
		this.permissions = permissions;
		aBean = new AccountBean();
		cBean = new ContractorBean();
		aBean.setFromDB(accountID);
		cBean.setFromDB(accountID);
		this.toAddress = aBean.email;
		this.ccAddress = cBean.secondEmail;
		merge.addTokens("permissions.display_name", permissions.getName());
		merge.addTokens("display_name", aBean.name);
		merge.addTokens("contact_name", aBean.contact);
		merge.addTokens("username", aBean.username);
		merge.addTokens("password", aBean.password);
	}
	public String getSentTo() {
		// return John Doe <john@doe.org>
		return this.aBean.name + " &lt;" + this.toAddress + "&gt;";
	}
	
	public void addNote(String message) throws Exception {
		String currentUserDisplayName = "PICS";
		if (permissions.getName() != null && permissions.getName().length() > 0)
			currentUserDisplayName = permissions.getName();

		cBean.addNote(aBean.id, currentUserDisplayName, message, DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}
	
	public void sendMessage(EmailTemplates emailType, String accountID, Permissions perms) throws Exception {
		sendMessage(emailType, accountID, perms, null);
	}
	public Email testMessage(EmailTemplates emailType, String accountID, Permissions perms) throws Exception {
		return testMessage(emailType, accountID, perms, null);
	}
	
	/**
	 * Send an email template built for a given account and post a note to the contractor notes.
	 * To test the email, use testMessage first.
	 * @param emailType
	 * @param accountID
	 * @param perms
	 * @throws Exception
	 */
	public void sendMessage(EmailTemplates emailType, String accountID, Permissions perms, HashMap<String, String> optionalTokens) throws Exception {
		this.setData(accountID, perms);
		if (optionalTokens != null && optionalTokens.size() > 0)
			for(String key : optionalTokens.keySet())
				this.merge.addTokens(key, optionalTokens.get(key));
		this.setMerge(emailType);
		
		// This next line should really be moved back to the ContractorBean
		if (emailType.equals(EmailTemplates.dasubmit)
			|| emailType.equals(EmailTemplates.desktopsubmit)
			)
			ccAddress = "";
		//String selectQuery = "SELECT email FROM users WHERE id = "+auditor_id;
		
		this.sendMail();
		this.addNote(emailType.getDescription() + " email sent to: "+ this.getSentTo());
	}

	/**
	 * Create the email for a given account and return the result without actually sending or saving any data
	 * @param emailType
	 * @param accountID
	 * @param perms
	 * @param optionalTokens
	 * @return
	 * @throws Exception
	 */
	public Email testMessage(EmailTemplates emailType, String accountID, Permissions perms, HashMap<String, String> optionalTokens) throws Exception {
		try {
			this.setData(accountID, perms);
			if (optionalTokens != null && optionalTokens.size() > 0)
				for(String key : optionalTokens.keySet())
					this.merge.addTokens(key, optionalTokens.get(key));
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
