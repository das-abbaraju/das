package com.picsauditing.actions.cron;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmailDelinquentContractors implements CronTask {
    @Autowired
    ContractorAccountDAO contractorAccountDAO;
    @Autowired
    EmailQueueDAO emailQueueDAO;

    public String getDescription() {
        return "Email Blast: almost delinquent contractors";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<Invoice> pendingAndDelinquentInvoices = contractorAccountDAO.findPendingDelinquentAndDelinquentInvoices();
        if (!pendingAndDelinquentInvoices.isEmpty()) {
            Map<ContractorAccount, Integer> pendingAndDelinquentAccts = splitPendingAndDeliquentInvoices(pendingAndDelinquentInvoices);
            for (ContractorAccount contractorAccount : pendingAndDelinquentAccts.keySet()) {
                steps.add("Will spam " + contractorAccount.getName() + " (" + contractorAccount.getId() + ")");
            }
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult();
		try {
			List<Invoice> pendingAndDelinquentInvoices = contractorAccountDAO.findPendingDelinquentAndDelinquentInvoices();
			if (!pendingAndDelinquentInvoices.isEmpty()) {
				Map<ContractorAccount, Integer> pendingAndDelinquentAccts = splitPendingAndDeliquentInvoices(pendingAndDelinquentInvoices);
				sendEmailsTo(pendingAndDelinquentAccts);
			}
			results.setSuccess(true);
		} catch (Exception exception) {
			results.setSuccess(false);
			results.getLogger().append(exception.getMessage()).append(exception.getCause());
		}
        return results;
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
