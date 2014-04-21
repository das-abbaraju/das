package com.picsauditing.service.email;

import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.URLUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AccountRecoveryEmailService {

    private final EmailTemplateDAO templateDAO;
    private final EmailSender sender;
    private EmailBuilder overrideBuilder;
    private final URLUtils urlUtils;

    public AccountRecoveryEmailService(EmailTemplateDAO templateDAO, EmailSender sender) {
        this(templateDAO, sender, null, new URLUtils());
    }

    protected AccountRecoveryEmailService(EmailTemplateDAO templateDAO, EmailSender sender, EmailBuilder builder, URLUtils urlUtils) {
        this.templateDAO = templateDAO;
        this.sender = sender;
        this.overrideBuilder = builder;
        this.urlUtils = urlUtils;
    }

    public void sendUsernameRecoveryEmail(List<User> matchingUsers) throws IOException, EmailBuildErrorException {
        final User user = matchingUsers.get(0);

        Map<String, Object> templateParameters = new TreeMap<>();
        templateParameters.put("user", user);
        templateParameters.put("users", matchingUsers);
        templateParameters.put("username", user.getName());

        EmailTemplate usernameReminder = templateDAO.find(EmailTemplate.USERNAME_REMINDER);
        EmailQueue emailQueue = email(user, usernameReminder, templateParameters);
        emailQueue.setCriticalPriority();

        sender.send(emailQueue);
    }

    public void sendRecoveryEmail(User user, String requestHost) throws IOException, EmailBuildErrorException {

        Map<String, Object> templateParameters = new TreeMap<>();
        templateParameters.put("user", user);
        templateParameters.put("confirmLink", confirmLinkFor(user, requestHost));

        EmailTemplate passwordReset = templateDAO.find(EmailTemplate.PASSWORD_RESET);
        EmailQueue emailQueue = email(user, passwordReset, templateParameters);
        emailQueue.setCriticalPriority();

        sender.send(emailQueue);
    }

    private String confirmLinkFor(User user, String requestHost) {
        final String serverName = requestHost.replace("http://", "https://");

        Map<String, Object> parameters = new TreeMap<>();
        parameters.put("username", user.getUsername());
        parameters.put("key", user.getResetHash());
        parameters.put("button", "reset");

        return serverName + urlUtils.getActionUrl("Login", parameters);
    }

    private EmailQueue email(User user, EmailTemplate template, Map<String, Object> templateParameters) throws IOException, EmailBuildErrorException {
        final EmailBuilder builder = getBuilder();

        builder.setTemplate(template);
        builder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
        builder.setToAddresses(user.getEmail());
        builder.addAllTokens(templateParameters);

        return builder.build();
    }

    private EmailBuilder getBuilder() {
        return (overrideBuilder == null) ? new EmailBuilder() : overrideBuilder;
    }

}
