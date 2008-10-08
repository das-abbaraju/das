package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.Billing;
import com.picsauditing.PICS.CertificateBean;
import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.Facilities;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;

public class Cron extends PicsActionSupport {

	protected FlagCalculator2 flagCalculator = null;
	protected OperatorAccountDAO operatorDAO = null;
	protected AppPropertyDAO appPropDao = null;
	protected AuditBuilder auditBuilder = null;
	protected CertificateDAO certificateDAO = null;
	ContractorAuditDAO contractorAuditDAO = null;

	protected long startTime = 0L;
	StringBuffer report = null;

	protected boolean flagsOnly = false;

	public Cron(FlagCalculator2 fc2, OperatorAccountDAO ops, AppPropertyDAO appProps, AuditBuilder ab,
			CertificateDAO certificateDAO, ContractorAuditDAO contractorAuditDAO) {
		this.flagCalculator = fc2;
		this.operatorDAO = ops;
		this.appPropDao = appProps;
		this.auditBuilder = ab;
		this.certificateDAO = certificateDAO;
		this.contractorAuditDAO = contractorAuditDAO;
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
				startTask("\nRunning AccountBean optimizer...");
				new AccountBean().optimizeDB();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("\nExpiring Certificates...");
				new CertificateBean().makeExpiredCertificatesExpiredStatus();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("\nUpdating Paying Facilities...");
				Facilities facilities = new Facilities();
				facilities.setFacilitiesFromDB();
				ServletContext application = ServletActionContext.getServletContext();
				new Billing().updateAllPayingFacilities(application);
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
		}

		try {
			startTask("\nCalculating Flags...");
			flagCalculator.runAll();
			endTask();
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
			String where = "expiresDate < NOW() AND auditStatus <> 'Expired'";
			List<ContractorAudit> conList = contractorAuditDAO.findWhere(70, where, "expiresDate");
			for (ContractorAudit cAudit : conList) {
				cAudit.setAuditStatus(AuditStatus.Expired);
				contractorAuditDAO.save(cAudit);
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
		List<Certificate> cList = certificateDAO.findExpiredCertificate();
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
		EmailBuilder emailBuilder = new EmailBuilder();
		for (Certificate certificate : cList) {
			emailBuilder.clear();
			emailBuilder.setTemplate(10); // Certificate Expiration
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(certificate.getContractorAccount());
			emailBuilder.addToken("opAcct", certificate.getOperatorAccount());
			emailBuilder.addToken("expiration_date", certificate.getExpiration());
			emailBuilder.addToken("certificate_type", certificate.getType());
			EmailQueue email = emailBuilder.build();
			email.setPriority(20);
			emailQueueDAO.save(email);
			ContractorBean.addNote(certificate.getContractorAccount().getId(), permissions,
					"Sent Certificate Expiration email to " + emailBuilder.getSentTo());

			certificate.setSentEmails(certificate.getSentEmails() + 1);
			certificate.setLastSentDate(new Date());
			certificateDAO.save(certificate);
		}
	}

}
