package com.picsauditing.service.contractor;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.email.EmailBuilderService;
import com.picsauditing.util.EmailAddressUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContractorEmailService {

	@Autowired
    protected EmailTemplateDAO templateDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private EmailBuilderService emailBuilderService;
    private Logger logger = LoggerFactory.getLogger(ContractorEmailService.class);

	public EmailQueue generateContractorNotificationEmail(ContractorAccount contractor, User user, Permissions permissions) throws Exception {
        EmailBuilder emailBuilder = new EmailBuilder();

        EmailTemplate template = templateDAO.find(356);

        emailBuilder.setTemplate(template);
        emailBuilder.addToken("contractor", contractor);
        emailBuilder.setPermissions(permissions);
        emailBuilder.setFromAddress(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
        emailBuilder.setToAddresses(user.getEmail());
        EmailQueue emailQueue = emailBuilder.build();
        return emailQueue;
    }

    public EmailQueue generateOperatorNotificationEmail(ContractorAccount contractor, Permissions permissions) throws Exception {
        EmailBuilder emailBuilder = new EmailBuilder();

        EmailTemplate template = templateDAO.find(354);

        emailBuilder.setTemplate(template);
        emailBuilder.addToken("contractor", contractor);
        emailBuilder.setPermissions(permissions);
        emailBuilder.setFromAddress(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
        emailBuilder.setToAddresses(permissions.getEmail());
        EmailQueue emailQueue = emailBuilder.build();
        return emailQueue;
    }

	public void sendEmailToCsr(String subject, String message, User fromContractorUser) throws Exception {
		String csrEmail = getCsrEmailAddressForContractorUser(fromContractorUser);
		EmailQueue email = buildContactYourCSREmail(subject, message, fromContractorUser, csrEmail);
		emailSender.sendNow(email);
	}

	private EmailQueue buildContactYourCSREmail(String subject, String message, User fromContractorUser, String toCsrEmail) throws IOException, EmailBuildErrorException, MessagingException {
		EmailTemplate emailTemplate = templateDAO.find(EmailTemplate.CONTACT_YOUR_CSR_EMAIL_TEMPLATE);

        if (emailTemplate == null) {
            logger.error("Unable to find template ID: {}", EmailTemplate.CONTACT_YOUR_CSR_EMAIL_TEMPLATE);
        }

	    Map<String, Object> tokenMap = new HashMap<>();
		tokenMap.put("contractor", fromContractorUser.getAccount());
		tokenMap.put("user", fromContractorUser);

		EmailQueue email = emailBuilderService.buildEmail(emailTemplate, fromContractorUser, toCsrEmail, tokenMap);

		email.setBody(addUserMessageToBody(message, email));
		email.setSubject(addUserSubjectToSubjectLine(subject, email));

		setSubjectAndBodyViewableBy(Account.EVERYONE, email);

		return email;
	}

	private String getCsrEmailAddressForContractorUser(User sendingContractorUser) throws Exception {
		if (sendingContractorUser.getAccount().isContractor()) {
			ContractorAccount contractorAccount = (ContractorAccount) sendingContractorUser.getAccount();
			User currentCsr = contractorAccount.getCurrentCsr();
			String csrEmail = currentCsr.getEmail();
			return csrEmail;
		} else {
			throw new IllegalStateException("Sending user is not a contractor: " + sendingContractorUser.toString());
		}
	}

	private String addUserSubjectToSubjectLine(String subject, EmailQueue email) {
		return email.getSubject().concat(": " + subject);
	}

	private String addUserMessageToBody(String message, EmailQueue email) {
		return email.getBody().concat(message);
	}

	private void setSubjectAndBodyViewableBy(int viewableBy, EmailQueue email) {
		email.setSubjectViewableById(viewableBy);
		email.setBodyViewableById(viewableBy);
	}

}
