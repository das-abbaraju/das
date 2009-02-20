package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class Cron extends PicsActionSupport {

	protected OperatorAccountDAO operatorDAO = null;
	protected AppPropertyDAO appPropDao = null;
	protected AuditBuilder auditBuilder = null;
	protected ContractorAuditDAO contractorAuditDAO = null;
	protected ContractorAccountDAO contractorAccountDAO = null;

	protected long startTime = 0L;
	StringBuffer report = null;

	protected boolean flagsOnly = false;

	public Cron(OperatorAccountDAO ops, AppPropertyDAO appProps, AuditBuilder ab,
			ContractorAuditDAO contractorAuditDAO, ContractorAccountDAO contractorAccountDAO) {
		this.operatorDAO = ops;
		this.appPropDao = appProps;
		this.auditBuilder = ab;
		this.contractorAuditDAO = contractorAuditDAO;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {

		report = new StringBuffer();

		report.append("Starting Cron Job at: ");
		report.append(new Date().toString());
		report.append("\n\n");

		if (!flagsOnly) {

			startTask("\nRunning auditBuilder.addAuditRenewals...");
			auditBuilder.addAuditRenewals();
			endTask();

			try {
				// TODO - Move this to the db.picsauditing.com cron bash script
				/*
				 * OPTIMIZE TABLE OSHA,accounts,auditCategories,auditData,auditQuestions,certificates,contractor_info,"
				+ "forms,generalContractors,loginLog,users;
				 */
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("\nSending emails to contractors for expired Certificates...");
				sendEmailExpiredCertificates();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("\nExpiring Audits...");
				String where = "expiresDate < NOW() AND auditStatus IN ('Submitted','Exempt','Active')";
				List<ContractorAudit> conList = contractorAuditDAO.findWhere(70, where, "expiresDate");
				for (ContractorAudit cAudit : conList) {
					if (cAudit.getAuditType().isPqf())
						cAudit.setAuditStatus(AuditStatus.Pending);
					else
						cAudit.setAuditStatus(AuditStatus.Expired);
					contractorAuditDAO.save(cAudit);
				}
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
		}

		try {
			startTask("\nInactivating Accounts via Billing Status...");
			String where = "a.active = 'Y' AND c.renew = 0 AND paymentExpires < NOW()";
			List<ContractorAccount> conAcctList = contractorAccountDAO.findWhere(where); 
			for (ContractorAccount conAcct : conAcctList) {
				conAcct.setActive('N');
				conAcct.setMembershipLevel(null);
				conAcct.setPaymentExpires(null);
				contractorAccountDAO.save(conAcct);
			}
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		report.append("\n\n\nCompleted Cron Job at: ");
		report.append(new Date().toString());

		sendEmail();

		output = "Complete";

		return SUCCESS;
	}

	private void handleException(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		report.append(t.getMessage());
		report.append(sw.toString());
		report.append("\n\n\n");
	}

	protected void endTask() {
		report.append("SUCCESS..(");
		report.append(new Long(System.currentTimeMillis() - startTime).toString());
		report.append(" millis )");
	}

	protected void startTask(String taskName) {
		startTime = System.currentTimeMillis();
		report.append(taskName);
	}

	protected void sendEmail() {
		String toAddress = null;
		try {
			AppProperty prop = appPropDao.find("admin_email_address");
			toAddress = prop.getValue();
		} catch (NoResultException notFound) {
		}
			
		if (toAddress == null || toAddress.length() == 0) {
			toAddress = "admin@picsauditing.com";
		}

		try {
			EmailSender.send(toAddress, "Cron job report", report.toString());
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("**********************************");
			System.out.println("Error Sending email from cron job");
			System.out.println("**********************************");

			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}

	}

	public boolean isFlagsOnly() {
		return flagsOnly;
	}

	public void setFlagsOnly(boolean flagsOnly) {
		this.flagsOnly = flagsOnly;
	}

	public void sendEmailExpiredCertificates() throws Exception {
		List<ContractorAudit> cList = contractorAuditDAO.findExpiredCertificates();
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		EmailBuilder emailBuilder = new EmailBuilder();
		Set<ContractorAccount> policies = new HashSet<ContractorAccount>();

		for (ContractorAudit cAudit : cList) {
			policies.add(cAudit.getContractorAccount());
		}
		for (ContractorAccount policy : policies) {
			emailBuilder.clear();
			emailBuilder.setTemplate(10); // Certificate Expiration
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(policy);
			emailBuilder.addToken("policies", policy);
			EmailQueue email = emailBuilder.build();
			email.setPriority(30);
			emailQueueDAO.save(email);
			
			Note note = new Note();
			note.setAccount(policy);
			note.setAuditColumns(this.getUser());
			note.setSummary("Sent Policy Expiration Email to "
					+ emailBuilder.getSentTo());
			note.setNoteCategory(NoteCategory.Insurance);
			note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);
		}
	}
}
