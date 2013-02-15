package com.picsauditing.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorCronStatistics;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AppPropertyDAO;
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
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
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
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EventSubscriptionBuilder;
import com.picsauditing.mail.NoUsersDefinedException;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.search.Database;
import com.picsauditing.util.EbixLoader;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.OperatorUtil;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class Cron extends PicsActionSupport {

	@Autowired
	protected AppPropertyDAO appPropDao;
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
	private EmailSender emailSender;
	@Autowired
	private IndexerEngine indexer;
	@Autowired
	private EmailBuilder emailBuilder;
	@Autowired
	private AccountStatusChanges accountStatusChanges;

	protected final static User system = new User(User.SYSTEM);

	protected long startTime = 0L;
	StringBuffer report = null;

	protected boolean flagsOnly = false;

	private List<String> emailExclusionList = new ArrayList<String>();

//	private int possibleDuplciateEmailTemplate = 234;
//	private int finalToOperatorsEmailTemplate = 240;
//	private int regReqFinalEmailTemplate = 241;
//	private int regReqLastChanceEmailTemplate = 242;
//	private int regReqReminderEmailTemplate = 243;
//	private int pendingFinalEmailTemplate = 244;
//	private int pendingLastChanceEmailTemplate = 245;
//	private int pendingReminderEmailTemplate = 246;

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
			startTask("Bump Dead Accounts that still have balances...");
			contractorAccountDAO.updateRecalculationForDeadAccountsWithBalances();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		/*
		 * Do not change the payment expires when deactivating accounts.
		 *
		 * This is actually Canceling an account, not a deactivation.
		 */
		try {
			startTask("Inactivating Accounts via Billing Status...");
			deactivateNonRenewalAccounts();
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

		try {
			startTask("deactivating pending accounts pass 90 days");
			deactivatePendingAccounts();
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		report.append(Strings.NEW_LINE).append(Strings.NEW_LINE).append(Strings.NEW_LINE)
				.append("Completed Cron Job at: ");
		report.append(new Date().toString());

		sendEmail();

		output = "Complete";

		return SUCCESS;
	}

	private void deactivateNonRenewalAccounts() {
		String where = "a.status = 'Active' AND a.renew = 0 AND paymentExpires < NOW()";
		List<ContractorAccount> conAcctList = contractorAccountDAO.findWhere(where);
		for (ContractorAccount contractor : conAcctList) {
			if (contractor.getAccountLevel().isBidOnly()) {
				contractor.setReason("Bid Only Account");
			}

			contractor.syncBalance();
			contractor.setAuditColumns(system);
			accountStatusChanges.deactivateContractor(contractor, permissions,
					"Automatically inactivating account based on expired membership");
		}
	}

	private void getEmailExclusions() {
		List<String> exclusionList = emailQueueDAO.findEmailAddressExclusions();
		if (CollectionUtils.isNotEmpty(exclusionList)) {
			emailExclusionList.addAll(exclusionList);
		}
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
			toAddress = EmailAddressUtils.PICS_ADMIN_EMAIL;
		}

		try {
			emailSender.send(toAddress, "Cron job report", report.toString());
			logger.error(report.toString());
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

		String where = "a.country IN ('US','CA') AND (c.lastContactedByAutomatedEmailDate != CURDATE() OR c.lastContactedByAutomatedEmailDate IS NULL) AND ";

		if (!emailExclusionList.isEmpty()) {
			where = "u.email NOT IN (" + exclude + ") AND " + where;
		}

		String whereReminder = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
		String whereLastChance = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 WEEK)";
		String whereFinal = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 1 MONTH)";

		String activationReminderNote = "Sent Activation Reminder Email to ";
		String activationLastReminderNote = "Sent Activation Last Chance Reminder Email to ";
		String deactivationNote = "Final email sent to Contractor and Client Site. Notification Email sent to ";

		List<ContractorAccount> pendingReminder = contractorAccountDAO.findPendingAccounts(whereReminder);
		runAccountEmailBlast(pendingReminder, EmailTemplate.pendingReminderEmailTemplate, activationReminderNote);

		List<ContractorAccount> pendingLastChance = contractorAccountDAO.findPendingAccounts(whereLastChance);
		runAccountEmailBlast(pendingLastChance, EmailTemplate.pendingLastChanceEmailTemplate, activationLastReminderNote);

		List<ContractorAccount> pendingFinal = contractorAccountDAO.findPendingAccounts(whereFinal);
		runAccountEmailBlast(pendingFinal, EmailTemplate.pendingFinalEmailTemplate, deactivationNote);
	}

	private void runAccountEmailBlast(List<ContractorAccount> list, int templateID, String newNote)
			throws EmailException, IOException, ParseException {
		Map<OperatorAccount, List<ContractorAccount>> operatorContractors = new HashMap<OperatorAccount, List<ContractorAccount>>();

		for (ContractorAccount contractor : list) {
			if (contractor.getPrimaryContact() != null
					&& !emailExclusionList.contains(contractor.getPrimaryContact().getEmail())) {

				if (templateID != EmailTemplate.pendingFinalEmailTemplate || !duplicationCheck(contractor, contractor.getNameIndex())) {
					OperatorAccount requestedByOperator = contractor.getRequestedBy();

					EmailBuilder emailBuilder = new EmailBuilder();
					emailBuilder.setFromAddress(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
					emailBuilder.setPermissions(permissions);
					emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
					emailBuilder.setTemplate(templateID);
					emailExclusionList.add(contractor.getPrimaryContact().getEmail());

					if (requestedByOperator == null) {
						requestedByOperator = new OperatorAccount();
						requestedByOperator.setName("the PICS Client Site that requested you join");
					}

					emailBuilder.addToken("operator", requestedByOperator);

					Calendar cal = Calendar.getInstance();
					cal.setTime(contractor.getCreationDate());
					cal.add(Calendar.MONTH, 1);
					emailBuilder.addToken("date", cal.getTime());

					EmailQueue email = emailBuilder.build();
					email.setViewableById(Account.EVERYONE);
					emailQueueDAO.save(email);

					// update the contractor notes
					stampNote(contractor, newNote + emailBuilder.getSentTo(), NoteCategory.Registration);
					if (templateID == EmailTemplate.pendingFinalEmailTemplate) {
						if (operatorContractors.get(requestedByOperator) == null) {
							operatorContractors.put(requestedByOperator, new ArrayList<ContractorAccount>());
						}

						operatorContractors.get(requestedByOperator).add(contractor);
					}
					SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.American);
					contractor.setLastContactedByAutomatedEmailDate(sdf.parse(sdf.format(new Date())));
					contractorAccountDAO.save(contractor);
				}
			}
		}

		// send emails out to all client sites whose contractors these were for
		for (OperatorAccount operator : operatorContractors.keySet()) {
			List<ContractorAccount> contractors = operatorContractors.get(operator);

			if (operator != null && operator.getPrimaryContact() != null
					&& !emailExclusionList.contains(operator.getPrimaryContact().getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.setFromAddress(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
				emailBuilder.setPermissions(permissions);
				emailBuilder.setToAddresses(operator.getPrimaryContact().getEmail());
				emailExclusionList.add(operator.getPrimaryContact().getEmail());

				emailBuilder.addToken("user", operator.getPrimaryContact());
				emailBuilder.addToken("contractors", contractors);
				emailBuilder.setTemplate(EmailTemplate.FINAL_TO_OPERATORS_EMAIL_TEMPLATE);

				EmailQueue email = emailBuilder.build();
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				// update the notes
				stampNote(operator, "Contractor Pending Account expired. Client site was notified at this address: "
						+ emailBuilder.getSentTo(), NoteCategory.Registration);
			}
		}
	}

	private void sendEmailContractorRegistrationRequest() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.American);

		int[] pendingEmailTemplates = { EmailTemplate.pendingFinalEmailTemplate, EmailTemplate.pendingLastChanceEmailTemplate,
				EmailTemplate.pendingReminderEmailTemplate };

		List<String> emailsAlreadySentToPending = emailQueueDAO.findPendingActivationEmails("1 MONTH",
				pendingEmailTemplates);
		emailExclusionList.addAll(emailsAlreadySentToPending);

		String excludedEmails = Strings.implodeForDB(emailExclusionList, ",");
		String where = "c.country IN ('US','CA') AND c.conID IS NULL AND (c.lastContactedByAutomatedEmailDate != CURDATE() OR c.lastContactedByAutomatedEmailDate IS NULL) AND ";

		if (!emailExclusionList.isEmpty()) {
			where = "c.email NOT IN ("
					+ excludedEmails
					+ ") AND (c.lastContactedByAutomatedEmailDate != CURDATE() OR c.lastContactedByAutomatedEmailDate IS NULL) AND ";
		}

		// 3 days after the request is created send a reminder email
		String whereReminder = where + "DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
		// If the deadline is within 14 days of the creation date
		// Send out the last chance email a week after creation
		// Otherwise send out the last chance email a week before the deadline
		String whereLastChance = where + "CASE WHEN DATEDIFF(c.deadline, c.creationDate) < 14 "
				+ "THEN DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 7 DAY) "
				+ "ELSE CURDATE() = DATE_SUB(c.deadline,INTERVAL 7 DAY) END";
		// If the deadline is within 14 days of the creation date
		// Send out the final email 2 weeks after creation
		// Otherwise send out the last chance email on the deadline
		String whereFinal = where + "CASE WHEN DATEDIFF(c.deadline, c.creationDate) < 14 "
				+ "THEN DATE(c.creationDate) = DATE_SUB(CURDATE(),INTERVAL 14 DAY) "
				+ "ELSE CURDATE() = DATE(c.deadline) END";

		String currentDate = sdf.format(new Date());
		String reminderNote = currentDate + " - Email has been sent to remind contractor to register.\n\n";
		String lastChanceNote = currentDate
				+ " - Email has been sent to contractor warning them that this is their last chance to register.\n\n";
		String finalAndExpirationNote = currentDate + " - Final email sent to Contractor and Client Site.\n\n";
		// Legacy
		List<ContractorRegistrationRequest> crrLegacyListReminder = contractorRegistrationRequestDAO
				.findLegacyActiveByDate(whereReminder.replace("primaryContact.", ""));
		runCRREmailBlast(crrLegacyListReminder, EmailTemplate.regReqReminderEmailTemplate, reminderNote);

		List<ContractorRegistrationRequest> crrLegacyListLastChance = contractorRegistrationRequestDAO
				.findLegacyActiveByDate(whereLastChance.replace("primaryContact.", ""));
		runCRREmailBlast(crrLegacyListLastChance, EmailTemplate.regReqLastChanceEmailTemplate, lastChanceNote);

		List<ContractorRegistrationRequest> crrLegacyListFinal = contractorRegistrationRequestDAO
				.findLegacyActiveByDate(whereFinal.replace("primaryContact.", ""));
		runCRREmailBlast(crrLegacyListFinal, EmailTemplate.REGISTRATION_REQUEST_FINAL_EMAIL_TEMPLATE, finalAndExpirationNote);

		// New system
		where = "c.country.isoCode IN ('US','CA') AND (c.lastContactedByAutomatedEmailDate != current_date() "
				+ "OR c.lastContactedByAutomatedEmailDate IS NULL) AND ";
		if (!emailExclusionList.isEmpty()) {
			where += "c.primaryContact.email NOT IN (" + excludedEmails + ") AND ";
		}

		// We don't have deadlines (operator specific) so go off of creation
		// dates
		whereReminder = where + "c.creationDate = ?";
		whereLastChance = where + "current_date() = ?";
		whereFinal = where + "current_date() = ?";

		Date now = new Date();
		Date threeDays = DateBean.addDays(now, -3);
		List<ContractorAccount> crrListReminder = contractorRegistrationRequestDAO.findActiveByDate(whereReminder,
				threeDays);
		runCRREmailBlast(crrListReminder, EmailTemplate.regReqReminderEmailTemplate, reminderNote);

		Date oneWeek = DateBean.addDays(now, -7);
		List<ContractorAccount> crrListLastChance = contractorRegistrationRequestDAO.findActiveByDate(whereLastChance,
				oneWeek);
		runCRREmailBlast(crrListLastChance, EmailTemplate.regReqLastChanceEmailTemplate, lastChanceNote);

		Date twoWeeks = DateBean.addDays(now, -14);
		List<ContractorAccount> crrListFinal = contractorRegistrationRequestDAO.findActiveByDate(whereFinal, twoWeeks);
		runCRREmailBlast(crrListFinal, EmailTemplate.REGISTRATION_REQUEST_FINAL_EMAIL_TEMPLATE, finalAndExpirationNote);
	}

	private void runCRREmailBlast(List<? extends BaseTable> list, int templateID, String newNote) throws IOException,
			ParseException {
		Map<User, List<BaseTable>> operatorContractors = new HashMap<User, List<BaseTable>>();

		for (BaseTable requestedContractor : list) {
			String requestName = Strings.EMPTY_STRING;
			String emailAddress = Strings.EMPTY_STRING;
			OperatorAccount requestedBy = null;
			Date deadline = null;
			User requestedByUser = null;
			String requestedByOther = null;

			if (requestedContractor instanceof ContractorAccount) {
				ContractorAccount request = (ContractorAccount) requestedContractor;
				ContractorOperator contractorOperator = request.getContractorOperatorWithClosestDeadline();

				requestName = request.getName();
				emailAddress = request.getPrimaryContact().getEmail();

				if (contractorOperator != null) {
					requestedBy = contractorOperator.getOperatorAccount();
				} else if (request.getRequestedBy() != null) {
					requestedBy = request.getRequestedBy();
					contractorOperator = request.getContractorOperatorForOperator(requestedBy);
				}

				if (contractorOperator != null) {
					deadline = contractorOperator.getDeadline();
					requestedByUser = contractorOperator.getRequestedBy();
					requestedByOther = contractorOperator.getRequestedByOther();
				}
			} else if (requestedContractor instanceof ContractorRegistrationRequest) {
				ContractorRegistrationRequest request = (ContractorRegistrationRequest) requestedContractor;
				requestName = request.getName();
				emailAddress = request.getEmail();
				requestedBy = request.getRequestedBy();
				deadline = request.getDeadline();
				requestedByUser = request.getRequestedByUser();
				requestedByOther = request.getRequestedByUserOther();
			}

			if (!Strings.isEmpty(emailAddress) && !emailExclusionList.contains(emailAddress)) {
				if (templateID != EmailTemplate.pendingFinalEmailTemplate || !duplicationCheck(requestedContractor, requestName)) {
					EmailBuilder emailBuilder = new EmailBuilder();

					emailBuilder.setFromAddress(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
					emailBuilder.setToAddresses(emailAddress);
					emailBuilder.setTemplate(templateID);
					emailExclusionList.add(emailAddress);

					emailBuilder.addToken("operator", requestedBy);
					emailBuilder.addToken("contractor", requestedContractor);

					Calendar cal = Calendar.getInstance();
					Date shortDeadline = DateUtils.addDays(requestedContractor.getCreationDate(), 14);

					if (deadline.before(shortDeadline)) {
						cal.setTime(shortDeadline);
					} else {
						cal.setTime(deadline);
					}
					emailBuilder.addToken("date", cal.getTime());

					if (requestedByUser == null) {
						requestedByUser = new User(requestedByOther);
					}

					EmailQueue email = emailBuilder.build();
					email.setViewableById(Account.EVERYONE);
					emailQueueDAO.save(email);

					// update the registration request
					if (requestedContractor instanceof ContractorAccount) {
						ContractorAccount request = (ContractorAccount) requestedContractor;

						request.contactByEmail();

						Note note = new Note(request, system, newNote);
						note.setNoteCategory(NoteCategory.Registration);
						note.setCanContractorView(true);
						note.setViewableBy(operatorDAO.find(Account.PicsID));
						noteDAO.save(note);

						request.setLastContactedByAutomatedEmailDate(new Date());
					} else if (requestedContractor instanceof ContractorRegistrationRequest) {
						ContractorRegistrationRequest request = (ContractorRegistrationRequest) requestedContractor;

						request.contactByEmail();
						request.addToNotes(newNote, system);
						request.setLastContactedByAutomatedEmailDate(new Date());
					}

					if (templateID == EmailTemplate.REGISTRATION_REQUEST_FINAL_EMAIL_TEMPLATE) {
						if (operatorContractors.get(requestedByUser) == null) {
							operatorContractors.put(requestedByUser, new ArrayList<BaseTable>());
						}

						operatorContractors.get(requestedByUser).add(requestedContractor);
					}

					contractorRegistrationRequestDAO.save(requestedContractor);
				}
			}
		}

		for (User operatorUser : operatorContractors.keySet()) {
			List<BaseTable> contractors = operatorContractors.get(operatorUser);

			if (operatorUser != null && operatorUser.getEmail() != null
					&& !emailExclusionList.contains(operatorUser.getEmail())) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.setFromAddress(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
				emailBuilder.setToAddresses(operatorUser.getEmail());
				emailExclusionList.add(operatorUser.getEmail());

				emailBuilder.addToken("user", operatorUser);
				emailBuilder.addToken("contractors", contractors);
				emailBuilder.setTemplate(EmailTemplate.FINAL_TO_OPERATORS_EMAIL_TEMPLATE);

				EmailQueue email = emailBuilder.build();
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				stampNote(
						operatorUser.getAccount(),
						"Registration Request has expired. Client site was notified at this address: "
								+ emailBuilder.getSentTo(), NoteCategory.Registration);
			}
		}
	}

	private boolean duplicationCheck(Object contractor, String nameIndex) throws IOException {
		List<ContractorAccount> duplicateContractors = contractorAccountDAO
				.findWhere(whereDuplicateNameIndex(nameIndex));

		if (CollectionUtils.isNotEmpty(duplicateContractors)) {

			EmailBuilder emailBuilder = new EmailBuilder();

			emailBuilder.setFromAddress(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS);
			emailBuilder.setToAddresses(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
			emailBuilder.addToken("contractor", contractor);
			emailBuilder.addToken("duplicates", duplicateContractors);
			emailBuilder.addToken("type", "Pending Account");
			emailBuilder.setTemplate(EmailTemplate.POSSIBLE_DUPLICATE_EMAIL_TEMPLATE);

			EmailQueue email = emailBuilder.build();
			email.setLowPriority();
			email.setViewableById(Account.EVERYONE);
			emailQueueDAO.save(email);
			return true;
		}
		return false;
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

	private void sendUpcomingImplementationAuditEmail() throws NoUsersDefinedException, IOException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);

		List<ContractorAudit> caList = contractorAuditDAO.findScheduledAuditsByAuditId(AuditType.OFFICE,
				DateBean.setToStartOfDay(cal.getTime()), DateBean.setToEndOfDay(cal.getTime()));
		for (ContractorAudit ca : caList) {
			EventSubscriptionBuilder.notifyUpcomingImplementationAudit(ca);
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
		List<Invoice> pendingAndDelienquentInvoices = contractorAccountDAO.findPendingDelinquentAndDelinquentInvoices();
		if (!pendingAndDelienquentInvoices.isEmpty()) {
			Map<ContractorAccount, Integer> pendingAndDelinquentAccts = splitPendingAndDeliquentInvoices(pendingAndDelienquentInvoices);
			sendEmailsTo(pendingAndDelinquentAccts);
		}
	}

	private Map<ContractorAccount, Integer> splitPendingAndDeliquentInvoices(List<Invoice> invoices) {
		Map<ContractorAccount, Integer> contractors = new TreeMap<ContractorAccount, Integer>();

		for (Invoice invoice : invoices) {
			ContractorAccount cAccount = (ContractorAccount) invoice.getAccount();
			if (invoice.getDueDate().before(new Date())) {
				contractors.put(cAccount, 48); // deactivation
			} else {
				contractors.put(cAccount, 50); // open
			}
		}

		return contractors;
	}

	private void sendEmailsTo(Map<ContractorAccount, Integer> pendingAndDelinquentAccts) {
		for (ContractorAccount cAccount : pendingAndDelinquentAccts.keySet()) {
			try {
				int templateID = pendingAndDelinquentAccts.get(cAccount);

				emailBuilder.clear();
				emailBuilder.setContractor(cAccount, OpPerms.ContractorBilling);
				emailBuilder.setTemplate(templateID);

				EmailQueue email = emailBuilder.build();
				email.setLowPriority();
				email.setViewableById(Account.PicsID);
				emailQueueDAO.save(email);
				stampNote(email.getContractorAccount(), "Deactivation Email Sent to " + email.getToAddresses(),
						NoteCategory.Billing);
			} catch (Exception e) {
				sendInvalidEmailsToBilling(cAccount);
			}
		}
	}

	private void sendInvalidEmailsToBilling(ContractorAccount cAccount) {
		EmailQueue email = new EmailQueue();
		email.setToAddresses(EmailAddressUtils.getBillingEmail(cAccount.getCurrency()));
		email.setContractorAccount(cAccount);
		email.setSubject("Contractor Missing Email Address");
		email.setBody(cAccount.getName() + " (" + cAccount.getId() + ") has no valid email address. "
				+ "The system is unable to send automated emails to this account. "
				+ "Attempted to send Overdue Invoice Email Reminder.");
		email.setLowPriority();
		email.setViewableById(Account.PicsID);
		emailQueueDAO.save(email);
		stampNote(email.getContractorAccount(), "Failed to send Deactivation Email because of no valid email address.",
				NoteCategory.Billing);
	}

	public void addLateFeeToDelinquentInvoices() throws Exception {
		// Get delinquent Invoices missing Late Fees
		List<Invoice> invoicesMissingLateFees = invoiceDAO.findDelinquentInvoicesMissingLateFees();

		for (Invoice i : invoicesMissingLateFees) {
			boolean hasReactivation = false;

			// Skip Reactivations
			for (InvoiceItem ii : i.getItems()) {
				if (ii.getInvoiceFee().isReactivation()) {
					hasReactivation = true;
				}
			}

			if (!hasReactivation) {
				// Calculate Late Fee
				BigDecimal lateFee = i.getTotalAmount().multiply(BigDecimal.valueOf(0.05))
						.setScale(0, BigDecimal.ROUND_HALF_UP);
				if (lateFee.compareTo(BigDecimal.valueOf(20)) < 1) {
					lateFee = BigDecimal.valueOf(20);
				}

				InvoiceFee fee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.LateFee,
						((ContractorAccount) i.getAccount()).getPayingFacilities());
				InvoiceItem lateFeeItem = new InvoiceItem(fee);
				lateFeeItem.setAmount(lateFee);
				lateFeeItem.setAuditColumns(new User(User.SYSTEM));
				lateFeeItem.setInvoice(i);
				lateFeeItem.setDescription("Assessed "
						+ new SimpleDateFormat(PicsDateFormat.American).format(new Date())
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
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
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
		if (CollectionUtils.isEmpty(data)) {
			return;
		}

		sendFlagChangesEmail(EmailAddressUtils.PICS_FLAG_CHANGE_EMAIL, data);

		Map<String, List<BasicDynaBean>> amMap = sortResultsByAccountManager(data);
		if (MapUtils.isNotEmpty(amMap)) {
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
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_SYSTEM_EMAIL_ADDRESS);
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

		if (CollectionUtils.isEmpty(data)) {
			return amMap;
		}

		for (BasicDynaBean bean : data) {
			String accountMgr = (String) bean.get("accountManager");
			if (accountMgr != null) {
				if (amMap.get(accountMgr) == null) {
					amMap.put(accountMgr, new ArrayList<BasicDynaBean>());
				}

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
		query.append("join accounts o on gc.genID = o.id and o.status = 'Active' and o.type = 'Operator' and o.id not in ("
				+ Strings.implode(OperatorUtil.operatorsIdsUsedForInternalPurposes()) + ") ");
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
		List<ContractorRegistrationRequest> holdRequests = dao.findWhere(ContractorRegistrationRequest.class,
				"t.status = 'Hold'");
		Date now = new Date();
		for (ContractorRegistrationRequest crr : holdRequests) {
			if (now.after(crr.getHoldDate())) {
				crr.setStatus(ContractorRegistrationRequestStatus.Active);
				crr.setNotes(maskDateFormat(now) + " - System - hold date passed.  Request set to active \n\n"
						+ crr.getNotes());
			}
		}
	}

	private void deactivatePendingAccounts() {
		List<ContractorAccount> deactivateList = contractorAccountDAO.findPendingAccountsToDeactivate();
		if (CollectionUtils.isEmpty(deactivateList)) {
			return;
		}

		for (ContractorAccount contractor : deactivateList) {
			accountStatusChanges.deactivateContractor(contractor, permissions,
					"Account has been deactivated, this account has been pending for 90 days without payment.");
		}
	}
}
