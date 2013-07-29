package com.picsauditing.mail;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.subscription.DynamicReportsSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Map;

public class PreviewEmail extends PicsActionSupport {
    @Autowired
    EmailSubscriptionDAO subscriptionDAO;
    @Autowired
    SubscriptionBuilderFactory subscriptionBuilderFactory;
    private ContractorAccount contractor;
	private User user;
	private Locale locale;
	private int templateID;
    private int subscriptionID;
	private EmailQueue email;

    @Anonymous
	public String execute() throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();

        if (subscriptionID != 0) {
            EmailSubscription emailSubscription = subscriptionDAO.find(subscriptionID);

            SubscriptionBuilder builder = subscriptionBuilderFactory.getBuilder(emailSubscription.getSubscription());
            Map<String, Object> tokens = builder.process(emailSubscription);

            emailBuilder.addAllTokens(tokens);
            templateID = emailSubscription.getSubscription().getTemplateID();
        }
        else if (templateID != 0) {
            emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
        }
		emailBuilder.setTemplate(templateID);

		email = emailBuilder.build();

		return SUCCESS;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public int getTemplateID() {
		return templateID;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}

    public int getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public EmailQueue getEmail() {
		return email;
	}

	public void setEmail(EmailQueue email) {
		this.email = email;
	}
}
