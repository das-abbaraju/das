package com.picsauditing.actions;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.cron.*;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.search.Database;
import com.picsauditing.util.EbixLoader;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    private EmailDelinquentContractors emailDelinquentContractors;
    private FlagChangesEmailTask flagChangesEmailTask;
    private ReportSuggestionsTask reportSuggestionsTask;
    private ExpireFlagChangesTask expireFlagChangesTask;

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
        emailDelinquentContractors = new EmailDelinquentContractors(contractorAccountDAO, emailQueueDAO);
        flagChangesEmailTask = new FlagChangesEmailTask(getDatabase(), emailQueueDAO);
        reportSuggestionsTask = new ReportSuggestionsTask(getDatabase());
        expireFlagChangesTask = new ExpireFlagChangesTask(getDatabase());
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

        emailDelinquentContractors.setEnabled(propertyDAO);
        report.append(emailDelinquentContractors.execute());

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

        expireFlagChangesTask.setEnabled(propertyDAO);
        report.append(expireFlagChangesTask.execute());

        reportSuggestionsTask.setEnabled(propertyDAO);
        report.append(reportSuggestionsTask.execute());

        flagChangesEmailTask.setEnabled(propertyDAO);
        report.append(flagChangesEmailTask.execute());

        try {
            startTask("Checking Registration Requests Hold Dates...");
            checkRegistrationRequestsHoldDates();
            endTask();
        } catch (Throwable t) {
            handleException(t);
        }

        movePendingAccountsToDeclined.setEnabled(propertyDAO);
        report.append(movePendingAccountsToDeclined.execute());

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