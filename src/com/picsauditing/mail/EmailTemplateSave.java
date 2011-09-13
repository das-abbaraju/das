package com.picsauditing.mail;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmailTemplateSave extends PicsActionSupport implements Preparable {
	@Autowired
	private EmailTemplateDAO emailTemplateDAO;
	private int id;
	private EmailTemplate template;
	private EmailTemplate template2;
	private List<EmailTemplate> emailTemplates = null;
	private boolean allowsVelocity;
	private boolean allowsHtml;
	private boolean allowsTranslations;

	@Override
	public void prepare() throws Exception {
		id = getParameter("id");
		if (id > 0) {
			template = emailTemplateDAO.find(id);
			allowsVelocity = template.isAllowsVelocity();
			allowsHtml = template.isHtml();
			allowsTranslations = template.isTranslated();
		} else
			template = new EmailTemplate();
	}

	public String execute() throws NoRightsException {
		getPermissions();

		if (button == null) {
			emailTemplateDAO.clear(); // don't save
			return SUCCESS;
		}

		if (template.getId() > 0 && !permissions.hasPermission(OpPerms.AllOperators)
				&& template.getAccountID() != permissions.getAccountId()) {
			addActionError(getText("EmailTemplateSave.MissingPermission"));
			emailTemplateDAO.clear(); // don't save
			return BLANK;
		}

		if ("delete".equals(button)) {
			permissions.tryPermission(OpPerms.EmailTemplates, OpType.Delete);
			emailTemplateDAO.remove(template.getId());
			addActionMessage(getText("EmailTemplateSave.SuccessfullyDeleted"));
			return BLANK;
		}
		if ("save".equals(button)) {
			permissions.tryPermission(OpPerms.EmailTemplates, OpType.Edit);
			if (Strings.isEmpty(template.getTemplateName()))
				addActionError(getText("EmailTemplateSave.EnterTemplateName"));

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

			if (subjectMissing)
				addActionError(getText("EmailTemplateSave.EnterSubject"));
			if (bodyMissing)
				addActionError(getText("EmailTemplateSave.EnterBody"));

			if (hasActionErrors()) { // change
				emailTemplateDAO.clear(); // don't save
				return BLANK;
			}

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
				addActionMessage("Successfully saved email template");
				WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
				wizardSession.setTemplateID(template.getId());
			} catch (EntityExistsException e) {
				addActionError(getText("EmailTemplateSave.MustHaveUniqueName"));
			} catch (Exception e) {
				addActionError(getText("EmailTemplateSave.FailedSaveTemplate", new Object[] { e.getMessage() }));
			}
			return BLANK;
		}
		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public EmailTemplate getTemplate2() {
		return template2;
	}

	public void setTemplate2(EmailTemplate template2) {
		this.template2 = template2;
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
