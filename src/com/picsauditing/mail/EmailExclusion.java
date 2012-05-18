package com.picsauditing.mail;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class EmailExclusion extends ReportActionSupport {

	@Autowired
	protected EmailQueueDAO emailQueueDAO;

	public String email;

	public String execute() throws Exception {
		permissions.tryPermission(OpPerms.EmailTemplates);
		
		if (email != null && !email.isEmpty()) {
			String quoteSafeEmail = email.replace("'", "''");

			if (button != null && button.toLowerCase().contains("save")) {
				if (emailQueueDAO.findEmailAddressExclusionAlreadyExists(quoteSafeEmail))
					addActionMessage(email + " already exists in the exclusion list");
				else if (!Strings.isValidEmail(email)) 
					addActionMessage(email + " is not a valid email address");
				else {
					addActionMessage("Added email to list. " + email + " will not receive email blast emails");
					emailQueueDAO.addEmailAddressExclusions(quoteSafeEmail);
				}
			} else if (button != null && button.toLowerCase().contains("remove")) {
				addActionMessage("Removed email from list. " + email + " will now receive email blast emails");
				emailQueueDAO.removeEmailAddressExclusions(quoteSafeEmail);
			}
		}

		SelectSQL sql = new SelectSQL("email_exclusion ee");
		sql.setSQL_CALC_FOUND_ROWS(true);
		sql.addField("ee.email");
		sql.addOrderBy("ee.email");

		run(sql);

		email = "";

		return SUCCESS;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}