package com.picsauditing.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorCronStatistics;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.FlagDataOverrideDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.FlagOverrideHistory;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.search.Database;
import com.picsauditing.util.EbixLoader;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.OperatorUtil;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class Cron extends PicsActionSupport {
	static protected User system = new User(User.SYSTEM);
	@Autowired
	protected AppPropertyDAO appPropDao;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
	@Autowired
	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private FlagDataOverrideDAO flagDataOverrideDAO;
	@Autowired
	protected InvoiceDAO invoiceDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;
	@Autowired
	private InvoiceItemDAO invoiceItemDAO;
	@Autowired
	protected OperatorAccountDAO operatorDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected UserDAO userDAO;

	@Autowired
	protected AuditBuilder auditBuilder;
	@Autowired
	protected AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private EbixLoader ebixLoader;
	@Autowired
	private EmailSenderSpring emailSender;
	@Autowired
	private IndexerEngine indexer;

	protected long startTime = 0L;
	StringBuffer report = null;

	protected boolean flagsOnly = false;

	private List<String> emailExclusionList = new ArrayList<String>();
	private int pending1stReminderTemplate = 185;
	private int pending2ndReminderTemplate = 186;
	private int pending3rdReminderTemplate = 187;
	private int pending4thAndLastChanceTemplate = 188;
	private int pending5thAndFinalTemplate = 201;
	private int pending5thAndFinalAlternateTemplate = 202;
	private int pendingOperatorEmailTemplate = 203;
	private int regReq1stReminderTemplate = 211;
	private int regReq2ndReminderTemplate = 212;
	private int regReq3rdReminderTemplate = 214;
	private int regReq4thAndLastChanceTemplate = 216;
	private int regReq5thAndFinalTemplate = 217;
	private int regReqOperatorEmailTemplate = 218;
	private int possibleDuplciateEmailTemplate = 234;

	private final Logger logger = LoggerFactory.getLogger(Cron.class);
	@Anonymous
	public String execute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();

		report = new StringBuffer();
		report.append("Running Cron Job on Server: " + request.getLocalName() + "\n\n");
		report.append("Address: " + request.getLocalAddr() + "\n\n");
		report.append("Cron Job initiated by: " + request.getRemoteAddr() + "\n\n");
		report.append("Starting Cron Job at: " + new Date().toString());
		report.append("\n\n\n");

		if (!flagsOnly) {

			startTask("Running auditBuilder.addAuditRenewals...");
			List<ContractorAccount> contractors = contractorAuditDAO.findContractorsWithExpiringAudits();
			for (ContractorAccount contractor : contractors) {
				try {
					auditBuilder.buildAudits(contractor);
					contractorAuditDAO.save(contractor);
				} catch (Exception e) {
					logger.error("ERROR!! AuditBuiler.addAuditRenewals() {}", e.getMessage());
				}
			}
			endTask();

			try {
				// TODO - Move this to the db.picsauditing.com cron bash script
				/*
				 * OPTIMIZE TABLE
				 * OSHA,accounts,auditCategories,auditData,auditQuestions
				 * ,certificates,contractor_info," +
				 * "forms,generalContractors,loginLog,users;
				 */
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("Running Huntsman EBIX Support...");
				processEbixData();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("Resetting Renewable Audits and cao and stamping notes...");
				contractorAuditOperatorDAO.resetRenewableAudits();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				// TODO we shouldn't recacluate audits, but only categories.
				// This shouldn't be needed at all anymore
				startTask("Recalculating all the categories for Audits...");
				List<ContractorAudit> conList = contractorAuditDAO.findAuditsNeedingRecalculation();
				for (ContractorAudit cAudit : conList) {
					auditPercentCalculator.percentCalculateComplete(cAudit, true);
					cAudit.setLastRecalculation(new Date());
					cAudit.setAuditColumns(system);
					contractorAuditDAO.save(cAudit);
				}
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
			try {
				startTask("Starting Indexer...");
				runIndexer();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
		}

		try {
			startTask("Sending emails to contractors pending...");

			getEmailExclusions();

			sendEmailPendingAccounts();

			emailExclusionList.clear();

			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Sending emails to registration requests...");

			getEmailExclusions();

			sendEmailContractorRegistrationRequest();

			emailExclusionList.clear();

			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Sending email about upcoming implementation audits...");

			sendUpcomingImplementationAuditEmail();

			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Adding Late Fee to Delinquent Contractor Invoices ...");
			addLateFeeToDelinquentInvoices();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Inactivating Accounts via Billing Status...");
			String where = "a.status = 'Active' AND a.renew = 0 AND paymentExpires < NOW()";
			List<ContractorAccount> conAcctList = contractorAccountDAO.findWhere(where);
			for (ContractorAccount contractor : conAcctList) {
				contractor.setRenew(false);
				contractor.setStatus(AccountStatus.Deactivated);
				// Setting a deactivation reason
				if (contractor.getAccountLevel().isBidOnly()) {
					contractor.setReason("Bid Only Account");
				}
				// Leave the PaymentExpires in the past
				// conAcct.setPaymentExpires(null);
				contractor.syncBalance();
				contractor.setAuditColumns(system);
				contractorAccountDAO.save(contractor);

				stampNote(contractor, "Automatically inactivating account based on expired membership",
						NoteCategory.Billing);
			}
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Sending Email to Delinquent Contractors ...");
			sendDelinquentContractorsEmail();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Sending No Action Email to Bid Only Accounts ...");
			sendNoActionEmailToTrialAccounts();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}
		try {
			startTask("Stamping Notes and Expiring overall Forced Flags and Individual Data Overrides...");
			clearForceFlags();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Expiring Flag Changes...");
			expireFlagChanges();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Emailing Flag Change Reports...");
			sendFlagChangesEmails();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("Checking System Status...");
			checkSystemStatus();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}
		try {
			startTask("Checking Registration Requests Hold Dates...");
			checkRegistrationRequestsHoldDates();
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

	private void getEmailExclusions() {
		List<String> exclusionList = emailQueueDAO.findEmailAddressExclusions();
		if (exclusionList != null && !exclusionList.isEmpty())
			emailExclusionList.addAll(exclusionList);
	}

	private void handleException(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		report.append("\n\n\n");
		report.append(t.getMessage());
		report.append(sw.toString());
		report.append("\n\n\n");
	}

	protected void endTask() {
		report.append("SUCCESS...(");
		report.append(new Long(System.currentTimeMillis() - startTime).toString());
		report.append(" millis )");
		report.append("\n\n");
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
			emailSender.send(toAddress, "Cron job report", report.toString());
			System.out.println(report.toString());
		} catch (Exception notMuchWeCanDoButLogIt) {
			logger.error("**********************************");
			logger.error("Error Sending email from cron job");
			logger.error("**********************************");
			logger.error(notMuchWeCanDoButLogIt.getMessage());
		}

	}

	public boolean isFlagsOnly() {
		return flagsOnly;
	}

	public void setFlagsOnly(boolean flagsOnly) {
		this.flagsOnly = flagsOnly;
	}

	private void sendEmailPendingAccounts() throws Exception {
		String exclude = Strings.implodeForDB(emailExclusionList, ",");
		String where = "a.country IN ('US','CA') AND u.email NOT IN (" + exclude + ") AND ";
		String where1stReminder = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 1 DAY)";
		String where2ndReminder = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
		String where3rdReminder = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 2 WEEK)";
		String where4thAndLastChance = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 1 MONTH)";
		String where5thAndFinal = where
				+ "DATE(a.creationDate) = DATE_SUB(DATE_SUB(CURDATE(),INTERVAL 1 MONTH),INTERVAL 1 WEEK)";

		String activationReminderNote = "Sent Activation Reminder Email to ";
		String activationLastReminderNote = "Sent Activation Last Chance Reminder Email to ";
		String deactivationNote = "Final email sent to Contractor and Client Site. Notification Email sent to ";

		// Pending accounts are reminded to finish registration at 1 day
		List<ContractorAccount> pending1stReminder = contractorAccountDAO.findPendingAccounts(where1stReminder);
		runAccountEmailBlast(pending1stReminder, pending1stReminderTemplate, activationReminderNote);

		// Pending accounts are reminded to finish registration at 3 days
		List<ContractorAccount> pending2ndReminder = contractorAccountDAO.findPendingAccounts(where2ndReminder);
		runAccountEmailBlast(pending2ndReminder, pending2ndReminderTemplate, activationReminderNote);

		// Pending accounts are reminded to finish registration at 2 weeks
		List<ContractorAccount> pending3rdReminder = contractorAccountDAO.findPendingAccounts(where3rdReminder);
		runAccountEmailBlast(pending3rdReminder, pending3rdReminderTemplate, activationReminderNote);

		// Pending accounts are reminded one last time that they have a week to
		// activate
		List<ContractorAccount> pending4thAndLastChance = contractorAccountDAO
				.findPendingAccounts(where4thAndLastChance);
		runAccountEmailBlast(pending4thAndLastChance, pending4thAndLastChanceTemplate, activationLastReminderNote);

		// deactivate the account
		List<ContractorAccount> pending5thAndFinal = contractorAccountDAO.findPendingAccounts(where5thAndFinal);
		runAccountEmailBlast(pending5thAndFinal, pending5thAndFinalTemplate, deactivationNote);
	}

	private void runAccountEmailBlast(List<ContractorAccount> list, int templateID, String newNote)
			throws EmailException, IOException {
		Map<OperatorAccount, List<ContractorAccount>> operatorContractors = new HashMap<OperatorAccount, List<ContractorAccount>>();

		for (ContractorAccount contractor : list) {
			if (contractor.getPrimaryContact() != null
					&& !emailExclusionList.contains(contractor.getPrimaryContact().getEmail())) {

				boolean sendEmailToContractors = true;

				if (templateID == pending5thAndFinalTemplate) {
					List<ContractorAccount> duplicateContractors = contractorAccountDAO
							.findWhere(whereDuplicateNameIndex(contractor.getNameIndex()));

					if (!duplicateContractors.isEmpty()) {
						sendEmailToContractors = false;

						EmailBuilder emailBuilder = new EmailBuilder();

						emailBuilder.setFromAddress("info@picsauditing.com");
						emailBuilder.setToAddresses("Registrations@picsauditing.com");
						emailBuilder.addToken("contractor", contractor);
						emailBuilder.addToken("duplicates", duplicateContractors);
						emailBuilder.addToken("type", "Pending Account");
						emailBuilder.setTemplate(possibleDuplciateEmailTemplate);

						EmailQueue email = emailBuilder.build();
						email.setLowPriority();
						email.setViewableById(Account.EVERYONE);
						emailQueueDAO.save(email);
					}
				}

				if (sendEmailToContractors) {
					OperatorAccount requestedByOperator = contractor.getRequestedBy();

					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setFromAddress("Registrations@picsauditing.com");
					emailBuilder.setPermissions(permissions);
					emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
					emailExclusionList.add(contractor.getPrimaryContact().getEmail());

					contractor.getPrimaryContact().setName(StringUtils.trim(contractor.getPrimaryContact().getName()));

					emailBuilder.addToken("contractor", contractor);

					if (requestedByOperator != null) {
						emailBuilder.addToken("clientSite", requestedByOperator);
						emailBuilder.setTemplate(templateID);
					} else {
						if (templateID == pending5thAndFinalTemplate)
							emailBuilder.setTemplate(pending5thAndFinalAlternateTemplate);
						else {
							int noFacilityTemplateID = templateID + 10;
							emailBuilder.setTemplate(noFacilityTemplateID);
						}
					}

					Calendar cal = Calendar.getInstance();
					if (templateID != pending5thAndFinalTemplate)
						cal.add(Calendar.DAY_OF_MONTH, 7);
					emailBuilder.addToken("date", cal.getTime());

					EmailQueue email = emailBuilder.build();
					email.setViewableById(Account.EVERYONE);
					emailQueueDAO.save(email);

					// update the contractor notes
					stampNote(contractor, newNote + emailBuilder.getSentTo(), NoteCategory.Registration);
					if (templateID == pending5thAndFinalTemplate) {
						if (operatorContractors.get(requestedByOperator) == null)
							operatorContractors.put(requestedByOperator, new ArrayList<ContractorAccount>());

						operatorContractors.get(requestedByOperator).add(contractor);
					}
				}
			}
		}

		// send emails out to all client sites whose contractors these were for
		for (OperatorAccount operator : operatorContractors.keySet()) {
			List<ContractorAccount> contractors = operatorContractors.get(operator);

			if (operator != null && operator.getPrimaryContact() != null
					&& !emailExclusionList.contains(operator.getPrimaryContact().getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.setFromAddress("Registrations@picsauditing.com");
				emailBuilder.setPermissions(permissions);
				emailBuilder.setToAddresses(operator.getPrimaryContact().getEmail());
				emailExclusionList.add(operator.getPrimaryContact().getEmail());

				emailBuilder.addToken("clientSite", operator);
				emailBuilder.addToken("user", operator.getPrimaryContact());
				emailBuilder.addToken("contractors", contractors);
				emailBuilder.setTemplate(pendingOperatorEmailTemplate);

				EmailQueue email = emailBuilder.build();
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				// update the notes
				stampNote(operator,
						"Contractor Pending Account expired and has been deactivated. Client site was notified at this address: "
								+ emailBuilder.getSentTo(), NoteCategory.Registration);
			}
		}
	}

	private String whereDuplicateNameIndex(String name) {
		return "a.status = 'Active' "
				+ "AND LENGTH(REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD','')) > LENGTH(REPLACE(REPLACE(REPLACE('"
				+ StringUtils.defaultIfEmpty(name, "")
				+ "','CORP',''),'INC',''),'LTD',''))-3 "
				+ "AND LENGTH(REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD','')) < LENGTH(REPLACE(REPLACE(REPLACE('"
				+ StringUtils.defaultIfEmpty(name, "")
				+ "','CORP',''),'INC',''),'LTD',''))+3 "
				+ "AND (REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD','') LIKE CONCAT('%',REPLACE(REPLACE(REPLACE('"
				+ StringUtils.defaultIfEmpty(name, "")
				+ "','CORP',''),'INC',''),'LTD',''),'%') "
				+ "OR REPLACE(REPLACE(REPLACE('"
				+ StringUtils.defaultIfEmpty(name, "")
				+ "','CORP',''),'INC',''),'LTD','') LIKE CONCAT('%',REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD',''),'%'))";
	}

	private void sendUpcomingImplementationAuditEmail() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);

		List<ContractorAudit> caList = contractorAuditDAO.findScheduledAuditsByAuditId(AuditType.OFFICE,
				DateBean.setToStartOfDay(cal.getTime()), DateBean.setToEndOfDay(cal.getTime()));
		for (ContractorAudit ca : caList) {
			EventSubscriptionBuilder.notifyUpcomingImplementationAudit(ca);
		}

	}

	private void sendEmailContractorRegistrationRequest() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		// ignore all email accounts that have been sent a pending email within
		// the last month
		List<String> emailsAlreadySentToPending = emailQueueDAO.findPendingActivationEmails("1 MONTH");
		emailExclusionList.addAll(emailsAlreadySentToPending);

		String exclude = Strings.implodeForDB(emailExclusionList, ",");
		String where = "c.country IN ('US','CA') AND c.email NOT IN (" + exclude + ") AND c.conID IS NULL AND ";

		String where1stReminder = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
		String where2ndReminder = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 6 DAY)";
		String where3rdReminder = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 14 DAY)";
		String where4thAndLastChance = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 21 DAY)";
		String where5thAndFinal = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 28 DAY)";

		String reminderNote = sdf.format(new Date()) + " - Email has been sent to remind contractor to register.\n\n";
		String lastChanceNote = sdf.format(new Date())
				+ " - Email has been sent to contractor warning them that this is their last chance to register.\n\n";
		String finalAndExpirationNote = sdf.format(new Date()) + " - Final email sent to Contractor and Client Site.\n\n";

		// First notification: 3 days
		List<ContractorRegistrationRequest> crrList1stReminder = contractorRegistrationRequestDAO
				.findActiveByDate(where1stReminder);
		runCRREmailBlast(crrList1stReminder, regReq1stReminderTemplate, reminderNote);

		// 1st reminder: 1 week 3 days
		List<ContractorRegistrationRequest> crrList2ndReminder = contractorRegistrationRequestDAO
				.findActiveByDate(where2ndReminder);
		runCRREmailBlast(crrList2ndReminder, regReq2ndReminderTemplate, reminderNote);

		// 2nd reminder: 2 weeks 3 days
		List<ContractorRegistrationRequest> crrList3rdReminder = contractorRegistrationRequestDAO
				.findActiveByDate(where3rdReminder);
		runCRREmailBlast(crrList3rdReminder, regReq3rdReminderTemplate, reminderNote);

		// final reminder: 3 weeks 3 days
		List<ContractorRegistrationRequest> crrList4thAndLastChance = contractorRegistrationRequestDAO
				.findActiveByDate(where4thAndLastChance);
		runCRREmailBlast(crrList4thAndLastChance, regReq4thAndLastChanceTemplate, lastChanceNote);

		// Closing the registration requests.
		List<ContractorRegistrationRequest> crrList5thAndFinal = contractorRegistrationRequestDAO
				.findActiveByDate(where5thAndFinal);
		runCRREmailBlast(crrList5thAndFinal, regReq5thAndFinalTemplate, finalAndExpirationNote);
	}

	private void runCRREmailBlast(List<ContractorRegistrationRequest> list, int templateID, String newNote)
			throws IOException {
		Map<User, List<ContractorRegistrationRequest>> operatorContractors = new HashMap<User, List<ContractorRegistrationRequest>>();

		for (ContractorRegistrationRequest crr : list) {
			if (!emailExclusionList.contains(crr.getEmail())) {

				boolean sendEmailToContractors = true;

				if (templateID == regReq5thAndFinalTemplate) {
					List<ContractorAccount> duplicateContractors = contractorAccountDAO
							.findWhere(whereDuplicateNameIndex(crr.getName()));

					if (!duplicateContractors.isEmpty()) {
						sendEmailToContractors = false;

						EmailBuilder emailBuilder = new EmailBuilder();

						emailBuilder.setFromAddress("info@picsauditing.com");
						emailBuilder.setToAddresses("Registrations@picsauditing.com");
						emailBuilder.addToken("contractor", crr);
						emailBuilder.addToken("duplicates", duplicateContractors);
						emailBuilder.addToken("type", "Registration Request");
						emailBuilder.setTemplate(possibleDuplciateEmailTemplate);

						EmailQueue email = emailBuilder.build();
						email.setLowPriority();
						email.setViewableById(Account.EVERYONE);
						emailQueueDAO.save(email);
					}
				}

				if (sendEmailToContractors) {
					EmailBuilder emailBuilder = new EmailBuilder();

					if ((templateID == regReq2ndReminderTemplate || templateID == regReq3rdReminderTemplate) && crr.getDeadline().before(new Date()))
						templateID++;
					emailBuilder.setTemplate(templateID);

					emailBuilder.setFromAddress("Registrations@picsauditing.com");
					emailBuilder.setToAddresses(crr.getEmail());
					emailExclusionList.add(crr.getEmail());

					crr.setName(StringUtils.trim(crr.getName()));

					emailBuilder.addToken("contractor", crr);
					emailBuilder.addToken("clientSite", crr.getRequestedBy());

					// try to find the client site user responsible for this
					// request
					User operatorUser = crr.getRequestedByUser();
					if (operatorUser == null)
						operatorUser = new User(crr.getRequestedByUserOther());

					emailBuilder.addToken("user", operatorUser);
					Calendar cal = Calendar.getInstance();
					if (templateID != regReq4thAndLastChanceTemplate) {
						cal.setTime(crr.getCreationDate());
						cal.add(Calendar.DAY_OF_MONTH, 3);
						cal.add(Calendar.WEEK_OF_YEAR, 3);
					} else {
						cal.add(Calendar.WEEK_OF_YEAR, 2);
					}
					emailBuilder.addToken("date", cal.getTime());

					EmailQueue email = emailBuilder.build();
					email.setViewableById(Account.EVERYONE);
					emailQueueDAO.save(email);

					// update the registration request
					String notes = crr.getNotes();
					crr.contactByEmail();
					crr.setLastContactDate(new Date());
					notes = newNote + notes;
					crr.setNotes(notes);
					if (templateID == regReq5thAndFinalTemplate) {
						if (operatorContractors.get(operatorUser) == null)
							operatorContractors.put(operatorUser, new ArrayList<ContractorRegistrationRequest>());

						operatorContractors.get(operatorUser).add(crr);
					}
					contractorRegistrationRequestDAO.save(crr);
				}
			}
		}

		for (User operatorUser : operatorContractors.keySet()) {
			List<ContractorRegistrationRequest> contractors = operatorContractors.get(operatorUser);

			if (operatorUser != null && operatorUser.getEmail() != null
					&& !emailExclusionList.contains(operatorUser.getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.setFromAddress("Registrations@picsauditing.com");
				emailBuilder.setToAddresses(operatorUser.getEmail());
				emailExclusionList.add(operatorUser.getEmail());

				emailBuilder.addToken("clientSite", operatorUser.getAccount());
				emailBuilder.addToken("user", operatorUser);
				emailBuilder.addToken("contractors", contractors);
				emailBuilder.setTemplate(regReqOperatorEmailTemplate);

				EmailQueue email = emailBuilder.build();
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				stampNote(operatorUser.getAccount(),
						"Contractor Registration Request expired and has been closed. Client site was notified at this address: "
								+ emailBuilder.getSentTo(), NoteCategory.Registration);
			}
		}
	}

	public void runIndexer() throws Exception {
		PicsLogger.start("");
		indexer.runAll(indexer.getEntries());
		PicsLogger.stop();
	}

	public void processEbixData() throws Exception {
		PicsLogger.start("cron_ebix");
		ebixLoader.load();
		PicsLogger.stop();
	}

	private void stampNote(Account account, String text, NoteCategory noteCategory) {
		Note note = new Note(account, system, text);
		note.setCanContractorView(true);
		note.setPriority(LowMedHigh.High);
		note.setNoteCategory(noteCategory);
		note.setAuditColumns(system);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public void sendDelinquentContractorsEmail() throws Exception {
		List<Invoice> invoices = contractorAccountDAO.findDelinquentContractors();
		Map<ContractorAccount, Set<String>> cMap = new TreeMap<ContractorAccount, Set<String>>();
		Map<ContractorAccount, Integer> templateMap = new TreeMap<ContractorAccount, Integer>();

		for (Invoice invoice : invoices) {
			Set<String> emailAddresses = new HashSet<String>();
			ContractorAccount cAccount = (ContractorAccount) invoice.getAccount();

			User billing = cAccount.getUsersByRole(OpPerms.ContractorBilling).get(0);
			if (!Strings.isEmpty(billing.getEmail()))
				emailAddresses.add(billing.getEmail());
			if (!Strings.isEmpty(cAccount.getCcEmail()))
				emailAddresses.add(cAccount.getCcEmail());

			if (DateBean.getDateDifference(invoice.getDueDate()) < -10) {
				List<Integer> questionsWithEmailAddresses = Arrays.<Integer> asList(604, 606, 624, 627, 630, 1437);
				List<AuditData> aList = auditDataDAO.findAnswerByConQuestions(cAccount.getId(),
						questionsWithEmailAddresses);
				for (AuditData auditData : aList) {
					if (!Strings.isEmpty(auditData.getAnswer()) && Strings.isValidEmail(auditData.getAnswer()))
						emailAddresses.add(auditData.getAnswer());
				}
			}
			cMap.put(cAccount, emailAddresses);

			if (invoice.getDueDate().before(new Date()))
				templateMap.put(cAccount, 48); // deactivation
			else
				templateMap.put(cAccount, 50); // open
		}

		for (ContractorAccount cAccount : cMap.keySet()) {
			String emailAddress = Strings.implode(cMap.get(cAccount), ",");
			EmailBuilder emailBuilder = new EmailBuilder();

			emailBuilder.setTemplate(templateMap.get(cAccount));
			emailBuilder.setContractor(cAccount, OpPerms.ContractorBilling);
			emailBuilder.setCcAddresses(emailAddress);
			EmailQueue email = emailBuilder.build();
			email.setLowPriority();
			email.setViewableById(Account.PicsID);
			emailQueueDAO.save(email);

			stampNote(cAccount, "Deactivation Email Sent to " + emailAddress, NoteCategory.Billing);
		}
	}

	public void addLateFeeToDelinquentInvoices() throws Exception {
		// Get delinquent Invoices missing Late Fees
		List<Invoice> invoicesMissingLateFees = invoiceDAO.findDelinquentInvoicesMissingLateFees();

		for (Invoice i : invoicesMissingLateFees) {
			boolean hasReactivation = false;

			// Skip Reactivations
			for (InvoiceItem ii : i.getItems())
				if (ii.getInvoiceFee().isReactivation())
					hasReactivation = true;

			if (!hasReactivation) {
				// Calculate Late Fee
				BigDecimal lateFee = i.getTotalAmount().multiply(BigDecimal.valueOf(0.05))
						.setScale(0, BigDecimal.ROUND_HALF_UP);
				if (lateFee.compareTo(BigDecimal.valueOf(20)) < 1)
					lateFee = BigDecimal.valueOf(20);

				InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee,
						((ContractorAccount) i.getAccount()).getPayingFacilities());
				InvoiceItem lateFeeItem = new InvoiceItem(fee);
				lateFeeItem.setAmount(lateFee);
				lateFeeItem.setAuditColumns(new User(User.SYSTEM));
				lateFeeItem.setInvoice(i);
				lateFeeItem.setDescription("Assessed " + new SimpleDateFormat("MM/dd/yyyy").format(new Date())
						+ " due to delinquent payment.");

				// Add Late Fee to Invoice
				i.getItems().add(lateFeeItem);
				i.updateAmount();
				i.updateAmountApplied();
				i.setQbSync(true);
				i.setAuditColumns(new User(User.SYSTEM));
				if (i.getAccount() instanceof ContractorAccount) {
					((ContractorAccount) i.getAccount()).syncBalance();
					invoiceItemDAO.save(i.getAccount());
				}
				invoiceItemDAO.save(lateFeeItem);
				invoiceItemDAO.save(i);
			}
		}
	}

	public void sendNoActionEmailToTrialAccounts() throws Exception {
		List<ContractorAccount> conList = contractorAccountDAO.findBidOnlyContractors();

		for (ContractorAccount cAccount : conList) {
			EmailBuilder emailBuilder = new EmailBuilder();

			emailBuilder.setTemplate(70);
			// No Action Email Notification - Contractor
			emailBuilder.setContractor(cAccount, OpPerms.ContractorAdmin);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			EmailQueue email = emailBuilder.build();
			email.setLowPriority();
			email.setViewableById(Account.EVERYONE);
			emailQueueDAO.save(email);

			stampNote(cAccount, "No Action Email Notification sent to " + cAccount.getPrimaryContact().getEmail(),
					NoteCategory.General);
		}
	}

	public void expireFlagChanges() throws Exception {

		String query = "UPDATE generalcontractors gc ";
		query += "JOIN accounts a ON gc.subID = a.id ";
		query += "JOIN contractor_info c ON a.id = c.id ";
		query += "SET baselineFlag = flag, ";
		query += "baselineFlagDetail = flagDetail, ";
		query += "baselineApproved = NOW(), ";
		query += "baselineApprover = 1 ";
		query += "WHERE flag != baselineFlag ";
		// Ignore Flag Changes that are two weeks old or longer
		query += "AND (flagLastUpdated <= DATE_SUB(NOW(), INTERVAL 14 DAY) ";
		// Automatically approve a. Audited - Unspecified Facility
		// and a. PQF Only - Unspecified Facility
		query += "OR genID IN (10403,2723) ";
		// Ignore Flag Changes for newly created contractors
		query += "OR a.creationDate >= DATE_SUB(NOW(), INTERVAL 2 WEEK) ";
		// Ignore Flag Changes for recently added contractors
		query += "OR gc.creationDate >= DATE_SUB(NOW(), INTERVAL 2 WEEK) ";
		// Ignore Clear Flag Changes
		query += "OR flag = 'Clear' OR baselineFlag = 'Clear'";
		// Removed Forced Overall Flags
		query += "OR (forceFlag IS NOT NULL AND NOW() < forceEnd))";

		new Database().executeUpdate(query);
	}

	private void sendFlagChangesEmails() throws Exception {
		List<BasicDynaBean> data = getFlagChangeData();
		if (data.isEmpty())
			return;

		sendFlagChangesEmail("flagchanges@picsauditing.com", data);

		Map<String, List<BasicDynaBean>> amMap = sortResultsByAccountManager(data);
		if (!CollectionUtils.isEmpty(amMap)) {
			for (String accountMgr : amMap.keySet()) {
				if (!Strings.isEmpty(accountMgr) && amMap.get(accountMgr) != null && amMap.get(accountMgr).size() > 0) {
					List<BasicDynaBean> flagChanges = amMap.get(accountMgr);
					sendFlagChangesEmail(accountMgr, flagChanges);
				}
			}
		}
	}

	private void sendFlagChangesEmail(String accountMgr, List<BasicDynaBean> flagChanges) throws IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(55);
		emailBuilder.setFromAddress("\"PICS System\"<info@picsauditing.com>");
		emailBuilder.addToken("changes", flagChanges);
		int totalFlagChanges = sumFlagChanges(flagChanges);
		emailBuilder.addToken("totalFlagChanges", totalFlagChanges);
		emailBuilder.setToAddresses(accountMgr);
		EmailQueue email = emailBuilder.build();
		email.setVeryHighPriority();
		email.setViewableById(Account.PicsID);
		emailQueueDAO.save(email);
		emailBuilder.clear();
	}

	private int sumFlagChanges(List<BasicDynaBean> flagChanges) {
		int totalChanges = 0;
		if (CollectionUtils.isEmpty(flagChanges)) {
			return totalChanges;
		}
		
		for (BasicDynaBean flagChangesByOperator : flagChanges) {
			try {
				Object operatorFlagChanges = flagChangesByOperator.get("changes");
				if (operatorFlagChanges != null) {
					totalChanges += NumberUtils.toInt(operatorFlagChanges.toString(), 0);
				}				
			} catch (Exception ignore) {
			}
		}
		
		return totalChanges;
	}

	private Map<String, List<BasicDynaBean>> sortResultsByAccountManager(List<BasicDynaBean> data) {
		// Sorting results into buckets by AM to add as tokens into the email
		Map<String, List<BasicDynaBean>> amMap = new TreeMap<String, List<BasicDynaBean>>();
		for (BasicDynaBean bean : data) {
			String accountMgr = (String) bean.get("accountManager");
			if (accountMgr != null) {
				if (amMap.get(accountMgr) == null)
					amMap.put(accountMgr, new ArrayList<BasicDynaBean>());

				amMap.get(accountMgr).add(bean);
			}
		}
		return amMap;
	}

	private List<BasicDynaBean> getFlagChangeData() throws SQLException {
		StringBuilder query = new StringBuilder();
		query.append("select id, operator, accountManager, changes, total, round(changes * 100 / total) as percent from ( ");
		query.append("select o.id, o.name operator, concat(u.name, ' <', u.email, '>') accountManager, ");
		query.append("count(*) total, sum(case when gc.flag = gc.baselineFlag THEN 0 ELSE 1 END) changes ");
		query.append("from generalcontractors gc ");
		query.append("join accounts c on gc.subID = c.id and c.status = 'Active' ");
		query.append("join accounts o on gc.genID = o.id and o.status = 'Active' and o.type = 'Operator' and o.id not in (" + 
				Strings.implode(OperatorUtil.operatorsIdsUsedForInternalPurposes()) + ") ");
		query.append("LEFT join account_user au on au.accountID = o.id and au.role = 'PICSAccountRep' and startDate < now() ");
		query.append("and endDate > now() ");
		query.append("LEFT join users u on au.userID = u.id ");
		query.append("group by o.id) t ");
		query.append("where changes >= 10 and changes/total > .05 ");
		query.append("order by percent desc ");

		Database db = new Database();
		List<BasicDynaBean> data = db.select(query.toString(), true);
		return data;
	}

	public void clearForceFlags() {
		List<FlagDataOverride> fdos = flagDataOverrideDAO.findExpiredForceFlags();

		Iterator<FlagDataOverride> fdoIter = fdos.iterator();
		while (fdoIter.hasNext()) {
			FlagDataOverride fdo = fdoIter.next();

			// save history
			FlagOverrideHistory foh = new FlagOverrideHistory();
			foh.setOverride(fdo);
			foh.setAuditColumns(system);
			foh.setDeleted(false);
			foh.setDeleteReason("Flag Data Override Expired");
			dao.save(foh);

			// Create note & Delete override
			Note note = new Note(fdo.getContractor(), system, "Forced " + fdo.getCriteria().getLabel() + " Flag to "
					+ fdo.getForceflag() + " Expired for " + fdo.getContractor().getName());
			note.setCanContractorView(true);
			note.setPriority(LowMedHigh.Med);
			note.setNoteCategory(NoteCategory.Flags);
			note.setAuditColumns(system);
			note.setViewableBy(fdo.getOperator());
			noteDAO.save(note);

			flagDataOverrideDAO.remove(fdo);
			fdoIter.remove();
		}

		List<ContractorOperator> overrides = contractorOperatorDAO.findExpiredForceFlags();

		Iterator<ContractorOperator> overrideIter = overrides.iterator();
		while (overrideIter.hasNext()) {
			ContractorOperator override = overrideIter.next();

			// save history
			FlagOverrideHistory foh = new FlagOverrideHistory();
			foh.setOverride(override);
			foh.setAuditColumns(permissions);
			foh.setDeleted(false);
			foh.setDeleteReason("Overall Flag Override Expired");
			dao.save(foh);

			// Create note & Remove override
			Note note = new Note(override.getContractorAccount(), system, "Overall Forced Flag to "
					+ override.getFlagColor() + " Expired for " + override.getContractorAccount().getName());
			note.setCanContractorView(true);
			note.setPriority(LowMedHigh.Med);
			note.setNoteCategory(NoteCategory.Flags);
			note.setAuditColumns(system);
			note.setViewableBy(override.getOperatorAccount());
			noteDAO.save(note);

			override.setForceEnd(null);
			override.setForceFlag(null);
			override.setForceBegin(null);
			override.setForcedBy(null);

			contractorOperatorDAO.save(override);
		}
	}

	private void checkSystemStatus() throws Exception {
		ContractorCronStatistics stats = new ContractorCronStatistics(contractorAccountDAO, emailQueueDAO);
		stats.execute();
	}

	private void checkRegistrationRequestsHoldDates() throws Exception {
		List<ContractorRegistrationRequest> holdRequests = (List<ContractorRegistrationRequest>) dao.findWhere(
				ContractorRegistrationRequest.class, "t.status = 'Hold'");
		Date now = new Date();
		for (ContractorRegistrationRequest crr : holdRequests) {
			if (now.after(crr.getHoldDate())) {
				crr.setStatus(ContractorRegistrationRequestStatus.Active);
				crr.setNotes(maskDateFormat(now) + " - System - hold date passed.  Request set to active \n\n"
						+ crr.getNotes());
			}
		}
	}
}
