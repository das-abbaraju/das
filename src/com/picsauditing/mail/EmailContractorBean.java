package com.picsauditing.mail;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

public class EmailContractorBean extends EmailBean {
	protected ContractorAccount contractor;
	protected ContractorAccountDAO contractorDAO;

	public EmailContractorBean(ContractorAccountDAO contractorDAO, UserDAO userDAO, AppPropertyDAO appPropertyDAO) {
		super(userDAO, appPropertyDAO);
		this.contractorDAO = contractorDAO;
	}

	/**
	 * Send an email template built for a given account and post a note to the
	 * contractor notes. To test the email, use testMessage first.
	 * 
	 * @param emailType
	 * @param accountID
	 * @param perms
	 * @throws Exception
	 */
	public void sendMessage(EmailTemplates templateType, ContractorAccount contractor) throws Exception {
		this.templateType = templateType;
		this.contractor = contractor;

		tokens.put("contractor", contractor);
		tokens.put("user", contractor); // Sometimes we treat contractors as
		// users
		email.setToAddress(contractor.getEmail());
		email.setCcAddress(contractor.getSecondEmail());

		if (templateType.equals(EmailTemplates.certificate_expire))
			email.setBccAddress("eorozco@picsauditing.com");

		this.sendMail();
		if (!testMode) {
			String message = this.templateType.getDescription() + " email sent to: " + this.getSentTo();
			ContractorBean.addNote(contractor.getId(), permissions, message);
		}
	}

	public void sendMessage(EmailTemplates templateType, int conID) throws Exception {
		ContractorAccount contractor = contractorDAO.find(conID);
		sendMessage(templateType, contractor);
	}

	@Override
	public String getSentTo() {
		// return John Doe <john@doe.org>
		return contractor.getContact() + " &lt;" + email.getToAddress() + "&gt;";
	}
}
