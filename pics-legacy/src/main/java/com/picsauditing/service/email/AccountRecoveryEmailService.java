package com.picsauditing.service.email;

import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.employeeguard.entities.Profile;
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
    private final AppUserService appUserService;
    private final EmailSender sender;
    private EmailBuilder overrideBuilder;
    private final URLUtils urlUtils;

    public AccountRecoveryEmailService(EmailTemplateDAO templateDAO, AppUserService appUserService, EmailSender sender) {
        this(templateDAO, appUserService, sender, null, new URLUtils());
    }

    protected AccountRecoveryEmailService(EmailTemplateDAO templateDAO, AppUserService appUserService, EmailSender sender, EmailBuilder builder, URLUtils urlUtils) {
        this.templateDAO = templateDAO;
        this.appUserService = appUserService;
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
        EmailQueue emailQueue = email(user.getEmail(), usernameReminder, templateParameters);
        emailQueue.setCriticalPriority();

        sender.send(emailQueue);
    }


    public void sendRecoveryEmail(User user, String requestHost) throws IOException, EmailBuildErrorException {
        sendRecoveryEmail(user.getAppUser(), user.getEmail(), user.getName(), requestHost);
    }

    public void sendRecoveryEmail(Profile profile, String requestHost) throws IOException, EmailBuildErrorException {
        sendRecoveryEmail(appUserService.findById(profile.getUserId()), profile.getEmail(), profile.getName(), requestHost);
    }

    public void sendRecoveryEmail(AppUser appUser, String email, String name, String requestHost) throws IOException, EmailBuildErrorException {
        Map<String, Object> templateParameters = new TreeMap<>();
        templateParameters.put("displayName", name);
        templateParameters.put("confirmLink", confirmLinkFor(appUser, requestHost));

        EmailTemplate passwordReset = templateDAO.find(EmailTemplate.PASSWORD_RESET);
        EmailQueue emailQueue = email(email, passwordReset, templateParameters);
        emailQueue.setCriticalPriority();

        sender.send(emailQueue);
    }

    private String confirmLinkFor(AppUser appUser, String requestHost) {
        final String serverName = requestHost.replace("http://", "https://");

        Map<String, Object> parameters = new TreeMap<>();
        parameters.put("username", appUser.getUsername());
        parameters.put("key", appUser.getResetHash());
        parameters.put("button", "reset");

        return serverName + urlUtils.getActionUrl("Login", parameters);
    }

    private EmailQueue email(String email, EmailTemplate template, Map<String, Object> templateParameters) throws IOException, EmailBuildErrorException {
        final EmailBuilder builder = getBuilder();

        builder.setTemplate(template);
        builder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
        builder.setToAddresses(email);
        builder.addAllTokens(templateParameters);

        return builder.build();
    }

    private EmailBuilder getBuilder() {
        return (overrideBuilder == null) ? new EmailBuilder() : overrideBuilder;
    }

}
