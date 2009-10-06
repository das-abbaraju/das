package com.picsauditing.actions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.NoResultException;

import org.apache.commons.net.ftp.FTPClient;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class Cron extends PicsActionSupport {

	static protected User system = new User(User.SYSTEM);
	protected OperatorAccountDAO operatorDAO = null;
	protected AppPropertyDAO appPropDao = null;
	protected AuditBuilder auditBuilder = null;
	protected ContractorAuditDAO contractorAuditDAO = null;
	protected ContractorAccountDAO contractorAccountDAO = null;
	protected NoteDAO noteDAO = null;
	protected AuditPercentCalculator auditPercentCalculator;

	protected long startTime = 0L;
	StringBuffer report = null;

	protected boolean flagsOnly = false;

	public Cron(OperatorAccountDAO ops, AppPropertyDAO appProps, AuditBuilder ab,
			ContractorAuditDAO contractorAuditDAO, ContractorAccountDAO contractorAccountDAO,
			AuditPercentCalculator auditPercentCalculator, NoteDAO noteDAO) {
		this.operatorDAO = ops;
		this.appPropDao = appProps;
		this.auditBuilder = ab;
		this.contractorAuditDAO = contractorAuditDAO;
		this.contractorAccountDAO = contractorAccountDAO;
		this.auditPercentCalculator = auditPercentCalculator;
		this.noteDAO = noteDAO;
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
				startTask("\nExpiring Audits...");
				// TODO do mass update statements rather than query for loop
				// update
				/*
				 * update contractor_audit set auditStatus = 'Expired' where
				 * auditStatus IN ('Submitted','Exempt','Active') and
				 * auditTypeID = 11 and expiresDate < NOW();
				 * 
				 * 
				 * update contractor_audit set auditStatus = 'Pending',
				 * closedDate = null, completedDate = null, expiresDate = null
				 * where auditStatus IN ('Submitted','Exempt','Active') and
				 * auditTypeID = 1 and expiresDate < NOW();
				 */
				String where = "expiresDate < NOW() AND auditStatus IN ('Submitted','Exempt','Active')";
				List<ContractorAudit> conList = contractorAuditDAO.findWhere(250, where, "expiresDate");
				for (ContractorAudit cAudit : conList) {
					if (cAudit.getAuditType().getClassType().isPqf())
						cAudit.changeStatus(AuditStatus.Pending, system);
					else
						cAudit.setAuditStatus(AuditStatus.Expired);
					contractorAuditDAO.save(cAudit);
				}
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
			try {
				// TODO - we should seriously consider removing this all
				// together and just Activating 100% verified PQFs on submission
				// This is needed because CSRs can verify a safety manual before
				// the PQF is submitted and when the contractor
				// finally submits the policy there's nothing to tell the CSR to
				// activate it
				startTask("\nActivating Pqf which are complete and verified...");
				String where = "auditStatus = 'Submitted' AND auditTypeID IN (1) AND percentComplete = 100 AND percentVerified = 100";
				List<ContractorAudit> conList = contractorAuditDAO.findWhere(10, where, "creationDate");
				for (ContractorAudit cAudit : conList) {
					cAudit.changeStatus(AuditStatus.Active, system);
					contractorAuditDAO.save(cAudit);
					stampNote(cAudit.getContractorAccount(), "Activated the " + cAudit.getAuditType().getAuditName(),
							NoteCategory.Audits);
				}
				endTask();
			} catch (Throwable t) {
				handleException(t);
			}
			try {
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
		}

		try {
			startTask("\nInactivating Accounts via Billing Status...");
			String where = "a.active = 'Y' AND a.renew = 0 AND paymentExpires < NOW()";
			List<ContractorAccount> conAcctList = contractorAccountDAO.findWhere(where);
			for (ContractorAccount contractor : conAcctList) {
				contractor.setActive('N');
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
			startTask("\nSending No Action Email to Trial Accounts ...");
			sendNoActionEmailToTrialAccounts();
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
			if (cAudit.getCurrentOperators().size() > 0)
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

			stampNote(policy, "Sent Policy Expiration Email to " + emailBuilder.getSentTo(), NoteCategory.Insurance);
		}
	}

	public void processEbixData() throws Exception {

		PicsLogger.start("cron_ebix");

		String server = appPropDao.find("huntsmansync.ftp.server").getValue();
		String username = appPropDao.find("huntsmansync.ftp.user").getValue();
		String password = appPropDao.find("huntsmansync.ftp.password").getValue();
		String folder = appPropDao.find("huntsmansync.ftp.folder").getValue();

		PicsLogger.log("Server: " + server);
		PicsLogger.log("username: " + username);
		PicsLogger.log("folder: " + folder);

		// there may be other files in that folder. we can use this to filter
		// down to the ones we want.
		// String pattern =
		// appPropDao.find("huntsmansync.ftp.filePattern").getValue();

		FTPClient ftp = new FTPClient();

		PicsLogger.log("logging in to server...");

		ftp.connect(server);
		ftp.enterLocalPassiveMode();

		ftp.login(username, password);

		ftp.changeWorkingDirectory(folder);

		String[] names = ftp.listNames();

		if (names != null) {

			for (String fileName : names) {

				PicsLogger.log("Processing file: " + fileName);

				BufferedReader reader = null;

				InputStream retrieveFileStream = ftp.retrieveFileStream(fileName);

				if (retrieveFileStream != null) {

					reader = new BufferedReader(new InputStreamReader(retrieveFileStream));

					String line = null;

					while ((line = reader.readLine()) != null) {

						if (line.length() > 0) {

							String[] data = line.split(",");
							PicsLogger.log("Processing data: " + data[0] + "/" + data[1]);

							int contractorId = 0;
							try {
								contractorId = Integer.parseInt(data[0]);
							} catch (Exception ignoreStrings) {
								// Sometimes we get ids that are strings like
								// HC00000629
							}

							if (data.length == 2 && contractorId > 0) {

								// the other field. comes in as a Y/N.
								AuditStatus status = AuditStatus.Pending;
								if (data[1].equals("Y"))
									status = AuditStatus.Active;

								try {

									ContractorAccount conAccount = contractorAccountDAO.find(contractorId);
									List<ContractorAudit> audits = contractorAuditDAO.findWhere(900, "auditType.id = "
											+ AuditType.HUNTSMAN_EBIX + " and contractorAccount.id = "
											+ conAccount.getId(), "");

									if (audits == null || audits.size() == 0) {
										PicsLogger.log("WARNING: Ebix record found for contractor "
												+ conAccount.getId() + " but no Ebix Compliance audit was found");
										continue;
									}

									for (ContractorAudit audit : audits) {
										if (status != audit.getAuditStatus()) {
											PicsLogger.log("Setting Ebix audit " + audit.getId() + " for contractor "
													+ conAccount.getId() + " to " + status.name());
											audit.setAuditStatus(status);
											contractorAuditDAO.save(audit);

											conAccount.setNeedsRecalculation(true);
											contractorAccountDAO.save(conAccount);
										} else {
											PicsLogger.log("No change for Ebix audit " + audit.getId()
													+ " for contractor " + conAccount.getId() + ", " + status.name());
										}
									}

								} catch (Exception e) {
									PicsLogger.log("ERROR: Error Processing Ebix for contractor " + data[0]);
									e.printStackTrace();
								}

							} else {
								PicsLogger.log("Bad Data Found : " + data);
							}
						}
					}
				} else {
					PicsLogger.log("unable to open connection: " + ftp.getReplyCode() + ":" + ftp.getReplyString());
				}
			}
		}

		ftp.logout();
		ftp.disconnect();

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
		List<Integer> questions = Arrays.<Integer> asList(604, 606, 624, 627, 630, 1437);

		for (Invoice invoice : invoices) {
			Set<String> emailAddresses = new HashSet<String>();
			ContractorAccount cAccount = (ContractorAccount) invoice.getAccount();

			if (!Strings.isEmpty(cAccount.getBillingEmail()))
				emailAddresses.add(cAccount.getBillingEmail());
			if (!Strings.isEmpty(cAccount.getCcEmail()))
				emailAddresses.add(cAccount.getCcEmail());

			if (DateBean.getDateDifference(invoice.getDueDate()) < 0) {
				if (!Strings.isEmpty(cAccount.getSecondEmail()))
					emailAddresses.add(cAccount.getSecondEmail());
			}
			if (DateBean.getDateDifference(invoice.getDueDate()) < -10) {
				List<AuditData> aList = auditDataDAO.findAnswerByConQuestions(cAccount.getId(), questions);
				for (AuditData auditData : aList) {
					if (!Strings.isEmpty(auditData.getAnswer()) && Utilities.isValidEmail(auditData.getAnswer()))
						emailAddresses.add(auditData.getAnswer());
				}
			}
			cMap.put(cAccount, emailAddresses);
		}

		for (ContractorAccount cAccount : cMap.keySet()) {
			String emailAddress = Strings.implode(cMap.get(cAccount), ",");
			emailBuilder.clear();
			emailBuilder.setTemplate(48); // **Your PICS account is will be
											// deactivated**
			emailBuilder.setContractor(cAccount);
			emailBuilder.setCcAddresses(emailAddress);
			EmailQueue email = emailBuilder.build();
			email.setPriority(30);
			emailQueueDAO.save(email);

			stampNote(cAccount, "Deactivation Email Sent to " + emailAddress, NoteCategory.Billing);
		}
	}
	
	public void sendNoActionEmailToTrialAccounts() throws Exception {
		List<ContractorAccount> conList = contractorAccountDAO.findBidOnlyContractors();
		EmailQueueDAO emailQueueDAO = (EmailQueueDAO) SpringUtils.getBean("EmailQueueDAO");
		EmailBuilder emailBuilder = new EmailBuilder();

		for (ContractorAccount cAccount : conList) {
			emailBuilder.clear();
			emailBuilder.setTemplate(70); // No Action Email Notification - Contractor 
			emailBuilder.setContractor(cAccount);
			EmailQueue email = emailBuilder.build();
			email.setPriority(30);
			emailQueueDAO.save(email);

			stampNote(cAccount, "No Action Email Notification sent to " + cAccount.getEmail(), NoteCategory.General);
		}
	}

}
