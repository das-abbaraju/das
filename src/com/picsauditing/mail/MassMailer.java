package com.picsauditing.mail;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectContractorAudit;

/**
 * Mass emailing tool that can send emails to a list of contractors based on contractorID or auditID
 * @author Trevor
 *
 */
public class MassMailer extends PicsActionSupport {
	List<Integer> conIDs = null;
	List<Integer> auditIDs = null;
	
	List<BasicDynaBean> data;
	EmailQueueDAO emailQueueDAO;
	EmailTemplateDAO emailTemplateDAO;
	
	public MassMailer(EmailQueueDAO emailQueueDAO, EmailTemplateDAO emailTemplateDAO) {
		this.emailQueueDAO = emailQueueDAO;
		this.emailTemplateDAO = emailTemplateDAO;
	}
	
	public String execute() {
		if (!forceLogin()) return LOGIN;
		
		// error if empty both cons and audits
		
		SelectAccount sql = new SelectAccount();
		// OR
		sql = new SelectContractorAudit();
		
		Database db = new Database();
		try {
			data = db.select(sql.toString(), true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (button != null) {
			if (button.equals("Send")) {
				if (conIDs != null) {
					for (Integer conID : conIDs) {
						EmailQueue email = new EmailQueue();
						// mail merge template
						emailQueueDAO.save(email);
					}
				}
				if (auditIDs != null) {
					for (Integer auditID : auditIDs) {
						EmailQueue email = new EmailQueue();
						// mail merge template
						emailQueueDAO.save(email);
					}
				}
			}
		}


		return SUCCESS;
	}
	
	public List<EmailTemplate> getEmailTemplates() {
		// return this person's templates
	}
}
