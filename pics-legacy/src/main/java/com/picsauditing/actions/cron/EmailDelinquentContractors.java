package com.picsauditing.actions.cron;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EmailDelinquentContractors extends CronTask {
    private static String NAME = "EmailDelinquentContractors";
    private ContractorAccountDAO contractorAccountDAO;
    private EmailQueueDAO emailQueueDAO;

    public EmailDelinquentContractors(ContractorAccountDAO contractorAccountDAO, EmailQueueDAO emailQueueDAO) {
        super(NAME);
        this.contractorAccountDAO = contractorAccountDAO;
        this.emailQueueDAO = emailQueueDAO;
    }

    protected void run() throws Exception {
        List<Invoice> pendingAndDelinquentInvoices = contractorAccountDAO.findPendingDelinquentAndDelinquentInvoices();
        if (!pendingAndDelinquentInvoices.isEmpty()) {
            Map<ContractorAccount, Integer> pendingAndDelinquentAccts = splitPendingAndDeliquentInvoices(pendingAndDelinquentInvoices);
            sendEmailsTo(pendingAndDelinquentAccts);
        }
    }

    Map<ContractorAccount, Integer> splitPendingAndDeliquentInvoices(List<Invoice> invoices) {
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

    void sendEmailsTo(Map<ContractorAccount, Integer> pendingAndDelinquentAccts) {
        for (ContractorAccount cAccount : pendingAndDelinquentAccts.keySet()) {
            try {
                int templateID = pendingAndDelinquentAccts.get(cAccount);
                EmailBuilder emailBuilder = new EmailBuilder();
                emailBuilder.setTemplate(templateID);
                emailBuilder.setContractor(cAccount, OpPerms.ContractorBilling);

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

    private void stampNote(Account account, String text, NoteCategory noteCategory) {
        Note note = new Note(account, new User(User.SYSTEM), text);
        note.setCanContractorView(true);
        note.setPriority(LowMedHigh.High);
        note.setNoteCategory(noteCategory);
        note.setAuditColumns(new User(User.SYSTEM));
        note.setViewableById(Account.PicsID);
        contractorAccountDAO.save(note);
    }


}
