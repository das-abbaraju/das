package com.picsauditing.actions;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.cron.*;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.*;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.search.Database;
import com.picsauditing.util.*;
import com.picsauditing.util.business.OperatorUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("serial")
public class Cron extends PicsActionSupport {

    protected final static User system = new User(User.SYSTEM);
    private final Logger logger = LoggerFactory.getLogger(Cron.class);
    @Autowired
    protected ContractorAccountDAO contractorAccountDAO;
    @Autowired
    protected ContractorAuditDAO contractorAuditDAO;
    @Autowired
    protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;
    @Autowired
    protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
    @Autowired
    protected InvoiceDAO invoiceDAO;
    @Autowired
    protected OperatorAccountDAO operatorDAO;
    @Autowired
    protected UserDAO userDAO;
    @Autowired
    protected AuditBuilder auditBuilder;
    @Autowired
    protected AuditPercentCalculator auditPercentCalculator;
    protected long startTime = 0L;
    Database database = new Database();
    StringBuffer report = null;
    @Autowired
    private ContractorOperatorDAO contractorOperatorDAO;
    @Autowired
    private EmailQueueDAO emailQueueDAO;
    @Autowired
    private FlagDataOverrideDAO flagDataOverrideDAO;
    @Autowired
    private InvoiceFeeDAO invoiceFeeDAO;
    @Autowired
    private InvoiceItemDAO invoiceItemDAO;
    @Autowired
    private ReportDAO reportDAO;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private IndexerEngine indexer;
    @Autowired
    private EmailBuilder emailBuilder;
    @Autowired
    private AccountStatusChanges accountStatusChanges;
    @Autowired
    private BillingService billingService;

    private AuditBuilderAddAuditRenewalsTask auditBuilderAddAuditRenewalsTask;
    private EbixLoader ebixLoader;
    private RecalculateAuditsTask recalculateAudits;
    private IndexerTask indexerTask;
    private EmailPendingContractorsTask emailPendingContractorsTask;
    private EmailRegistrationRequestsTask emailRegistrationRequestsTask;
    private EmailUpcomingImplementationAudits emailUpcomingImplementationAudits;
    private DeclineOldPendingAccounts movePendingAccountsToDeclined;
    private AddLateFees addLateFees;
    private DeactivateNonRenewalAccounts deactivateNonRenewalAccounts;

    private void setUpTasks() {
        auditBuilderAddAuditRenewalsTask = new AuditBuilderAddAuditRenewalsTask(contractorAuditDAO, auditBuilder);
        ebixLoader = new EbixLoader(propertyDAO, contractorAuditDAO, contractorAccountDAO);
        recalculateAudits = new RecalculateAuditsTask(contractorAuditDAO, auditPercentCalculator);
        indexerTask = new IndexerTask(indexer);
        emailPendingContractorsTask = new EmailPendingContractorsTask(emailQueueDAO, contractorAccountDAO);
        emailRegistrationRequestsTask = new EmailRegistrationRequestsTask(contractorAccountDAO, emailQueueDAO, contractorRegistrationRequestDAO);
        emailUpcomingImplementationAudits = new EmailUpcomingImplementationAudits(contractorAuditDAO);
        movePendingAccountsToDeclined = new DeclineOldPendingAccounts(contractorAccountDAO, accountStatusChanges);
        addLateFees = new AddLateFees(invoiceDAO, invoiceItemDAO, invoiceFeeDAO, billingService);
        deactivateNonRenewalAccounts = new DeactivateNonRenewalAccounts(contractorAccountDAO, billingService, accountStatusChanges);
    }

