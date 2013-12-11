package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.i18n.RequiredLanguagesSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.BaseTableRequiringLanguages;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmailTemplateSave extends PicsActionSupport {
	@Autowired
	private EmailTemplateDAO emailTemplateDAO;
	private EmailTemplate template;
	private List<EmailTemplate> emailTemplates = null;
	private boolean allowsVelocity;
	private boolean allowsHtml;
	private boolean allowsTranslations;

	public String execute() throws NoRightsException {
		// TODO Need to replace this. I think changes made from the front end
		// get saved when the object is pulled from memory.
		if (template.getId() > 0 && !permissions.hasPermission(OpPerms.AllOperators)
				&& template.getAccountID() != permissions.getAccountId()) {
			addActionError(getText("EmailTemplateSave.MissingPermission"));
			emailTemplateDAO.clear(); // don't save
			return BLANK;
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.EmailTemplates, type = OpType.Edit)
	public String save() {
		if (Strings.isEmpty(template.getTemplateName())) {
			addActionError(getText("EmailTemplateSave.EnterTemplateName"));
        }

		boolean subjectMissing = false;
		boolean bodyMissing = false;

		if (template.isTranslated()) {
			if (template.getTranslatedSubject() == null)
				subjectMissing = true;
			if (template.getTranslatedBody() == null)
				bodyMissing = true;
		} else {
			if (Strings.isEmpty(template.getSubject()))
				subjectMissing = true;
			if (Strings.isEmpty(template.getBody()))
				bodyMissing = true;
		}

		List<String> defaultLocales = Arrays.asList(RequiredLanguagesSupport.DEFAULT_LOCALES);
		List<String> validLanguages = new ArrayList<>();

		for (String language : template.getLanguages()) {
			if (defaultLocales.contains(language)) {
				validLanguages.add(language);
			}
		}

		if (validLanguages.size() == 0) {
			if (permissions.isOperatorCorporate()) {
				// Default to whatever locale the operator/corporate user is
				// using
				validLanguages.add(permissions.getLocale().getLanguage());
			} else {
				addActionError(getText("EmailTemplateSave.SelectRequiredLanguage"));
			}
		}

		if (subjectMissing)
			addActionError(getText("EmailTemplateSave.EnterSubject"));
		if (bodyMissing)
			addActionError(getText("EmailTemplateSave.EnterBody"));

        if (!usingNewTranslationFeature()) {
            if (template.hasMissingChildRequiredLanguages())
                addActionError("Changes to required languages must always have at least one language left. "
                        + "Make sure your email template has at least one language.");
        }

		if (hasActionErrors()) { // change
			emailTemplateDAO.clear(); // don't save
			return BLANK;
		}

		template.setLanguages(validLanguages);

		if (template.getId() == 0) {
			// This is a new one so do some updates
			template.setCreationDate(new Date());
			template.setCreatedBy(getUser());
			template.setAccountID(permissions.getAccountId());
		}
		template.setUpdateDate(new Date());
		template.setUpdatedBy(getUser());
		try {
			if (!permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
				template.setAllowsVelocity(allowsVelocity);
				template.setHtml(allowsHtml);
				template.setTranslated(allowsTranslations);
			}
			template = emailTemplateDAO.save(template);
			addActionMessage(getText("EmailTemplateSave.SuccessfullySaved"));
			WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
			wizardSession.setTemplateID(template.getId());
		} catch (EntityExistsException e) {
			addActionError(getText("EmailTemplateSave.MustHaveUniqueName"));
		} catch (Exception e) {
			addActionError(getText("EmailTemplateSave.FailedSaveTemplate", new Object[] { e.getMessage() }));
		}

		return BLANK;
	}

	@RequiredPermission(value = OpPerms.EmailTemplates, type = OpType.Delete)
	public String delete() {
		emailTemplateDAO.remove(template);
		addActionMessage(getText("EmailTemplateSave.SuccessfullyDeleted"));
		return BLANK;
	}

	public EmailTemplate getTemplate() {
		return template;
	}

	public void setTemplate(EmailTemplate template) {
		this.template = template;
	}

	public List<EmailTemplate> getEmailTemplates() {
		if (emailTemplates == null)
			emailTemplates = emailTemplateDAO.findByAccountID(permissions.getAccountId(), template.getListType());
		return emailTemplates;
	}

	public boolean isAllowsVelocity() {
		return allowsVelocity;
	}

	public void setAllowsVelocity(boolean allowsVelocity) {
		this.allowsVelocity = allowsVelocity;
	}

	public boolean isAllowsHtml() {
		return allowsHtml;
	}

	public void setAllowsHtml(boolean allowsHtml) {
		this.allowsHtml = allowsHtml;
	}

	public boolean isAllowsTranslations() {
		return allowsTranslations;
	}

	public void setAllowsTranslations(boolean allowsTranslations) {
		this.allowsTranslations = allowsTranslations;
	}
}
