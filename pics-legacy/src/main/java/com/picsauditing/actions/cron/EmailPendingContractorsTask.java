package com.picsauditing.actions.cron;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmailPendingContractorsTask implements CronTask {
    protected Permissions permissions = null;
    List<String> emailExclusionList = new ArrayList<>();
    @Autowired
    EmailQueueDAO emailQueueDAO;
    @Autowired
    ContractorAccountDAO contractorAccountDAO;
    @Autowired
    EmailDuplicateContractors duplicateContractors;

    public String getDescription() {
        return "Email Blast: pending contractors";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        emailExclusionList = emailQueueDAO.findEmailAddressExclusions();
        steps.add("Excluding " + emailExclusionList.size() + " emails");

        String where = "a.country IN ('US','CA') AND (c.lastContactedByAutomatedEmailDate != CURDATE() OR c.lastContactedByAutomatedEmailDate IS NULL) AND ";
        String whereReminder = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 DAY)";
        String whereLastChance = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 3 WEEK)";
        String whereFinal = where + "DATE(a.creationDate) = DATE_SUB(CURDATE(),INTERVAL 1 MONTH)";

        List<ContractorAccount> contractors = new ArrayList<>();
        contractors.addAll(contractorAccountDAO.findPendingAccounts(whereReminder));
        contractors.addAll(contractorAccountDAO.findPendingAccounts(whereLastChance));
        contractors.addAll(contractorAccountDAO.findPendingAccounts(whereFinal));
        for (ContractorAccount contractorAccount : contractors) {
            steps.add("Will spam " + contractorAccount.getName() + " (" + contractorAccount.getId() + ")");
        }

        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        try {
            emailExclusionList = emailQueueDAO.findEmailAddressExclusions();
            sendEmailPendingAccounts();
        } catch (Exception e) {
            results.setSuccess(false);
            results.getLogger().append(e.getMessage());
        }
        return results;
    }

    void sendEmailPendingAccounts() throws Exception {
        String exclude = Strings.implodeForDB(emailExclusionList);

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
            throws EmailException, IOException, ParseException, EmailBuildErrorException {
        Map<OperatorAccount, List<ContractorAccount>> operatorContractors = new HashMap<>();

        removeContractorsWithRecentlySentEmail(list, templateID);

        for (ContractorAccount contractor : list) {
            if (contractor.getPrimaryContact() != null
                    && !emailExclusionList.contains(contractor.getPrimaryContact().getEmail())) {

                if (templateID != EmailTemplate.pendingFinalEmailTemplate || !duplicateContractors.duplicationCheck(contractor, contractor.getNameIndex())) {
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
                    email.setSubjectViewableById(Account.EVERYONE);
                    email.setBodyViewableById(Account.EVERYONE);
                    emailQueueDAO.save(email);

                    // update the contractor notes
                    stampNote(contractor, newNote + emailBuilder.getSentTo(), NoteCategory.Registration);
                    if (templateID == EmailTemplate.pendingFinalEmailTemplate) {
                        if (operatorContractors.get(requestedByOperator) == null) {
                            operatorContractors.put(requestedByOperator, new ArrayList<ContractorAccount>());
                        }

                        operatorContractors.get(requestedByOperator).add(contractor);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.Iso);
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
                email.setSubjectViewableById(Account.EVERYONE);
                email.setBodyViewableById(Account.EVERYONE);
                emailQueueDAO.save(email);

                // update the notes
                stampNote(operator, "Contractor Pending Account expired. Client site was notified at this address: "
                        + emailBuilder.getSentTo(), NoteCategory.Registration);
            }
        }
    }

    private void removeContractorsWithRecentlySentEmail(List<ContractorAccount> list, int templateID) {
        if (list.size() == 0)
            return;

        List<Integer> ids = emailQueueDAO.findContractorsWithRecentEmails("20 HOUR", templateID);
        if (ids.size() == 0)
            return;

        Iterator<ContractorAccount> iterator = list.iterator();
        while (iterator.hasNext()) {
            ContractorAccount contractor = iterator.next();
            if (ids.contains(contractor.getId())) {
                list.remove(contractor);
//                iterator.remove();
            }
        }
    }

    private void stampNote(Account account, String text, NoteCategory noteCategory) {
        Note note = new Note(account, new User(User.SYSTEM), text);
        note.setCanContractorView(true);
        note.setPriority(LowMedHigh.High);
        note.setNoteCategory(noteCategory);
        note.setAuditColumns(new User(User.SYSTEM));
        note.setViewableById(Account.PicsID);
        emailQueueDAO.save(note);
    }

}
