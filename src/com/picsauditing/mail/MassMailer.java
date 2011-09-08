package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.Token;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.search.SelectUser;
import com.picsauditing.util.SelectOption;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

/**
 * Mass emailing tool that can send emails to a list of contractors based on contractorID or auditID
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class MassMailer extends PicsActionSupport {
	private Set<Integer> ids = null;
	private ListType type;

	private int templateID = 0; // 0 means no template selected at all
	private int removeID;
	private int previewID = 0;
	final public static int BLANK_EMAIL = -1;
	private String templateName;
	private String templateSubject;
	private String templateBody;
	private OpPerms recipient;
	private boolean templateAllowsVelocity;
	private boolean templateHtml;
	private String fromMyAddress = "info@picsauditing.com";
	private List<String> fromAddresses = new ArrayList<String>();
	private boolean editTemplate = false;
	private boolean fromOtherAction;

	private List<EmailTemplate> emailTemplates = null;
	private List<Token> picsTags = null;
	private List<BasicDynaBean> data = new ArrayList<BasicDynaBean>();
	private ArrayList<SelectOption> list = new ArrayList<SelectOption>();

	private EmailQueueDAO emailQueueDAO;
	private EmailTemplateDAO emailTemplateDAO;
	private TokenDAO tokenDAO;

	private EmailBuilder emailBuilder;
	private EmailQueue emailPreview;

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
		// ActionContext.getContext().getActionInvocation().getProxy()

		// Start the main logic for actions that require passing the contractors
		// in
		WizardSession wizardSession = new WizardSession(ActionContext.getContext().getSession());
		if (!editTemplate) {
			fromOtherAction = false;
			type = wizardSession.getListType();
		} else if (type == null) {
			addActionMessage("Please select a template or start with a blank email to get started");
			return SUCCESS;
		}

		if (getEmailTemplates().size() == 0) {
			// This account has no email templates, just start with a blank one
			templateID = BLANK_EMAIL;
		}

		if ("MailEditorAjax".equals(button)) {
			if (templateID > 0) {
				EmailTemplate template = emailTemplateDAO.find(templateID);
				templateSubject = template.getSubject();
				templateBody = template.getBody();
				templateName = template.getTemplateName();
				templateAllowsVelocity = template.isAllowsVelocity();
				templateHtml = template.isHtml();
				type = template.getListType();
			} else {
				templateSubject = "";
				templateBody = "";
			}
			wizardSession.setTemplateID(templateID);
			return SUCCESS;
		}

		if ("start".equals(button)) {
			// Reset the templateID to this new passed in one
			wizardSession.setTemplateID(templateID);
		}

		if (wizardSession.getTemplateID() > 0 && templateID <= 0)
			templateID = wizardSession.getTemplateID();

		if ("MailPreviewAjax".equals(button)) {
			if (previewID == 0) {
				templateSubject = "Please one record to preview email";
				templateBody = "";
				return SUCCESS;
			}

			EmailTemplate template = buildEmailTemplate();
			emailBuilder.setTemplate(template);
			getRecipient();
			addTokens(previewID);

			emailPreview = emailBuilder.build();
			return SUCCESS;
		}

		if (ids == null || ids.size() == 0)
			ids = wizardSession.getIds();

		if (!editTemplate && (ids == null || ids.size() == 0)) {
			String url = "EmailWizard.action";
			if (type != null)
				url += "?type=" + type;

			addActionError("Please select at least one record to which to send an email. <a href='" + url
					+ "'>Click to Continue</a>");
			return BLANK;
		}

		if (button != null) {
			if (button.equals("removeCon")) {
				if (ids.contains(removeID))
					ids.remove(removeID);
			}

			if (button.equals("send")) {
				EmailTemplate template = buildEmailTemplate();
				// TODO we may want to offer sending from another email
				// other than their own
				// if (fromMyAddress)
				// emailBuilder.setFromAddress("\"" + permissions.getName() +
				// "\"<" + permissions.getEmail() + ">");
				// else
				emailBuilder.setFromAddress(fromMyAddress);
				emailBuilder.setTemplate(template);

				for (Integer id : ids) {
					getRecipient();
					try {
						addTokens(id);
						EmailQueue email = emailBuilder.build();
						// I really think we should be saving this. Not sure why we
						// weren't
						if (templateID < 0)
							email.setEmailTemplate(null);

						email.setViewableById(permissions.getTopAccountID());
						emailQueueDAO.save(email);
					} catch (EmailException e) {
						e.printStackTrace();
					}
				}
				wizardSession.clear();

				if (getActionErrors().size() > 0) {
					addActionMessage("You have sent " + (ids.size() - getActionErrors().size())
							+ " emails to the queue.");
					addActionError("<a href=\"EmailWizard.action\">Click here</a> to go back to Email Wizard.");
					return BLANK;
				}

				return "emailConfirm";
			}
		}

		// At this point if ids is empty then we've removed all possible
		// contractors
		// So return a message and url and finish
		if (!editTemplate && ids.isEmpty()) {
			String url = "EmailWizard.action";
			if (type != null)
				url += "?type=" + type;
			addActionMessage("You have removed all matches from your list, to start again " + "<a href=\"" + url
					+ "\">Click here</a> " + "to go back to the Email Wizzard");
			return SUCCESS;
		}

		if (editTemplate) {
			// if editing a template then we can exit here
			return SUCCESS;
		}

		// We aren't previewing, sending, or editing, so just get the list from
		// the db using the filters
		String idList = Strings.implode(ids, ",");
		SelectAccount sql = null;
		SelectUser sqlUser = null;

		if (ListType.Contractor.equals(type)) {
			sql = new SelectAccount();
			sql.addWhere("a.id IN (" + idList + ")");
			sql.addOrderBy("a.name");
		} else if (ListType.Audit.equals(type)) {
			sql = new SelectContractorAudit();
			sql.addWhere("ca.id IN (" + idList + ")");
			sql.addOrderBy("a.name");
		} else if (ListType.User.equals(type)) {
			sqlUser = new SelectUser();
			sqlUser.addWhere("u.id IN (" + idList + ")");
			sqlUser.addOrderBy("u.name");
		} else {
			addActionError(type + " is not supported");
			return SUCCESS;
		}

		Database db = new Database();

		if (ListType.Contractor.equals(type) || ListType.Audit.equals(type))
			data = db.select(sql.toString(), true);
		else
			data = db.select(sqlUser.toString(), true);

		for (BasicDynaBean row : data) {
			if (ListType.Contractor.equals(type)) {
				list.add(new SelectOption(row.get("id").toString(), row.get("name").toString()));
			} else if (ListType.Audit.equals(type)) {
				list.add(new SelectOption(row.get("auditID").toString(), row.get("name").toString() + " - "
						+ getText(row.get("atype.name").toString())));
			} else if (ListType.User.equals(type)) {
				list.add(new SelectOption(row.get("id").toString(), row.get("name").toString()));
			}
		}

		return SUCCESS;
	}

	private void addTokens(int id) throws EmailException {
		emailBuilder.clear();
		emailBuilder.setPermissions(permissions);
		if (ListType.Contractor.equals(type)) {
			ContractorAccountDAO dao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
			ContractorAccount contractor = dao.find(id);
			ContractorOperator co = null;

			if (permissions.isOperator()) {
				ContractorOperatorDAO coDAO = (ContractorOperatorDAO) SpringUtils.getBean("ContractorOperatorDAO");
				co = coDAO.find(id, permissions.getAccountId());
				emailBuilder.addToken("flagColor", co.getFlagColor());
			}

			if (recipient != null && !recipient.equals(OpPerms.ContractorAdmin)) {
				// Send the emails to the right recipients?
				if (recipient.equals(OpPerms.ContractorBilling)) {
					emailBuilder.setContractor(contractor, OpPerms.ContractorBilling);
				} else if (recipient.equals(OpPerms.ContractorSafety)) {
					emailBuilder.setContractor(contractor, OpPerms.ContractorSafety);
				} else if (recipient.equals(OpPerms.ContractorInsurance)) {
					emailBuilder.setContractor(contractor, OpPerms.ContractorInsurance);
				}
			} else
				emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
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
		template.setSubject(templateSubject);
		template.setBody(templateBody);
		if (permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
			template.setAllowsVelocity(templateAllowsVelocity);
			template.setHtml(templateHtml);
		}
		return template;
	}

	public List<Token> getPicsTags() {
		if (type == null)
			return Collections.emptyList();
		if (picsTags == null)
			picsTags = tokenDAO.findByType(type);
		return picsTags;
	}

	public List<EmailTemplate> getEmailTemplates() {
		if (emailTemplates == null)
			emailTemplates = emailTemplateDAO.findByAccountID(permissions.getAccountId(), type);
		return emailTemplates;
	}

	public ListType getType() {
		return type;
	}

	public void setType(ListType type) {
		this.type = type;
	}

	public List<SelectOption> getList() {
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

	public OpPerms getRecipient() {
		if (templateID > 0) {
			EmailTemplate temp = emailTemplateDAO.find(templateID);
			String recipientType = temp.getRecipient();

			if (recipientType == null || recipientType.startsWith("Admin"))
				recipient = OpPerms.ContractorAdmin;
			else if (recipientType.equals("Billing"))
				recipient = OpPerms.ContractorBilling;
			else if (recipientType.equals("Safety"))
				recipient = OpPerms.ContractorSafety;
			else if (recipientType.equals("Insurance"))
				recipient = OpPerms.ContractorInsurance;

			return recipient;
		}
		return OpPerms.ContractorAdmin;
	}

	public int getPreviewID() {
		return previewID;
	}

	public void setPreviewID(int previewID) {
		this.previewID = previewID;
	}

	public String getFromMyAddress() {
		return fromMyAddress;
	}

	public void setFromMyAddress(String fromMyAddress) {
		this.fromMyAddress = fromMyAddress;
	}

	public EmailQueue getEmailPreview() {
		return emailPreview;
	}

	public void setEmailPreviewBody() {
		// do nothing
	}

	public void setTokens() {
		// do nothing
	}

	public boolean isTemplateAllowsVelocity() {
		return templateAllowsVelocity;
	}

	public boolean isTemplateHtml() {
		return templateHtml;
	}

	public void setTemplateHtml(boolean templateHtml) {
		this.templateHtml = templateHtml;
	}

	public void setTemplateAllowsVelocity(boolean templateAllowsVelocity) {
		this.templateAllowsVelocity = templateAllowsVelocity;
	}

	public int getRemoveID() {
		return removeID;
	}

	public void setRemoveID(int removeID) {
		this.removeID = removeID;
	}

	public void setEditTemplate(boolean editTemplate) {
		this.editTemplate = editTemplate;
	}

	public boolean isEditTemplate() {
		return editTemplate;
	}

	public boolean isFromOtherAction() {
		return fromOtherAction;
	}

	public void setFromOtherAction(boolean fromOtherAction) {
		this.fromOtherAction = fromOtherAction;
	}

	public List<String> getFromAddresses() {
		if (fromAddresses.isEmpty()) {
			fromAddresses.add("info@picsauditing.com");
			if (permissions.isAuditor()) {
				fromAddresses.add("audits@picsauditing.com");
			}
			fromAddresses.add(permissions.getEmail());

		}
		return fromAddresses;
	}
}
