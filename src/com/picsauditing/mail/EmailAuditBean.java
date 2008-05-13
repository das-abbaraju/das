package com.picsauditing.mail;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAudit;

public class EmailAuditBean extends EmailContractorBean {
	protected ContractorAudit conAudit;
	
	public void setData(String accountID, Permissions permissions) throws Exception {
		super.setData(accountID, permissions);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dow mon dd, yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
		merge.addTokens("scheduledDate", dateFormat.format(conAudit.getScheduledDate()));
		merge.addTokens("scheduledTime", timeFormat.format(conAudit.getScheduledDate()));
		merge.addTokens("auditType", conAudit.getAuditType().getAuditName());
	}

	public void sendMessage(EmailTemplates emailType, String accountID, Permissions perms, HashMap<String, String> optionalTokens) throws Exception {
		this.setData(accountID, perms);
		if (optionalTokens != null && optionalTokens.size() > 0)
			for(String key : optionalTokens.keySet())
				this.merge.addTokens(key, optionalTokens.get(key));
		this.setMerge(emailType);
		
		// This next line should really be moved back to the ContractorBean
		if (emailType.equals(EmailTemplates.dasubmit)
			|| emailType.equals(EmailTemplates.desktopsubmit)
			)
			ccAddress = "";
		
		this.sendMail();
	}
}