    @Anonymous
    public String execute() throws Exception {
        setUpTasks();
        startReportLogging();

        auditBuilderAddAuditRenewalsTask.setEnabled(propertyDAO);
        report.append(auditBuilderAddAuditRenewalsTask.execute());

        ebixLoader.setEnabled(propertyDAO);
        report.append(ebixLoader.execute());

        recalculateAudits.setEnabled(propertyDAO);
        report.append(recalculateAudits.execute());

        indexerTask.setEnabled(propertyDAO);
        report.append(indexerTask.execute());

        emailPendingContractorsTask.setEnabled(propertyDAO);
        report.append(emailPendingContractorsTask.execute());

        emailRegistrationRequestsTask.setEnabled(propertyDAO);
        report.append(emailRegistrationRequestsTask.execute());

        emailUpcomingImplementationAudits.setEnabled(propertyDAO);
        report.append(emailUpcomingImplementationAudits.execute());

        addLateFees.setEnabled(propertyDAO);
        report.append(addLateFees.execute());

        try {
            startTask("Bump Dead Accounts that still have balances...");
            contractorAccountDAO.updateRecalculationForDeadAccountsWithBalances();
            endTask();
        } catch (Throwable t) {
            handleException(t);
        }

        deactivateNonRenewalAccounts.setEnabled(propertyDAO);
        report.append(deactivateNonRenewalAccounts.execute());

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
            startTask("Refresh Report Suggestions...");
            reportDAO.updateReportSuggestions();
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
            startTask("Checking Registration Requests Hold Dates...");
            checkRegistrationRequestsHoldDates();
            endTask();
        } catch (Throwable t) {
            handleException(t);
        }

        move90DayPendingAccountsToDeclinedStatus();

        report.append(Strings.NEW_LINE).append(Strings.NEW_LINE).append(Strings.NEW_LINE)
                .append("Completed Cron Job at: ");
        report.append(new Date().toString());

        sendEmail();

        output = "Complete";

