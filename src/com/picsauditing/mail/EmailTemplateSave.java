package com.picsauditing.mail;

import java.util.Date;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.util.Strings;

public class EmailTemplateSave extends PicsActionSupport implements Preparable {
	private static final long serialVersionUID = -7852679107862181163L;
	private EmailTemplateDAO emailTemplateDAO;
	private int id;
	private EmailTemplate template;
	
	public EmailTemplateSave(EmailTemplateDAO emailTemplateDAO) {
		this.emailTemplateDAO = emailTemplateDAO;
	}
	
	public String execute() {
		getPermissions();
		if (!permissions.hasPermission(OpPerms.AllOperators)
				&& template.getAccountID() != permissions.getAccountId()) {
			addActionError("You don't have permission to change this template");
			return SUCCESS;
		}
		
		if ("delete".equals(button)) {
			emailTemplateDAO.remove(template);
			addActionMessage("Successfully deleted email template");
			return BLANK;
		}
		if ("save".equals(button)) {
			if (Strings.isEmpty(template.getTemplateName()))
				addActionError("Please enter a template name");
			if (Strings.isEmpty(template.getSubject()))
				addActionError("Please enter a subject");
			if (Strings.isEmpty(template.getBody()))
				addActionError("Please enter a body");
			
			if (this.getActionErrors().size() > 0) {
				emailTemplateDAO.clear();
				return SUCCESS;
			}
			
			if (template.getId() == 0) {
				// This is a new one so do some updates
				template.setCreationDate(new Date());
				template.setCreatedBy(getUser());
				template.setAccountID(permissions.getAccountId());
			}
			template.setUpdateDate(new Date());
			template.setUpdatedBy(getUser());
			emailTemplateDAO.save(template);
			addActionMessage("Successfully saved email template");
			return BLANK;
		}
		emailTemplateDAO.clear();
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

	@Override
	public void prepare() throws Exception {
		template = emailTemplateDAO.find(getParameter("id"));
	}
}
