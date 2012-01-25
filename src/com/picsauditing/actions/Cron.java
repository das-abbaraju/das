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

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.FlagDataOverride;
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
import com.picsauditing.search.Database;
import com.picsauditing.util.EbixLoader;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.Strings;
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

	@Anonymous
	public String execute() throws Exception {

		report = new StringBuffer();

		report.append("Starting Cron Job at: ");
		report.append(new Date().toString());
		report.append("\n\n");

		if (!flagsOnly) {

			startTask("\nRunning auditBuilder.addAuditRenewals...");
			List<ContractorAccount> contractors = contractorAuditDAO.findContractorsWithExpiringAudits();
			for (ContractorAccount contractor : contractors) {
				try {
					auditBuilder.buildAudits(contractor);
					contractorAuditDAO.save(contractor);
				} catch (Exception e) {
					System.out.println("ERROR!! AuditBuiler.addAuditRenewals() " + e.getMessage());
				}
			}
			endTask();

			try {
				// TODO - Move this to the db.picsauditing.com cron bash script
				/*
				 * OPTIMIZE TABLE OSHA,accounts,auditCategories,auditData,auditQuestions ,certificates,contractor_info,"
				 * + "forms,generalContractors,loginLog,users;
				 */
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("\nRunning Huntsman EBIX Support...");
				processEbixData();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				startTask("\nExpiring Audits and cao and stamping notes...");
				contractorAuditOperatorDAO.expireAudits();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
			try {
				startTask("\nAutoSubmitting/Completing cao for Manual/Implementation audits if one of the CAO is Active...");
				contractorAuditOperatorDAO.activateAuditsWithReqs();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}

			try {
				// TODO we shouldn't recacluate audits, but only categories.
				// This shouldn't be needed at all anymore
				startTask("\nRecalculating all the categories for Audits...");
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
				startTask("\nStarting Indexer");
				runIndexer();
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
		}

		try {
			startTask("\nSending emails to contractors pending and registration requests...");

			emailExclusionList = emailQueueDAO.findEmailAddressExclusions();

			sendEmailPendingAccounts();
			sendEmailContractorRegistrationRequest();

			emailExclusionList.clear();

			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nAdding Late Fee to Delinquent Contractor Invoices ...");
			addLateFeeToDelinquentInvoices();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nInactivating Accounts via Billing Status...");
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
			startTask("\nSending Email to Delinquent Contractors ...");
			sendDelinquentContractorsEmail();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nSending No Action Email to Bid Only Accounts ...");
			sendNoActionEmailToTrialAccounts();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}
		try {
			startTask("\nStamping Notes and Expiring overall Forced Flags and Individual Data Overrides...");
			clearForceFlags();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nExpiring Flag Changes");
			expireFlagChanges();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nEmailing Flag Change Reports...");
			sendFlagChangesEmails();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nChecking System Status");
			checkSystemStatus();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}
		try {
			startTask("\nChecking Registration Requests Hold Dates");
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
			emailSender.send(toAddress, "Cron job report", report.toString());
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

	private void sendEmailPendingAccounts() throws Exception {
		String exclude = Strings.implode(emailExclusionList, "', '");
		String where = "u.email NOT IN ('" + exclude + "') AND ";
		String where1Day = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 1 DAY)";
		String where3Day = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
		String where2Week = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 2 WEEK)";
		String where1Month = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 1 MONTH)";
		String where1Month1Week = where
				+ "DATE(a.creationDate) = DATE_SUB(DATE_SUB(CURDATE(),INTERVAL 1 MONTH),INTERVAL 1 WEEK)";

		String activationReminderNote = "Sent Activation Reminder Email to ";
		String activationLastReminderNote = "Sent Activation Last Chance Reminder Email to ";
		String deactivationNote = "This account has been deactivated. Notification Email sent to ";

		// Pending accounts are reminded to finish registration at 1 day
		List<ContractorAccount> pending1Day = contractorAccountDAO.findPendingAccounts(where1Day);
		runAccountEmailBlast(pending1Day, 185, activationReminderNote);

		// Pending accounts are reminded to finish registration at 3 days
		List<ContractorAccount> pending3Day = contractorAccountDAO.findPendingAccounts(where3Day);
		runAccountEmailBlast(pending3Day, 186, activationReminderNote);

		// Pending accounts are reminded to finish registration at 2 weeks
		List<ContractorAccount> pending2Week = contractorAccountDAO.findPendingAccounts(where2Week);
		runAccountEmailBlast(pending2Week, 187, activationReminderNote);

		// Pending accounts are reminded one last time that they have a week to activate
		List<ContractorAccount> pending1Month = contractorAccountDAO.findPendingAccounts(where1Month);
		runAccountEmailBlast(pending1Month, 188, activationLastReminderNote);

		// deactivate the account
		List<ContractorAccount> pendingDeactivation = contractorAccountDAO.findPendingAccounts(where1Month1Week);
		runAccountEmailBlast(pendingDeactivation, 0, deactivationNote);
	}

	private void runAccountEmailBlast(List<ContractorAccount> list, int templateID, String newNote)
			throws EmailException, IOException {
		Map<OperatorAccount, List<ContractorAccount>> clientSiteContractors = new HashMap<OperatorAccount, List<ContractorAccount>>();

		for (ContractorAccount contractor : list) {
			if (!emailExclusionList.contains(contractor.getPrimaryContact().getEmail())) {
				OperatorAccount requestedByClientSite = contractor.getRequestedBy();

				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setFromAddress("Registrations@picsauditing.com");
				emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
				emailExclusionList.add(contractor.getPrimaryContact().getEmail());

				contractor.getPrimaryContact().setName(
						StringUtils.trimWhitespace(contractor.getPrimaryContact().getName()));

				emailBuilder.addToken("contractor", contractor);

				if (requestedByClientSite != null) {
					emailBuilder.addToken("clientSite", requestedByClientSite);
					if (templateID == 0)
						emailBuilder.setTemplate(201);
					else
						emailBuilder.setTemplate(templateID);
				} else {
					if (templateID == 0)
						emailBuilder.setTemplate(202);
					else {
						int noFacilityTemplateID = templateID + 10;
						emailBuilder.setTemplate(noFacilityTemplateID);
					}
				}

				Calendar cal = Calendar.getInstance();
				if (templateID != 0)
					cal.add(Calendar.DAY_OF_MONTH, 7);
				emailBuilder.addToken("date", cal.getTime());

				EmailQueue email = emailBuilder.build();
				email.setPriority(20);
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				// update the contractor notes
				stampNote(contractor, newNote + emailBuilder.getSentTo(), NoteCategory.Registration);
				if (templateID == 0) {
					if (clientSiteContractors.get(requestedByClientSite) == null)
						clientSiteContractors.put(requestedByClientSite, new ArrayList<ContractorAccount>());

					clientSiteContractors.get(requestedByClientSite).add(contractor);
				}
			}
		}

		// send emails out to all client sites whose contractors these were for
		for (OperatorAccount clientSite : clientSiteContractors.keySet()) {
			List<ContractorAccount> contractors = clientSiteContractors.get(clientSite);

			if (clientSite.getPrimaryContact() != null
					&& !emailExclusionList.contains(clientSite.getPrimaryContact().getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.setFromAddress("Registrations@picsauditing.com");
				emailBuilder.setToAddresses(clientSite.getPrimaryContact().getEmail());
				emailExclusionList.add(clientSite.getPrimaryContact().getEmail());

				emailBuilder.addToken("clientSite", clientSite);
				emailBuilder.addToken("user", clientSite.getPrimaryContact());
				emailBuilder.addToken("contractors", contractors);
				emailBuilder.setTemplate(203);

				EmailQueue email = emailBuilder.build();
				email.setPriority(20);
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				// update the notes
				stampNote(clientSite,
						"Contractor Pending Account expired and has been deactivated. Client site was notified at this address: "
								+ emailBuilder.getSentTo(), NoteCategory.Registration);
			}
		}
	}

	private void sendEmailContractorRegistrationRequest() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		// ignore all email accounts that have been sent a pending email within the last month
		List<String> emailsAlreadySent = emailQueueDAO.findPendingActivationEmails("1 MONTH");
		emailExclusionList.addAll(emailsAlreadySent);

		String exclude = Strings.implode(emailExclusionList, "', '");
		String where = "c.email NOT IN ('" + exclude + "') AND c.conID IS NULL AND c.creationDate > '2011-12-30' AND ";

		String where3Days = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
		String where1Week3Days = where
				+ "DATE(c.creationDate) = DATE_SUB(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 1 WEEK)";
		String where2Weeks3Days = where
				+ "DATE(c.creationDate) = DATE_SUB(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 2 WEEK)";
		String where3Weeks3Days = where
				+ "DATE(c.creationDate) = DATE_SUB(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 3 WEEK)";
		String where5Weeks3Days = where
				+ "DATE(c.creationDate) = DATE_SUB(DATE_SUB(CURDATE(),INTERVAL 3 DAY),INTERVAL 5 WEEK)";

		String reminderNote = sdf.format(new Date()) + " - Email has been sent to remind contractor to register.\n\n";
		String finalReminderNote = sdf.format(new Date())
				+ " - Email has been sent to contractor warning them that this is their last chance to register.\n\n";
		String expirationNote = sdf.format(new Date()) + " - Registration request has expired.\n\n";

		// First notification: 3 days
		List<ContractorRegistrationRequest> crrList3Days = contractorRegistrationRequestDAO
				.findActiveByDate(where3Days);
		runCRREmailBlast(crrList3Days, 211, reminderNote);

		// 1st reminder: 1 week 3 days
		List<ContractorRegistrationRequest> crrList1Week3Days = contractorRegistrationRequestDAO
				.findActiveByDate(where1Week3Days);
		runCRREmailBlast(crrList1Week3Days, 212, reminderNote);

		// 2nd reminder: 2 weeks 3 days
		List<ContractorRegistrationRequest> crrList2Weeks3Days = contractorRegistrationRequestDAO
				.findActiveByDate(where2Weeks3Days);
		runCRREmailBlast(crrList2Weeks3Days, 214, reminderNote);

		// final reminder: 3 weeks 3 days
		List<ContractorRegistrationRequest> crrList3Weeks3Days = contractorRegistrationRequestDAO
				.findActiveByDate(where3Weeks3Days);
		runCRREmailBlast(crrList3Weeks3Days, 216, finalReminderNote);

		// Closing the registration requests.
		List<ContractorRegistrationRequest> crrClosingList5Weeks3Days = contractorRegistrationRequestDAO
				.findActiveByDate(where5Weeks3Days);
		runCRREmailBlast(crrClosingList5Weeks3Days, 217, expirationNote);
	}

	private void runCRREmailBlast(List<ContractorRegistrationRequest> list, int templateID, String newNote)
			throws IOException {
		Map<User, List<ContractorRegistrationRequest>> clientSiteContractors = new HashMap<User, List<ContractorRegistrationRequest>>();

		for (ContractorRegistrationRequest crr : list) {
			if (!emailExclusionList.contains(crr.getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				if ((templateID == 212 || templateID == 214) && crr.getDeadline().before(new Date()))
					templateID++;
				emailBuilder.setTemplate(templateID);

				emailBuilder.setFromAddress("Registrations@picsauditing.com");
				emailBuilder.setToAddresses(crr.getEmail());
				emailExclusionList.add(crr.getEmail());

				crr.setName(StringUtils.trimWhitespace(crr.getName()));

				emailBuilder.addToken("contractor", crr);
				emailBuilder.addToken("clientSite", crr.getRequestedBy());

				// try to find the client site user responsible for this request
				User clientSiteUser = crr.getRequestedByUser();
				if (clientSiteUser == null)
					clientSiteUser = new User(crr.getRequestedByUserOther());

				emailBuilder.addToken("user", clientSiteUser);
				Calendar cal = Calendar.getInstance();
				if (templateID != 216) {
					cal.setTime(crr.getCreationDate());
					cal.add(Calendar.DAY_OF_MONTH, 3);
					cal.add(Calendar.WEEK_OF_YEAR, 3);
				} else {
					cal.add(Calendar.WEEK_OF_YEAR, 2);
				}
				emailBuilder.addToken("date", cal.getTime());

				EmailQueue email = emailBuilder.build();
				email.setPriority(20);
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				// update the registration request
				String notes = crr.getNotes();
				crr.contactByEmail();
				crr.setLastContactDate(new Date());
				notes = newNote + notes;
				crr.setNotes(notes);
				if (templateID == 217) {
					if (clientSiteContractors.get(clientSiteUser) == null)
						clientSiteContractors.put(clientSiteUser, new ArrayList<ContractorRegistrationRequest>());

					clientSiteContractors.get(clientSiteUser).add(crr);
				}
				contractorRegistrationRequestDAO.save(crr);
			}
		}

		// send emails out to all client sites whose contractors these were for
		for (User clientSiteUser : clientSiteContractors.keySet()) {
			List<ContractorRegistrationRequest> contractors = clientSiteContractors.get(clientSiteUser);

			if (clientSiteUser != null && clientSiteUser.getEmail() != null
					&& !emailExclusionList.contains(clientSiteUser.getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.setFromAddress("Registrations@picsauditing.com");
				emailBuilder.setToAddresses(clientSiteUser.getEmail());
				emailExclusionList.add(clientSiteUser.getEmail());

				emailBuilder.addToken("clientSite", clientSiteUser.getAccount());
				emailBuilder.addToken("user", clientSiteUser);
				emailBuilder.addToken("contractors", contractors);
				emailBuilder.setTemplate(218);

				EmailQueue email = emailBuilder.build();
				email.setPriority(20);
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				// update the notes
				stampNote(clientSiteUser.getAccount(),
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
			email.setPriority(30);
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
				BigDecimal lateFee = i.getTotalAmount().multiply(BigDecimal.valueOf(0.05)).setScale(0,
						BigDecimal.ROUND_HALF_UP);
				if (lateFee.compareTo(BigDecimal.valueOf(20)) < 1)
					lateFee = BigDecimal.valueOf(20);

				InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee, ((ContractorAccount) i
						.getAccount()).getPayingFacilities());
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
			email.setPriority(30);
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

	public void sendFlagChangesEmails() throws Exception {
		List<BasicDynaBean> data = getFlagChangeData();
		if (data.isEmpty())
			return;

		Map<String, List<BasicDynaBean>> amMap = sortResultsByAccountManager(data);
		for (String accountMgr : amMap.keySet()) {
			if (amMap.get(accountMgr) != null && amMap.get(accountMgr).size() > 0) {
				List<BasicDynaBean> flagChanges = amMap.get(accountMgr);
				sendFlagChangesEmail(accountMgr, flagChanges);
			}
		}

		sendFlagChangesEmail("flagchanges@picsauditing.com", data);
	}

	private void sendFlagChangesEmail(String accountMgr, List<BasicDynaBean> flagChanges) throws IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(55);
		emailBuilder.setFromAddress("\"PICS System\"<info@picsauditing.com>");
		emailBuilder.addToken("changes", flagChanges);
		emailBuilder.setToAddresses(accountMgr);
		EmailQueue email = emailBuilder.build();
		email.setPriority(30);
		email.setViewableById(Account.PicsID);
		emailQueueDAO.save(email);
		emailBuilder.clear();
	}

	private Map<String, List<BasicDynaBean>> sortResultsByAccountManager(List<BasicDynaBean> data) {
		// Sorting results into buckets by AM to add as tokens into the email
		Map<String, List<BasicDynaBean>> amMap = new HashMap<String, List<BasicDynaBean>>();
		for (BasicDynaBean bean : data) {
			String accountMgr = (String) bean.get("accountManager");
			if (amMap.get(accountMgr) == null)
				amMap.put(accountMgr, new ArrayList<BasicDynaBean>());
			amMap.get(accountMgr).add(bean);
		}
		return amMap;
	}

	private List<BasicDynaBean> getFlagChangeData() throws SQLException {
		StringBuilder query = new StringBuilder();
		query
				.append("select id, operator, accountManager, changes, total, round(changes * 100 / total) as percent from ( ");
		query.append("select o.id, o.name operator, concat(u.name, ' <', u.email, '>') accountManager, ");
		query.append("count(*) total, sum(case when gc.flag = gc.baselineFlag THEN 0 ELSE 1 END) changes ");
		query.append("from generalcontractors gc ");
		query.append("join accounts c on gc.subID = c.id and c.status = 'Active' ");
		query
				.append("join accounts o on gc.genID = o.id and o.status = 'Active' and o.type = 'Operator' and o.id not in (10403,2723) ");
		query
				.append("LEFT join account_user au on au.accountID = o.id and au.role = 'PICSAccountRep' and startDate < now() ");
		query.append("and endDate > now() ");
		query.append("LEFT join users u on au.userID = u.id ");
		query.append("group by o.id) t ");
		query.append("where changes >= 10 and changes/total > .05 ");
		query.append("order by accountManager, percent desc ");

		Database db = new Database();
		List<BasicDynaBean> data = db.select(query.toString(), true);
		return data;
	}

	public void clearForceFlags() {
		List<FlagDataOverride> fdos = flagDataOverrideDAO.findExpiredForceFlags();

		Iterator<FlagDataOverride> fdoIter = fdos.iterator();
		while (fdoIter.hasNext()) {
			FlagDataOverride fdo = fdoIter.next();

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

	@SuppressWarnings("unchecked")
	private void checkRegistrationRequestsHoldDates() throws Exception {
		List<ContractorRegistrationRequest> holdRequests = (List<ContractorRegistrationRequest>) dao.findWhere(
				ContractorRegistrationRequest.class, "c.status = 'Hold'");
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
