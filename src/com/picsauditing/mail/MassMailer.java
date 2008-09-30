package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Token;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.util.Strings;
import com.picsauditing.util.VelocityAdaptor;

/**
 * Mass emailing tool that can send emails to a list of contractors based on
 * contractorID or auditID
 * 
 * @author Trevor
 * 
 */
public class MassMailer extends PicsActionSupport {
	private List<Integer> ids = null;
	private String type = "Contractors"; // Contractors, Audits, Users

	private int templateID;
	private String templateSubject;
	private String templateBody;
	private List<Token> tokens = null;
	
	private List<BasicDynaBean> list = new ArrayList<BasicDynaBean>();
	private EmailQueueDAO emailQueueDAO;
	private EmailTemplateDAO emailTemplateDAO;
	
	//private EmailContractorBean emailContractorBean;
	//private EmailAuditBean emailAuditBean;
	private ContractorAccountDAO contractorAccountDAO;
	private TokenDAO tokenDAO;
	
	private VelocityAdaptor velocityAdaptor;

	public MassMailer(EmailQueueDAO emailQueueDAO, EmailTemplateDAO emailTemplateDAO, 
			ContractorAccountDAO contractorAccountDAO, TokenDAO tokenDAO) {
		this.emailQueueDAO = emailQueueDAO;
		this.emailTemplateDAO = emailTemplateDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.tokenDAO = tokenDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if ("MailEditorAjax".equals(button)) {
			if (templateID > 0) {
				EmailTemplate template = emailTemplateDAO.find(templateID);
				templateSubject = template.getSubject();
				templateBody = template.getBody();
			} else {
				templateSubject = "";
				templateBody = "";
			}
			return SUCCESS;
		}
		
		if ("MailPreviewAjax".equals(button)) {
			if (ids == null || ids.size() != 1) {
				templateSubject = "Please one record to preview email";
				templateBody = "";
				return SUCCESS;
			}
			int conID = ids.get(0);
			
			templateSubject = filterTemplate(templateSubject);
			templateBody = filterTemplate(templateBody);
			EmailQueue email = createEmail(conID, templateSubject, templateBody);
			
			templateSubject = email.getSubject();
			templateBody = email.getBody();
			return SUCCESS;
		}
		
		// Start the main logic for actions that require passing the contractors in
		if (type == null || type.length() == 0)
			type = "Contractors";
		
		if (ids == null || ids.size() == 0) {
			addActionError("Please select at least one record to which to send an email.");
			return "blank";
		}
		
		String idList = Strings.implode(ids, ",");
		SelectAccount sql = null;
		if (type.equals("Contractors")) {
			//emailContractorBean.setPermissions(permissions);
			sql = new SelectAccount();
			sql.addWhere("a.id IN (" + idList + ")");
			sql.addOrderBy("a.name");
		} else if (type.equals("Audits")) {
			//emailAuditBean.setPermissions(permissions);
			sql = new SelectContractorAudit();
			sql.addWhere("ca.auditID IN (" + idList + ")");
			sql.addOrderBy("a.name");
			sql.addOrderBy("atype.auditName");
		} else {
			addActionError(type + " is not supported");
			return SUCCESS;
		}

		Database db = new Database();
		list = db.select(sql.toString(), true);
		
		if (button != null) {
			if (button.equals("send")) {
				String filteredSubject = filterTemplate(templateSubject);
				String filteredBody = filterTemplate(templateBody);
				
				for (Integer conID : ids) {
					EmailQueue email = createEmail(conID, filteredSubject, filteredBody);
					emailQueueDAO.save(email);
				}
			}
		}
		return SUCCESS;
	}
	
	private EmailQueue createEmail(int conID, String subject, String body) {
		//long startTime = Calendar.getInstance().getTimeInMillis();
		
		// All of this should probably go back into EmailContractorBean and EmailAuditBean
		EmailQueue email = new EmailQueue();
		email.setCreatedBy(new User(permissions.getUserId()));
		email.setCreationDate(new Date());
		email.setPriority(10);
		
		Map<String, Object> tokens = new HashMap<String, Object>();
		tokens.put("permissions", permissions);
		if (type.equals("Contractors")) {
			ContractorAccount contractor = contractorAccountDAO.find(conID);
			tokens.put("contractor", contractor);
			tokens.put("user", contractor);
			email.setToAddresses(contractor.getEmail());
			email.setCcAddresses(contractor.getSecondEmail());
			email.setContractorAccount(contractor);
		} else if (type.equals("Audits")) {
			//ContractorAccount contractor = this.
		}
		
		try {
			subject = getVelocity().merge(subject, tokens);
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		try {
			body = getVelocity().merge(body, tokens);
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		email.setSubject(subject);
		email.setBody(body);
		
		//long endTime = Calendar.getInstance().getTimeInMillis();
		//System.out.println("ms" +  (endTime - startTime));
		return email;
	}
	
	private String filterTemplate(String template) {
		boolean templateSupportsVelocity = true; // TODO pass this in or something
		if (!templateSupportsVelocity) {
			// Strip out the velocity tags
			template = template.replace("${", "_");
			template = template.replace("}", "_");
		}
		//System.out.println("starting with: " + template);
		for(Token token : getTokens()) {
			// This token is valid for this type of email template
			// Convert anything like this <Name> into something like this ${person.name}
			String find = "<" + token.getTokenName() + ">";
			String replace = "${" + token.getVelocityName() + "}";
			//System.out.println("replace " + find + " with " + replace);
			template = template.replace(find, replace);
		}
		//System.out.println("filtered: " + template);
		return template;
	}

	public List<Token> getTokens() {
		if (tokens == null) {
			tokens = new ArrayList<Token>();
			if ("Contractors".equals(type))
				tokens = tokenDAO.findByType("Contractor");
			if ("Audits".equals(type))
				tokens = tokenDAO.findByType("Audit");
			if ("Users".equals(type))
				tokens = tokenDAO.findByType("User");
		}
		return tokens;
	}
	
	private VelocityAdaptor getVelocity() {
		if (velocityAdaptor == null)
			velocityAdaptor = new VelocityAdaptor();
		return velocityAdaptor;
	}

	public List<EmailTemplate> getEmailTemplates() {
		return emailTemplateDAO.findByAccountID(permissions.getAccountId());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<BasicDynaBean> getList() {
		return list;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public String getTemplateSubject() {
		return templateSubject;
	}

	public void setTemplateSubject(String templateSubject) {
		this.templateSubject = templateSubject;
	}

	public String getTemplateBody() {
		return templateBody;
	}

	public void setTemplateBody(String templateBody) {
		this.templateBody = templateBody;
	}

	public int getTemplateID() {
		return templateID;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}

}