        return SUCCESS;
    }

    private void startReportLogging() {
        HttpServletRequest request = ServletActionContext.getRequest();

        report = new StringBuffer();
        report.append("Running Cron Job on Server: " + request.getLocalName() + "\n\n");
        report.append("Address: " + request.getLocalAddr() + "\n\n");
        report.append("Cron Job initiated by: " + request.getRemoteAddr() + "\n\n");
        report.append("Starting Cron Job at: " + new Date().toString());
        report.append("\n\n\n");
    }

    @Anonymous
    public String move90DayPendingAccountsToDeclinedStatus() throws Exception {
        movePendingAccountsToDeclined.setEnabled(propertyDAO);
        report.append(movePendingAccountsToDeclined.execute());
        return SUCCESS;
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
        if (report == null) {
            report = new StringBuffer();
        }

        startTime = System.currentTimeMillis();
        report.append("Starting " + taskName + "\n\t");
    }

    protected void sendEmail() {
        String toAddress = null;
        try {
            AppProperty prop = propertyDAO.find("admin_email_address");
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

    private void stampNote(Account account, String text, NoteCategory noteCategory) {
        Note note = new Note(account, system, text);
        note.setCanContractorView(true);
        note.setPriority(LowMedHigh.High);
        note.setNoteCategory(noteCategory);
        note.setAuditColumns(system);
        note.setViewableById(Account.PicsID);
        noteDao.save(note);
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

        List<Integer> recentlyDeactivatedIds = emailQueueDAO.findContractorsWithRecentEmails("20 HOUR", 48);
        List<Integer> recentlyOpenInvoicesIds = emailQueueDAO.findContractorsWithRecentEmails("20 HOUR", 50);

        for (Invoice invoice : invoices) {
            ContractorAccount cAccount = (ContractorAccount) invoice.getAccount();
            if (invoice.getDueDate().before(new Date())) {
                if (!recentlyDeactivatedIds.contains(cAccount.getId()))
                    contractors.put(cAccount, 48); // deactivation
            } else if (!recentlyOpenInvoicesIds.contains(cAccount.getId())) {
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
                email.setSubjectViewableById(Account.PicsID);
                email.setBodyViewableById(Account.PicsID);
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
        email.setSubjectViewableById(Account.PicsID);
        email.setBodyViewableById(Account.PicsID);
        emailQueueDAO.save(email);
        stampNote(email.getContractorAccount(), "Failed to send Deactivation Email because of no valid email address.",
                NoteCategory.Billing);
    }

    public void sendNoActionEmailToTrialAccounts() throws Exception {
        List<ContractorAccount> conList = contractorAccountDAO.findBidOnlyContractors();

        for (ContractorAccount cAccount : conList) {
            EmailBuilder emailBuilder = new EmailBuilder();

            emailBuilder.setTemplate(EmailTemplate.NO_ACTION_EMAIL_TEMPLATE);
            // No Action Email Notification - Contractor
            emailBuilder.setContractor(cAccount, OpPerms.ContractorAdmin);
            emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
            EmailQueue email = emailBuilder.build();
            email.setLowPriority();
            email.setSubjectViewableById(Account.EVERYONE);
            email.setBodyViewableById(Account.EVERYONE);
            emailQueueDAO.save(email);

            stampNote(cAccount, "No Action Email Notification sent to " + cAccount.getPrimaryContact().getEmail(),
                    NoteCategory.General);
        }
    }

    public void expireFlagChanges() throws Exception {

        String query = "UPDATE contractor_operator co ";
        query += "JOIN accounts a ON co.conID = a.id ";
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
        query += "OR opID IN (10403,2723) ";
        // Ignore Flag Changes for newly created contractors
        query += "OR a.creationDate >= DATE_SUB(NOW(), INTERVAL 2 WEEK) ";
        // Ignore Flag Changes for recently added contractors
        query += "OR co.creationDate >= DATE_SUB(NOW(), INTERVAL 2 WEEK) ";
        // Ignore Clear Flag Changes
        query += "OR flag = 'Clear' OR baselineFlag = 'Clear'";
        // Removed Forced Overall Flags
        query += "OR (forceFlag IS NOT NULL AND NOW() < forceEnd))";

        getDatabase().executeUpdate(query);
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
        emailBuilder.setTemplate(EmailTemplate.FLAG_CHANGES_EMAIL_TEMPLATE);
        emailBuilder.setFromAddress(EmailAddressUtils.PICS_SYSTEM_EMAIL_ADDRESS);
        emailBuilder.addToken("changes", flagChanges);
        int totalFlagChanges = sumFlagChanges(flagChanges);
        emailBuilder.addToken("totalFlagChanges", totalFlagChanges);
        emailBuilder.setToAddresses(accountMgr);
        EmailQueue email = emailBuilder.build();
        email.setVeryHighPriority();
        email.setSubjectViewableById(Account.PicsID);
        email.setBodyViewableById(Account.PicsID);
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
        query.append("count(*) total, sum(case when co.flag = co.baselineFlag THEN 0 ELSE 1 END) changes ");
        query.append("from contractor_operator co ");
        query.append("join accounts c on co.conID = c.id and c.status = 'Active' ");
        query.append("join accounts o on co.opID = o.id and o.status = 'Active' and o.type = 'Operator' and o.id not in ("
                + Strings.implode(OperatorUtil.operatorsIdsUsedForInternalPurposes()) + ") ");
        query.append("LEFT join account_user au on au.accountID = o.id and au.role = 'PICSAccountRep' and startDate < now() ");
        query.append("and endDate > now() ");
        query.append("LEFT join users u on au.userID = u.id ");
        query.append("group by o.id) t ");
        query.append("where changes >= 10 and changes/total > .05 ");
        query.append("order by percent desc ");

        List<BasicDynaBean> data = getDatabase().select(query.toString(), true);
        return data;
    }

    private Database getDatabase() {
        return database;
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
            noteDao.save(note);

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
            noteDao.save(note);

            override.setForceEnd(null);
            override.setForceFlag(null);
            override.setForceBegin(null);
            override.setForcedBy(null);

            contractorOperatorDAO.save(override);
        }
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

    public String getReport() {
        return report.toString();
    }
}