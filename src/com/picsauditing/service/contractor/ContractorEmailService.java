package com.picsauditing.service.contractor;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractorEmailService {
    @Autowired
    protected EmailTemplateDAO templateDAO;

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

}
