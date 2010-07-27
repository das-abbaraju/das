package com.picsauditing.util;

import java.util.Date;
import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailTemplate;

@SuppressWarnings("serial")
public class ReportFilterEmail extends ReportFilter {
	public static final String DEFAULT_TO_ADDRESS = "- To Address -";
	// Filter visibility
	protected boolean showStatus = true;
	protected boolean showTemplateName = true;
	protected boolean showContractor = true;
	protected boolean showSentDate = true;
	protected boolean showToAddress = true;

	// Filter values
	protected String[] status;
	protected String[] templateName;
	protected String accountName = ReportFilterAccount.DEFAULT_NAME;
	protected Date sentDateStart;
	protected Date sentDateEnd;
	protected String toAddress = DEFAULT_TO_ADDRESS;
	
	// Lists
	protected List<EmailTemplate> templates;

	// Permissions
	protected Permissions permissions = null;

	// Getters and Setters for Filter Visibility
	public boolean isShowStatus() {
		return showStatus;
	}

	public void setShowStatus(boolean showStatus) {
		this.showStatus = showStatus;
	}

	public boolean isShowTemplateName() {
		return showTemplateName;
	}

	public void setShowTemplateName(boolean showTemplateName) {
		this.showTemplateName = showTemplateName;
	}

	public boolean isShowContractor() {
		return showContractor;
	}

	public void setShowContractor(boolean showContractor) {
		this.showContractor = showContractor;
	}

	public boolean isShowSentDate() {
		return showSentDate;
	}

	public void setShowSentDate(boolean showSentDate) {
		this.showSentDate = showSentDate;
	}

	public boolean isShowToAddress() {
		return showToAddress;
	}

	public void setShowToAddress(boolean showToAddress) {
		this.showToAddress = showToAddress;
	}

	// Getters and Setters for Filter Values
	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public String[] getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String[] templateName) {
		this.templateName = templateName;
	}

	public String getAccountName() {
		return accountName;
	}
	
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Date getSentDateStart() {
		return sentDateStart;
	}

	public void setSentDateStart(Date sentDateStart) {
		this.sentDateStart = sentDateStart;
	}

	public Date getSentDateEnd() {
		return sentDateEnd;
	}

	public void setSentDateEnd(Date sentDateEnd) {
		this.sentDateEnd = sentDateEnd;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	@Override
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
	}
	
	// Lists
	public String[] getStatusList() {
		// Do we need to show errors?
		return new String[] { "Pending", "Sent" };
	}
	
	public List<EmailTemplate> getTemplateList() {
		if (templates == null) {
			EmailTemplateDAO templateDAO = (EmailTemplateDAO) SpringUtils.getBean("EmailTemplateDAO");
			
			if (permissions.isOperatorCorporate())
				templates = templateDAO.findByAccountID(permissions.getAccountId());
			else
				templates = templateDAO.findAll();
		}
		
		return templates;
	}
}
