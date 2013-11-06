package com.picsauditing.actions.cron;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmailRegistrationRequestsTask implements CronTask {
    protected List<String> emailExclusionList;
    protected Permissions permissions = null;
    @Autowired
    private EmailQueueDAO emailQueueDAO;
    @Autowired
    private ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
    @Autowired
    private EmailDuplicateContractors duplicateContractors;
    // this.duplicateContractors = new EmailDuplicateContractors(contractorAccountDAO, emailQueueDAO);

    public String getDescription() {
        return "TODO";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() throws Exception {
        CronTaskResult results = new CronTaskResult(true, "");
        emailExclusionList = emailQueueDAO.findEmailAddressExclusions();
        results.getLogger().append("Excluding " +
                emailExclusionList.size() + " emails");
        sendEmailContractorRegistrationRequest();
        return results;
    }

    private void sendEmailContractorRegistrationRequest() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(PicsDateFormat.Iso);

        int[] pendingEmailTemplates = {EmailTemplate.pendingFinalEmailTemplate, EmailTemplate.pendingLastChanceEmailTemplate,
                EmailTemplate.pendingReminderEmailTemplate};

        List<String> emailsAlreadySentToPending = emailQueueDAO.findPendingActivationEmails("1 MONTH",
                pendingEmailTemplates);
        emailExclusionList.addAll(emailsAlreadySentToPending);

        String excludedEmails = Strings.implodeForDB(emailExclusionList);
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
            ParseException, EmailBuildErrorException {
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
                if (templateID != EmailTemplate.pendingFinalEmailTemplate || !duplicateContractors.duplicationCheck(requestedContractor, requestName)) {
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
                    email.setSubjectViewableById(Account.EVERYONE);
                    email.setBodyViewableById(Account.EVERYONE);
                    emailQueueDAO.save(email);

                    // update the registration request
                    if (requestedContractor instanceof ContractorAccount) {
                        ContractorAccount request = (ContractorAccount) requestedContractor;

                        request.contactByEmail();

                        Note note = new Note(request, new User(User.SYSTEM), newNote);
                        note.setNoteCategory(NoteCategory.Registration);
                        note.setCanContractorView(true);
                        note.setViewableById(Account.PicsID);
                        emailQueueDAO.save(note);

                        request.setLastContactedByAutomatedEmailDate(new Date());
                    } else if (requestedContractor instanceof ContractorRegistrationRequest) {
                        ContractorRegistrationRequest request = (ContractorRegistrationRequest) requestedContractor;

                        request.contactByEmail();
                        request.addToNotes(newNote, new User(User.SYSTEM));
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
                email.setSubjectViewableById(Account.EVERYONE);
                email.setBodyViewableById(Account.EVERYONE);
                emailQueueDAO.save(email);

                stampNote(
                        operatorUser.getAccount(),
                        "Registration Request has expired. Client site was notified at this address: "
                                + emailBuilder.getSentTo(), NoteCategory.Registration);
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
