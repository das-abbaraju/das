package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.Token;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

/**
 * Mass emailing tool that can send emails to a list of contractors based on
 * contractorID or auditID
 * 
 * @author Trevor
 * 
 */
public class MassMailer extends PicsActionSupport {
	private Set<Integer> ids = null;
	private ListType type = ListType.Contractor;

	private int templateID;
	private String templateName;
	private String templateSubject;
	private String templateBody;
	private List<Token> tokens = null;

	private List<BasicDynaBean> list = new ArrayList<BasicDynaBean>();
	private EmailQueueDAO emailQueueDAO;
	private EmailTemplateDAO emailTemplateDAO;

	private TokenDAO tokenDAO;
	private EmailBuilder emailBuilder;

	public MassMailer(EmailQueueDAO emailQueueDAO, EmailTemplateDAO emailTemplateDAO, TokenDAO tokenDAO) {
		this.emailQueueDAO = emailQueueDAO;
		this.emailTemplateDAO = emailTemplateDAO;
		this.tokenDAO = tokenDAO;
		this.emailBuilder = new EmailBuilder();
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EmailTemplates);
		if ("MailEditorAjax".equals(button)) {
			if (templateID > 0) {
				EmailTemplate template = emailTemplateDAO.find(templateID);
				templateSubject = template.getSubject();
				templateBody = template.getBody();
				templateName = template.getTemplateName();
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
			int id = 0;
			for(Integer tempId : ids) {
				id = tempId;
				break;
			}

			EmailTemplate template = buildEmailTemplate();
			emailBuilder.setTemplate(template);
			addTokens(id);

			EmailQueue email = emailBuilder.build();
			templateSubject = email.getSubject();
			templateBody = email.getBody();
			return SUCCESS;
		}

		// Start the main logic for actions that require passing the contractors
		// in
		if (ActionContext.getContext().getSession().containsKey("mailer_ids"))
			ids = (Set<Integer>)ActionContext.getContext().getSession().get("mailer_ids");
		if (ActionContext.getContext().getSession().containsKey("mailer_list_type"))
			type = (ListType)ActionContext.getContext().getSession().get("mailer_list_type");
		if (type == null)
			type = ListType.Contractor;

		if (ids == null || ids.size() == 0) {
			addActionError("Please select at least one record to which to send an email.");
			return BLANK;
		}

		String idList = Strings.implode(ids, ",");
		SelectAccount sql = null;

		if (ListType.Contractor.equals(type)) {
			sql = new SelectAccount();
			sql.addWhere("a.id IN (" + idList + ")");
			sql.addOrderBy("a.name");
		} else if (ListType.Audit.equals(type)) {
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
				EmailTemplate template = buildEmailTemplate();
				// TODO we may want to offer sending from another email
				// other than their own
				emailBuilder.setFromAddress(permissions.getEmail());
				emailBuilder.setTemplate(template);

				for (Integer id : ids) {
					addTokens(id);
					EmailQueue email = emailBuilder.build();
					emailQueueDAO.save(email);
				}
			}
		}
		return SUCCESS;
	}

	private void addTokens(int id) {
		emailBuilder.clear();
		emailBuilder.setPermissions(permissions);
		if (ListType.Contractor.equals(type)) {
			ContractorAccountDAO dao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
			ContractorAccount contractor = dao.find(id);
			emailBuilder.setContractor(contractor);
		}
		if (ListType.Audit.equals(type)) {
			ContractorAuditDAO dao = (ContractorAuditDAO) SpringUtils.getBean("ContractorAuditDAO");
			ContractorAudit conAudit = dao.find(id);
			emailBuilder.setConAudit(conAudit);
		}
		if (ListType.User.equals(type)) {
			UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
			User user = dao.find(id);
			emailBuilder.setUser(user);
		}
	}

	/**
	 * Convert tokens like this <TOKEN_NAME> in a given string to velocity tags
	 * like this ${token.name}
	 * 
	 * @param text
	 * @param allowsVelocity
	 * @return
	 */
	private String convertTokensToVelocity(String text, boolean allowsVelocity) {
		if (!allowsVelocity) {
			// Strip out the velocity tags
			text = text.replace("${", "_");
			text = text.replace("}", "_");
		}
		// System.out.println("starting with: " + template);
		for (Token token : getTokens()) {
			// This token is valid for this type of email template
			// Convert anything like this <Name> into something like this
			// ${person.name}
			String find = "<" + token.getTokenName() + ">";
			String replace = "${" + token.getVelocityName() + "}";
			// System.out.println("replace " + find + " with " + replace);
			text = text.replace(find, replace);
		}
		// System.out.println("filtered: " + template);
		return text;
	}

	/**
	 * Creates a velocity-ready email template from the subject and body
	 * 
	 * @return
	 */
	private EmailTemplate buildEmailTemplate() {
		EmailTemplate template = new EmailTemplate();
		template.setListType(type);
		if (templateID > 0) {
			template = emailTemplateDAO.find(templateID);
			emailTemplateDAO.clear();
		}
		template.setTemplateName(templateName);
		template.setSubject(convertTokensToVelocity(templateSubject, template.isAllowsVelocity()));
		template.setBody(convertTokensToVelocity(templateBody, template.isAllowsVelocity()));
		return template;
	}

	public List<Token> getTokens() {
		if (type == null)
			return null;
		if (tokens == null)
			tokens = tokenDAO.findByType(type);
		return tokens;
	}

	public List<EmailTemplate> getEmailTemplates() {
		// TODO restrict based on list type
		return emailTemplateDAO.findByAccountID(permissions.getAccountId());
	}

	public ListType getType() {
		return type;
	}

	public void setType(ListType type) {
		this.type = type;
	}

	public List<BasicDynaBean> getList() {
		return list;
	}

	public Set<Integer> getIds() {
		return ids;
	}

	public void setIds(Set<Integer> ids) {
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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
