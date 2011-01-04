package com.picsauditing.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Database;
import com.picsauditing.util.EbixLoader;
import com.picsauditing.util.IndexerController;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class Cron extends PicsActionSupport {

	static protected User system = new User(User.SYSTEM);
	protected OperatorAccountDAO operatorDAO = null;
	protected AppPropertyDAO appPropDao = null;
	protected AuditBuilderController auditBuilder = null;
	protected ContractorAuditDAO contractorAuditDAO = null;
	protected ContractorAccountDAO contractorAccountDAO = null;
	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO = null;
	protected NoteDAO noteDAO = null;
	protected InvoiceDAO invoiceDAO = null;
	protected AuditPercentCalculator auditPercentCalculator;
	private EbixLoader ebixLoader;
	private IndexerController indexer;
	private InvoiceFeeDAO invoiceFeeDAO;
	private InvoiceItemDAO invoiceItemDAO;

	protected long startTime = 0L;
	StringBuffer report = null;

	protected boolean flagsOnly = false;

	public Cron(OperatorAccountDAO ops, AppPropertyDAO appProps, AuditBuilderController ab,
			ContractorAuditDAO contractorAuditDAO, ContractorAccountDAO contractorAccountDAO,
			AuditPercentCalculator auditPercentCalculator, NoteDAO noteDAO, InvoiceDAO invoiceDAO,
			EbixLoader ebixLoader, ContractorAuditOperatorDAO contractorAuditOperatorDAO, IndexerController indexer,
			InvoiceFeeDAO invoiceFeeDAO, InvoiceItemDAO invoiceItemDAO) {
		this.operatorDAO = ops;
		this.appPropDao = appProps;
		this.auditBuilder = ab;
		this.contractorAuditDAO = contractorAuditDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.noteDAO = noteDAO;
		this.invoiceDAO = invoiceDAO;
		this.ebixLoader = ebixLoader;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
		this.indexer = indexer;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.invoiceItemDAO = invoiceItemDAO;
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
				 * OPTIMIZE TABLE
				 * OSHA,accounts,auditCategories,auditData,auditQuestions
				 * ,certificates,contractor_info," +
				 * "forms,generalContractors,loginLog,users;
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
				startTask("\nSending emails to contractors for expired Certificates...");
				sendEmailExpiredCertificates();
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
				contractor.setStatus(AccountStatus.Deactivated);
				// Setting a deactivation reason
				if (contractor.isAcceptsBids()) {
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
			startTask("\nDeleting Expired Individual Data Overrides...");
			contractorAuditDAO.deleteData(FlagDataOverride.class, "forceEnd < NOW()");
			endTask();
		} catch (Throwable t) {
			handleException(t);
		}

		try {
			startTask("\nEmailing Flag Changes Report to Account Managers...");
			sendFlagChangesEmailToAccountManagers();
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
		EmailBuilder emailBuilder = new EmailBuilder();
		Set<ContractorAccount> policies = new HashSet<ContractorAccount>();

		for (ContractorAudit cAudit : cList) {
			if (cAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
					&& cAudit.getCurrentOperators().size() > 0)
				policies.add(cAudit.getContractorAccount());
		}
		for (ContractorAccount policy : policies) {
			emailBuilder.clear();
			emailBuilder.setTemplate(10); // Certificate Expiration
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(policy, OpPerms.ContractorInsurance);
			emailBuilder.addToken("policies", policy);
			EmailQueue email = emailBuilder.build();
			email.setPriority(30);
			email.setViewableById(Account.EVERYONE);
			emailQueueDAO.save(email);

			stampNote(policy, "Sent Policy Expiration Email to " + emailBuilder.getSentTo(), NoteCategory.Insurance);
		}
	}

	public void runIndexer() throws Exception {
		PicsLogger.start("");
		indexer.runAll(null, true);
		PicsLogger.stop();
	}

	public void processEbixData() throws Exception {
		PicsLogger.start("cron_ebix");
		ebixLoader.load();
		PicsLogger.stop();
	}

	public void stampNote(ContractorAccount cAccount, String text, NoteCategory noteCategory) {
		Note note = new Note(cAccount, system, text);
		note.setCanContractorView(true);
		note.setPriority(LowMedHigh.High);
		note.setNoteCategory(noteCategory);
		note.setAuditColumns(system);
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}

	public void sendDelinquentContractorsEmail() throws Exception {
		List<Invoice> invoices = contractorAccountDAO.findDelinquentContractors();
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
		AuditDataDAO auditDataDAO = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
		EmailBuilder emailBuilder = new EmailBuilder();
		Map<ContractorAccount, Set<String>> cMap = new TreeMap<ContractorAccount, Set<String>>();
		Map<ContractorAccount, Integer> templateMap = new TreeMap<ContractorAccount, Integer>();
		List<Integer> questions = Arrays.<Integer> asList(604, 606, 624, 627, 630, 1437);

		for (Invoice invoice : invoices) {
			Set<String> emailAddresses = new HashSet<String>();
			ContractorAccount cAccount = (ContractorAccount) invoice.getAccount();

			User billing = cAccount.getUsersByRole(OpPerms.ContractorBilling).get(0);
			if (!Strings.isEmpty(billing.getEmail()))
				emailAddresses.add(billing.getEmail());
			if (!Strings.isEmpty(cAccount.getCcEmail()))
				emailAddresses.add(cAccount.getCcEmail());

			if (DateBean.getDateDifference(invoice.getDueDate()) < -10) {
				List<AuditData> aList = auditDataDAO.findAnswerByConQuestions(cAccount.getId(), questions);
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
			emailBuilder.clear();
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
			// Calculate Late Fee
			BigDecimal lateFee = i.getTotalAmount().multiply(BigDecimal.valueOf(0.05)).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			if (lateFee.compareTo(BigDecimal.valueOf(25)) < 1)
				lateFee = BigDecimal.valueOf(25);

			InvoiceItem lateFeeItem = new InvoiceItem(invoiceFeeDAO.find(InvoiceFee.LATEFEE));
			lateFeeItem.setAmount(lateFee);
			lateFeeItem.setAuditColumns(new User(User.SYSTEM));
			lateFeeItem.setInvoice(i);
			lateFeeItem.setDescription("Assessed " + new SimpleDateFormat("MM/dd/yyyy").format(new Date())
					+ " due to deliquent payment.");
			invoiceItemDAO.save(lateFeeItem);

			// Add Late Fee to Invoice
			i.getItems().add(lateFeeItem);
			i.updateAmount();
			i.setQbSync(true);
			i.setAuditColumns(new User(User.SYSTEM));
			if (i.getAccount() instanceof ContractorAccount)
				((ContractorAccount) i.getAccount()).syncBalance();
			invoiceItemDAO.save(i);
		}
	}

	public void sendNoActionEmailToTrialAccounts() throws Exception {
		List<ContractorAccount> conList = contractorAccountDAO.findBidOnlyContractors();
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
		EmailBuilder emailBuilder = new EmailBuilder();

		for (ContractorAccount cAccount : conList) {
			emailBuilder.clear();
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

	public void sendFlagChangesEmailToAccountManagers() throws Exception {
		// Running Query
		StringBuilder query = new StringBuilder();
		query
				.append("select id, operator, accountManager, changes, total, round(changes * 100 / total) as percent from ( ");
		query.append("select o.id, o.name operator, concat(u.name, ' <', u.email, '>') accountManager, ");
		query.append("count(*) total, sum(case when gc.flag = gc.baselineFlag THEN 0 ELSE 1 END) changes ");
		query.append("from generalcontractors gc ");
		query.append("join accounts c on gc.subID = c.id and c.status = 'Active' ");
		query.append("join accounts o on gc.genID = o.id and o.status = 'Active' and o.type = 'Operator' ");
		query
				.append("LEFT join account_user au on au.accountID = o.id and au.role = 'PICSAccountRep' and startDate < now() ");
		query.append("and endDate > now() ");
		query.append("LEFT join users u on au.userID = u.id ");
		query.append("group by o.id) t ");
		query.append("where changes > 1 and changes/total > .05 ");
		query.append("order by accountManager, percent desc ");

		Database db = new Database();
		List<BasicDynaBean> data = db.select(query.toString(), true);

		// Sorting results into buckets by AM to add as tokens into the email
		Map<String, List<DynaBean>> amMap = new HashMap<String, List<DynaBean>>();
		for (DynaBean bean : data) {
			String accountMgr = (String) bean.get("accountManager");
			if (amMap.get(accountMgr) == null)
				amMap.put(accountMgr, new ArrayList<DynaBean>());
			amMap.get(accountMgr).add(bean);
		}

		// Adding AM specific tokens to email and sending to AM
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");

		for (String accountMgr : amMap.keySet()) {
			if (amMap.get(accountMgr) != null && amMap.get(accountMgr).size() > 0) {
				EmailBuilder emailBuilder = new EmailBuilder();

				emailBuilder.addToken("changes", amMap.get(accountMgr));
				emailBuilder.setTemplate(55);
				emailBuilder.setFromAddress("\"PICS System\"<info@picsauditing.com>");
				emailBuilder.setToAddresses((accountMgr == null) ? "dtruitt@picsauditing.com" : accountMgr);
				EmailQueue email = emailBuilder.build();
				email.setPriority(30);
				email.setViewableById(Account.PicsID);
				emailQueueDAO.save(email);
			}
		}
	}
}
