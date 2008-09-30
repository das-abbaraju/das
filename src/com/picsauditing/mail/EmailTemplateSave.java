package com.picsauditing.mail;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailTemplate;

public class EmailTemplateSave extends PicsActionSupport implements Preparable {
	private static final long serialVersionUID = -7852679107862181163L;
	private EmailTemplateDAO emailTemplateDAO;
	private int id;
	private EmailTemplate template;
	
	public EmailTemplateSave(EmailTemplateDAO emailTemplateDAO) {
		this.emailTemplateDAO = emailTemplateDAO;
	}
	
	public String execute() {
		if ("delete".equals(button)) {
			emailTemplateDAO.remove(id);
			return BLANK;
		}
		if ("save".equals(button)) {
			emailTemplateDAO.save(template);
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

	@Override
	public void prepare() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
