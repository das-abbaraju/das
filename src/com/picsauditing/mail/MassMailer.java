package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
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

	private String templateSubject;
	private String templateBody;
	
	private List<BasicDynaBean> list;
	private EmailQueueDAO emailQueueDAO;
	private EmailTemplateDAO emailTemplateDAO;
	
	private EmailContractorBean emailContractorBean;
	private EmailAuditBean emailAuditBean;
	private ContractorAccountDAO contractorAccountDAO;

	public MassMailer(EmailQueueDAO emailQueueDAO, EmailTemplateDAO emailTemplateDAO, 
			EmailContractorBean emailContractorBean, EmailAuditBean emailAuditBean,
			ContractorAccountDAO contractorAccountDAO) {
		this.emailQueueDAO = emailQueueDAO;
		this.emailTemplateDAO = emailTemplateDAO;
		this.emailContractorBean = emailContractorBean;
		this.emailAuditBean = emailAuditBean;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		SelectAccount sql = null;
		
		if (type == null || type.length() == 0)
			throw new Exception("Type variable is missing");
		
		if (ids == null || ids.size() == 0) {
			addActionError("Please select atleast one record to which to send an email.");
			return SUCCESS;
		}
		
		String idList = Strings.implode(ids, ",");
		if (type.equals("Contractors")) {
			emailContractorBean.setPermissions(permissions);
			sql = new SelectAccount();
			sql.addWhere("a.id IN (" + idList + ")");
		} else if (type.equals("Audits")) {
			emailAuditBean.setPermissions(permissions);
			sql = new SelectContractorAudit();
			sql.addWhere("ca.auditID IN (" + idList + ")");
		} else {
			addActionError(type + " is not supported");
			return SUCCESS;
		}

		Database db = new Database();
		list = db.select(sql.toString(), true);
		
		if (button != null) {
			if (button.equals("send")) {
				boolean templateSupportsVelocity = true;
				String filteredSubject = templateSubject;
				String filteredBody = templateBody;
				if (!templateSupportsVelocity) {
					// Strip out the velocity tags
					filteredSubject = filteredSubject.replace("${", "_");
					filteredSubject = filteredSubject.replace("}", "_");
					filteredBody = filteredBody.replace("${", "_");
					filteredBody = filteredBody.replace("}", "_");
				}
				for(Token token : getTokens()) {
					if (type.equals(token.getType())) {
						// This token is valid for this type of email template
						// Convert anything like this <Name> into something like this ${person.name}
						String find = "<" + token.getTokenName() + ">";
						String replace = "${" + token.getVelocityName() + "}";
						filteredSubject = filteredSubject.replace(find, replace);
						filteredBody = filteredBody.replace(find, replace);
					}
				}
				System.out.println("filteredSubject: " + filteredSubject);
				System.out.println("filteredBody: " + filteredBody);
				
				for (Integer id : ids) {
					// All of this should probably go back into EmailContractorBean and EmailAuditBean
					EmailQueue email = new EmailQueue();
					email.setCreatedBy(new User(permissions.getUserId()));
					email.setCreationDate(new Date());
					email.setPriority(100);
					
					Map<String, Object> tokens = new HashMap<String, Object>();
					tokens.put("permissions", permissions);
					if (type.equals("Contractors")) {
						ContractorAccount contractor = contractorAccountDAO.find(id);
						tokens.put("contractor", contractor);
						tokens.put("user", contractor);
						email.setToAddresses(contractor.getEmail());
						email.setCcAddresses(contractor.getSecondEmail());
						email.setContractorAccount(contractor);
					} else if (type.equals("Audits")) {
						//ContractorAccount contractor = this.
					}
					
					email.setSubject(VelocityAdaptor.mergeTemplate(filteredSubject, tokens));
					email.setBody(VelocityAdaptor.mergeTemplate(filteredBody, tokens));
					
					emailQueueDAO.save(email);
				}
			}
		}
		return SUCCESS;
	}

	public List<Token> getTokens() {
		List<Token> tokens = new ArrayList<Token>();
		Token t;
		t = new Token();
		t.setId(1);
		t.setTokenName("CompanyName");
		tokens.add(t);
		t = new Token();
		t.setId(1);
		t.setTokenName("ContactName");
		tokens.add(t);
		t = new Token();
		t.setId(1);
		t.setTokenName("Phone");
		tokens.add(t);
		t = new Token();
		t.setId(1);
		t.setTokenName("Email");
		tokens.add(t);
		return tokens;
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

}
